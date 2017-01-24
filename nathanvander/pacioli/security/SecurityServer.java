package nathanvander.pacioli.security;
import nathanvander.pacioli.iface.*;
import nathanvander.pacioli.table.*;
import apollo.iface.*;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;
import java.math.BigInteger;

public class SecurityServer implements Security {
	DataStore ds;

	//create a new Security server, given the datastore
	public SecurityServer(DataStore ds) throws DataStoreException, RemoteException {

		//first create the necessary tables, _security and _user
		Transaction tx=ds.createTransaction();
		System.out.println("creating _security and _user tables");
		tx.begin();
		tx.createTable(new SecurityTable());
		tx.createTable(new User());
		tx.commit();

		//first see if this is already setup
		int r=ds.rows("_security");

		if (r==0) {
			//generate 16-bit security keys
			SecurityTable st=generateKeys(16);
			PublicKey pk=new PublicKey(st.public_key);

			//create root user
			int rootPassword=1234;	//yep hard-coded
			SecurityToken rootToken=ClientSecurity.encrypt(pk,"root",rootPassword);
			User root=new User();
			root.username="root";
			root.firstname="System";
			root.lastname="Administrator";
			root.password=rootToken.password;

			//store security keys and root
			Transaction tx2=ds.createTransaction();
			tx2.begin();
			Key k = tx2.insert(st);
			if (k.rowid!=1L) {
				System.out.println("WARNING: New Security has a rowid of "+k.rowid);
			}
			Key k2=tx2.insert(root);
			if (k2.rowid!=1L) {
				System.out.println("WARNING: New root user has a rowid of "+k2.rowid);
			}
			tx2.commit();
		}
	}

	//now generate the initial keys
	//make sure password is encrypted
	protected static SecurityTable generateKeys(int numBits) {
		if (numBits<16 || numBits>128) {
			throw new IllegalArgumentException("numBits must be in the range 16..128");
		}

		BigInteger P = randomPrime(numBits);
		//I make Q a little bigger.  We could use the same number of bits since
		//it is random
		BigInteger Q = randomPrime(numBits+1);
		BigInteger N = P.multiply(Q);
		BigInteger pm=P.subtract(BigInteger.ONE);
		BigInteger qm=Q.subtract(BigInteger.ONE);
		BigInteger F = pm.multiply(qm);
		long E = 65537L;
		BigInteger D = calculateDecryptionKey(F,BigInteger.valueOf(E));

		SecurityTable st=new SecurityTable();
		//we really shouldn't expose these
		//but I'm quite proud of this, it's a way to check the calculations
		//and 99% of users won't know what it means
		//and the other 1% could hack in anyways
		st.p_prime=P.toString(16);
		st.q_prime=Q.toString(16);
		st.public_key=N.toString(16);
		st.private_key=D.toString(16);
		return st;

	}

	private static BigInteger randomPrime(int numBits) {
		return BigInteger.probablePrime(numBits,new Random());
	}

	/**
	* X in the formula here is any number that will work for integer division by E.
	*	It must be discovered by trial and error.
	* 	Formula: D =(F*X+1)/E
	*
	* This could take a minute or so
	*/
	private static BigInteger calculateDecryptionKey(BigInteger f,BigInteger e) {
		//System.out.println("calculating decryption key.  this could take a minute");
		//System.out.println("e = "+e.toString());
		//System.out.println("f = "+f.toString());

		int x=1;
		//put an upper limit on this. hopefully the number won't be too big
		while(x<1000000000) {
			BigInteger bx=BigInteger.valueOf(x);
			//sub = f * x + 1
			BigInteger subtotal=f.multiply(bx).add(BigInteger.ONE);
			//m = sub mod e
			BigInteger m = subtotal.mod(e);
			//System.out.println("for x = "+bx.toString()+", f*x+1 = "+subtotal.toString()+", mod e = "+ m.toString());
			if (m.equals(BigInteger.ZERO)) {
				//we have a winner
				return subtotal.divide(e);
			}
			x++;
		}
		//shouldn't happen
		throw new IllegalStateException("can't find decryption key");
	}


	//===========================================

	public PublicKey getPublicKey() throws RemoteException, DataStoreException {
		//we are assuming the security information is in the first row.
		SecurityTable st=(SecurityTable)ds.get(new Key("_security",1L));
		return new PublicKey(st.public_key);
	}

	//also check if not disabled
	public boolean validate(SecurityToken token) throws RemoteException,DataStoreException {
		boolean valid=false;
		Cursor it=ds.selectWhere(new User(),"WHERE username='"+token.username+"' AND disabled='false'");
		it.open();
		if (it.hasNext()) {
			User u=(User)it.next();
			if (u.password.equals(token.password)) {
				valid=true;
			}
		}
		it.close();
		return valid;
	}

	private boolean validateAdmin(SecurityToken admin) throws RemoteException,DataStoreException {
		if (admin==null) {return false;}
		if (!admin.username.equals("root")) {
			return false; //failure
		}
		//validate admin
		return validate(admin);
	}


	//change the password for the user
	public boolean changePassword(SecurityToken oldToken,SecurityToken newToken) throws RemoteException,DataStoreException {
		//check 1: the usernames must be the same
		if (!oldToken.username.equals(newToken.username)) {
			System.out.println("DEBUG: can't change password because usernames don't match");
			return false;
		}

		//check 2: validate old token
		//we could use the validate code above, but we need to keep the user object
		boolean valid=false;
		User u=null;

		Cursor it=ds.selectWhere(new User(),"WHERE username='"+oldToken.username+"'");
		it.open();
		if (it.hasNext()) {
			u=(User)it.next();
			if (u.password.equals(oldToken.password)) {
				valid=true;
			}
		}
		it.close();
		if (!valid) {
			System.out.println("DEBUG: can't change password because old token is invalid");
			return false;
		}

		//ok, we are good, assuming newtoken is not null
		//first clone old user
		User oldUser=u.clone();

		//now update the fields that changed
		//should only be password, right?
		u.password=newToken.password;

		//now update it
		Transaction t=ds.createTransaction();
		t.update(oldUser,u);
		t.commit();

		//looks good
		return true;
	}

	//return userid if success
	//this will do an insert.  I am not checking if it already exists
	public int addUser(SecurityToken admin,SecurityToken newUser) throws RemoteException, DataStoreException {
		//first validate admin
		boolean b=validateAdmin(admin);
		if (!b) {return 0;} //failure

		//passed security check you can add it, assuming it doesn't already exist
		//but i am not checking for duplicates now
		User u=new User();
		u.username=newUser.username;
		u.password=newUser.password;

		Transaction t=ds.createTransaction();
		Key k=t.insert(u);
		t.commit();
		return (int)k.rowid;
	}

	//user must already exist at this point
	//also re-enable user if disabled
	public boolean changePasswordForUser(SecurityToken admin, SecurityToken newToken) throws RemoteException,DataStoreException {
		boolean b=validateAdmin(admin);
		if (!b) {return false;} //failure

		//passed security checks.
		//get old user object
		User u=null;

		Cursor it=ds.selectWhere(new User(),"WHERE username='"+newToken.username+"'");
		it.open();
		if (it.hasNext()) {
			u=(User)it.next();
		}
		it.close();
		if (u==null) {
			return false;	//failure, user does not exist
		}

		//clone it
		User oldUser=u.clone();

		//change the values
		u.password=newToken.password;
		u.disabled=false;

		//save it
		Transaction t=ds.createTransaction();
		t.update(oldUser,u);
		t.commit();

		//looks good
		return true;
	}

	public boolean lockOutUser(SecurityToken admin,String username) throws RemoteException, DataStoreException {
		boolean b=validateAdmin(admin);
		if (!b) {return false;} //failure

		//get user
		User u=null;
		Cursor it=ds.selectWhere(new User(),"WHERE username='"+username+"'");
		it.open();
		if (it.hasNext()) {
			u=(User)it.next();
		}
		it.close();
		if (u==null) {
			return false;	//failure, user does not exist
		}

		//clone it
		User oldUser=u.clone();

		//change the values
		u.disabled=true;

		//save it
		Transaction t=ds.createTransaction();
		t.update(oldUser,u);
		t.commit();

		//looks good
		return true;
	}

	//return null upon failure
	public String[] listUsers(SecurityToken admin) throws RemoteException, DataStoreException {
		boolean b=validateAdmin(admin);
		if (!b) {return null;} //failure

		//get the number of rows in the table
		int rows=ds.rows("_user");
		String[] users=new String[rows];

		Cursor it=ds.selectAll(new User());
		it.open();
		int i=0;
		while (it.hasNext()) {
			User u=(User)it.next();
			users[i]=u.username;
			i++;
		}
		it.close();
		return users;
	}
	//====================
	//start this up and add it to the registry
	public static void main(String[] args) {
		//for this test, this is in a different process from apollo
		String host=args[0];
		if (host==null) {host="localhost";}
        try {
            Registry registry = LocateRegistry.getRegistry(host);

			//get the datastore
			DataStore ds = (DataStore) registry.lookup("DataStore");

            SecurityServer ss = new SecurityServer(ds);
            Security stub = (Security)UnicastRemoteObject.exportObject(ss, 0);

            registry.rebind("Security", stub);
		} catch (Exception x) {
			System.out.println(x.getMessage());
			System.out.println("failed to start SecurityServer");
		}
	}
}
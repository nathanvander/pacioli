package pacioli2;
import apollo.iface.*;
import java.util.Vector;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import pacioli2.table.*;
import apollo.util.DateYMD;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
* An AccountingTransaction is a wrapper around a database transaction.
* Note that the database transaction has an id which restarts when the server restarts, whereas this
* transaction id comes from the sequence table
*/
public class AccountingTransaction {
	DataStore ds;
	Transaction tx;
	String txnid;
	Vector entries;	//there are probably just 2, but unlimited are possible
	Txn t;			//this has metadata about the transaction itself, like the date

	public AccountingTransaction(DataStore ds) throws RemoteException, DataStoreException {
		this.ds=ds;
		txnid=ds.nextId("txn");
		entries=new Vector();
		t=new Txn();
		t.date=new DateYMD();
		t.time=getTime();
		t.id=txnid;
	}

	//mostly for debugging purposes
	public String getId() {return txnid;}

	//set a different transaction date than today
	public void setDate(DateYMD date) {
		if (date!=null) {t.date=date;}
	}

	//if you change the transaction date, you should change this as well
	//it actually can be null with no problem
	public void setTime(String time) {
		t.time=time;
	}

	//source could be the journal
	public void setSource(String source) {
		t.source=source;
	}

	//associate this with a given VOS activity
	//if you aren't sure what this is, don't touch it
	//the input must be a number in base-12
	public void setActivityKey(String k) {
		t.activity=k;
	}

	//describe the transaction
	public void setDescription(String d) {
		t.desc=d;
	}

	//------------------------------------------------------
	public void begin() throws RemoteException, DataStoreException {
		//defer creating the underlying transaction until we begin
		tx=ds.createTransaction();
		tx.begin();
	}

	/**
	* This both rolls back the transaction and closes the connection.
	*/
	public void rollback() throws RemoteException, DataStoreException {
		tx.rollback();
	}

	/**
	* This both commits the transaction and closes the connection.
	*/
	public void commit() throws RemoteException, DataStoreException, OutOfBalanceException {
		if (!checkBalance()) {
			rollback();
			throw new OutOfBalanceException();
		} else {
			//insert the Txn
			tx.insert(t);

			//now add the entries
			for (int i=0;i<entries.size();i++) {
				Entry e=(Entry)entries.elementAt(i);
				tx.insert(e);
			}
			tx.commit();
		}
	}



	//--------------------------------------------
	//add an entry to the transaction
	//the entry must have the account number (and sub, if applicable), and the debit or credit
	//we will add the txn id
	//you can actually add this before you begin()
	public void add(Entry e) {
		e.txnid=txnid;
		//add it to the list
		entries.add(e);
	}

	//check to see if the transaction is in balance
	//this won't throw an exception if you call it here, but it will if you try to commit
	public boolean checkBalance() {
		BigDecimal debits=new BigDecimal(0.0,Entry.FORMAT);
		BigDecimal credits=new BigDecimal(0.0,Entry.FORMAT);
		for (int i=0;i<entries.size();i++) {
			Entry e=(Entry)entries.elementAt(i);
			debits=debits.add(e.debit);
			credits=credits.add(e.credit);
		}
		int b=debits.compareTo(credits);
		return b==0;
	}

	//get the time now in HH:mm format
	public static String getTime() {
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
		return timeFormat.format(new Date());
	}

}
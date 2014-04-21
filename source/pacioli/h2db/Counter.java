package pacioli.h2db;
import pacioli.util.Base60;
import pacioli.db.DSX;
import java.io.*;
import java.sql.*;

/**
* This is a wrapper around a sequence number.
* Creates a table called "sequence".
*
* Any method that involves the database and is called by an external thread must be synchronized.
* The init method, or methods called only by it, should be called only once, when the database is initialized.
*/

public class Counter {
	int nextId;
	char[] nextVal;  //nextVal has 8 characters
	//note that the connection is not cached

	/**
	* there must be only 1 counter per database, because this caches the sequence numbers
	*/
	public Counter() {}

	//----------------------------------------------
	//methods associated with init()
	//
	/**
	* this is not in the constructor because SQLiteConnection isn't reused, it is only used to init
	* connection must already be open
	* we have no idea if the table exists at this point
	*
	* This should only be called once when the database is initialized
	*/
	public synchronized void init(Connection conn) throws DSX {
		nextVal=new char[8];

		if (!sequenceTableExists(conn)) {
			createSequenceTable(conn);
			nextId=1;
			String v=Base60.base60String(nextId);
			copyVal(v);

			insertKey(conn,nextId,v);
		} else {
			nextId=getNextId(conn);
			String nval=getNextVal(conn);
			copyVal(nval);

			//make sure they are in-sync
			String base60=Base60.base60String(nextId);
			String val=String.valueOf(nextVal).trim();
			if (!base60.equals(val)) {
				String err=base60+" != "+val;
				throw new DSX(ErrorCode.PACIOLI_DB_COUNTER,err);
			}
		}
	}

	//we would prefer to have this lower case, but the Select needs to be in upper case
	//it seems like this is unnecessary to check if it exists, but I would like to go ahead and check it
	private synchronized boolean sequenceTableExists(Connection conn) throws DSX {
		String sql="SELECT COUNT(*) AS count FROM information_schema.tables WHERE upper(table_name) = 'SEQUENCE'";
		//String sql="SELECT name FROM sqlite_master WHERE type = 'table' AND name = 'sequence'";
		System.out.println(sql);
		try {
			Statement stmt=conn.createStatement();
			ResultSet rs=stmt.executeQuery(sql);
			rs.next();
			int count=rs.getInt(1);
			stmt.close();
			return (count==1);
		} catch (SQLException x) {
			throw new DSX(x.getErrorCode(),x.getMessage());
		}
	}

	private synchronized void createSequenceTable(Connection conn) throws DSX {
		//create sequence table
		String sql="CREATE TABLE IF NOT EXISTS sequence (name VARCHAR, nextid INT, nextval VARCHAR)";
		System.out.println(sql);
		try {
			Statement st=conn.createStatement();
			st.execute(sql);
			st.close();
		} catch (SQLException x) {
			throw new DSX(x.getErrorCode(),x.getMessage());
		}
	}

	private synchronized void insertKey(Connection conn,int id,String val) throws DSX {
		//insert the key sequence
		String sql="INSERT INTO sequence (name,nextid,nextval) VALUES ('key',"+id+",'"+val+"')";
		System.out.println(sql);
		try {
			Statement st=conn.createStatement();
			st.execute(sql);
			st.close();
		} catch (SQLException x) {
			throw new DSX(x.getErrorCode(),x.getMessage());
		}
	}

	//not that this just returns the nextId and doesn't increment it
	//that is why this is private
	private synchronized int getNextId(Connection conn) throws DSX {
		String sql="SELECT nextid FROM sequence WHERE name = 'key'";
		System.out.println(sql);
		boolean b=false;
		try {
			Statement stmt=conn.createStatement();
			ResultSet rs=stmt.executeQuery(sql);
			b=rs.next();
			if (b) {
				//int nid=stmt.columnInt(0);
				int nid=rs.getInt(1);
				stmt.close();
				return nid;
			} else {
				throw new DSX(ErrorCode.PACIOLI_DB_COUNTER,"no nextid");
			}
		} catch (SQLException x) {
			throw new DSX(x.getErrorCode(),x.getMessage());
		}
	}

	//this is a separate method from getNextId because it returns a different datatype
	//it is only called once upon startup
	private synchronized String getNextVal(Connection conn) throws DSX {
		String sql="SELECT nextval FROM sequence WHERE name = 'key'";
		System.out.println(sql);
		boolean b=false;
		try {
			Statement stmt=conn.createStatement();
			ResultSet rs=stmt.executeQuery(sql);
			b=rs.next();
			if (b) {
				//String nv=stmt.columnString(0);
				String nv=rs.getString(1);
				stmt.close();
				return nv;
			} else {
				throw new DSX(ErrorCode.PACIOLI_DB_COUNTER,"no nextval");
			}
		} catch (SQLException x) {
			throw new DSX(x.getErrorCode(),x.getMessage());
		}
	}

	//copy String to nextVal
	private void copyVal(String v) {
		char[] nval=v.toCharArray();
		System.arraycopy(nval,0,nextVal,8-nval.length,nval.length);
	}

	//-----------------------------------------------
	//methods associated with nextKey()
	//
	/**
	* Returns the nextVal as a string
	*
	* This method is heavily called, which is everytime an object is inserted
	* into the database.
	*/
	public synchronized String nextKey(Connection conn) throws DSX {
		String key=String.valueOf(nextVal).trim();
		nextId++;
		Base60.increment(nextVal);
		String nextKey=String.valueOf(nextVal).trim();
		updateKey(conn,nextId,nextKey);
		return key;
	}

	private synchronized void updateKey(Connection conn,int nextId,String nextKey) throws DSX {
		String sql="UPDATE sequence SET nextid="+nextId+",nextval='"+nextKey+"' WHERE name = 'key'";
		System.out.println(sql);
		try {
			Statement stmt=conn.createStatement();
			stmt.execute(sql);
			stmt.close();
		} catch (SQLException x) {
			throw new DSX(x.getErrorCode(),x.getMessage());
		}
	}

	//===================================
	//stand alone test
	public static void main(String[] args) throws DSX, SQLException {

		String fileName="pacioli";
		try {
		Class.forName("org.h2.Driver");
		} catch (ClassNotFoundException x) {
			throw new DSX(ErrorCode.JAVA_LANG_CLASSNOTFOUND,x.getMessage());
		}
		String uri="jdbc:h2:./"+fileName;
        Connection conn = DriverManager.getConnection(uri);
		Counter c=new Counter();
		c.init(conn);
		System.out.println("nextKey="+String.valueOf(c.nextVal).trim());

		for (int i=0;i<7;i++) {
			String key=c.nextKey(conn);
			System.out.println("assigned key="+key);
		}
		conn.close();
	}
}
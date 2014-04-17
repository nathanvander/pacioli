package pacioli.db;
import pacioli.util.Base60;
import com.almworks.sqlite4java.*;
import java.io.*;

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
	public synchronized void init(SQLiteConnection conn) throws DSX {
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

	private synchronized boolean sequenceTableExists(SQLiteConnection conn) throws DSX {
		String sql="SELECT name FROM sqlite_master WHERE type = 'table' AND name = 'sequence'";
		System.out.println(sql);
		try {
			SQLiteStatement stmt = conn.prepare(sql);
			return stmt.step();
		} catch (SQLiteException x) {
			throw new DSX(x.getErrorCode(),x.getMessage());
		}
	}

	private synchronized void createSequenceTable(SQLiteConnection conn) throws DSX {
		//create sequence table
		String sql="CREATE TABLE IF NOT EXISTS sequence (name TEXT, nextid INT, nextval TEXT)";
		System.out.println(sql);
		try {
			conn.exec(sql);
		} catch (SQLiteException x) {
			throw new DSX(x.getErrorCode(),x.getMessage());
		}
	}

	private synchronized void insertKey(SQLiteConnection conn,int id,String val) throws DSX {
		//insert the key sequence
		String sql="INSERT INTO sequence (name,nextid,nextval) VALUES ('key',"+id+",'"+val+"')";
		System.out.println(sql);
		try {
			conn.exec(sql);
		} catch (SQLiteException x) {
			throw new DSX(x.getErrorCode(),x.getMessage());
		}
	}

	//not that this just returns the nextId and doesn't increment it
	private synchronized int getNextId(SQLiteConnection conn) throws DSX {
		String sql="SELECT nextid FROM sequence WHERE name = 'key'";
		System.out.println(sql);
		boolean b=false;
		try {
			SQLiteStatement stmt = conn.prepare(sql);
			b=stmt.step();
			if (b) {
				int nid=stmt.columnInt(0);
				return nid;
			} else {
				throw new DSX(ErrorCode.PACIOLI_DB_COUNTER,"no nextid");
			}
		} catch (SQLiteException x) {
			throw new DSX(x.getErrorCode(),x.getMessage());
		}
	}

	private synchronized String getNextVal(SQLiteConnection conn) throws DSX {
		String sql="SELECT nextval FROM sequence WHERE name = 'key'";
		System.out.println(sql);
		boolean b=false;
		try {
			SQLiteStatement stmt = conn.prepare(sql);
			b=stmt.step();
			if (b) {
				String nv=stmt.columnString(0);
				return nv;
			} else {
				throw new DSX(ErrorCode.PACIOLI_DB_COUNTER,"no nextval");
			}
		} catch (SQLiteException x) {
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
	public synchronized String nextKey(SQLiteConnection conn) throws DSX {
		String key=String.valueOf(nextVal).trim();
		nextId++;
		Base60.increment(nextVal);
		String nextKey=String.valueOf(nextVal).trim();
		updateKey(conn,nextId,nextKey);
		return key;
	}

	private synchronized void updateKey(SQLiteConnection conn,int nextId,String nextKey) throws DSX {
		String sql="UPDATE sequence SET nextid="+nextId+",nextval='"+nextKey+"' WHERE name = 'key'";
		System.out.println(sql);
		try {
			conn.exec(sql);
		} catch (SQLiteException x) {
			throw new DSX(x.getErrorCode(),x.getMessage());
		}
	}

	//===================================
	public static void main(String[] args) throws DSX, SQLiteException {
		//turn off logging
		//Logger logger = Logger.getLogger("com.almworks.sqlite4java");
		//logger.setLevel(Level.OFF);

		String fileName="pacioli.ldb";
		SQLiteConnection conn=new SQLiteConnection(new File(fileName));
		conn.open();
		Counter c=new Counter();
		c.init(conn);
		System.out.println("nextKey="+String.valueOf(c.nextVal).trim());

		for (int i=0;i<7;i++) {
			String key=c.nextKey(conn);
			System.out.println("assigned key="+key);
		}
	}
}
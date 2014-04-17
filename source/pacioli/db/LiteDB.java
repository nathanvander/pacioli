package pacioli.db;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.almworks.sqlite4java.*;

/** This should be a very minimal class that only stores the db file and id number.
* It also includes the Counter
*/
public class LiteDB implements DataSource {
	public final static String VERSION="0.05";
	File dbFile;
	int connId;
	Counter counter;

	public LiteDB(String fileName) throws DSX {
		this(new File(fileName));
	}

	public LiteDB(File file) throws DSX {
		//turn off logging
		SQLiteConnection scon=null;
		try {
			Logger logger = Logger.getLogger("com.almworks.sqlite4java");
			logger.setLevel(Level.OFF);

			//print off SQLite version
			System.out.println("SQLite version "+SQLite.getSQLiteVersion());

			//print off Sqlite4Java version
			System.out.println("sqlite4java version "+SQLite.getLibraryVersion());

			//print off LiteDB version
			System.out.println("pacioli.db.LiteDB version "+VERSION);

			//get default file is not specified
			if (dbFile==null) {
				dbFile=new File("pacioli.ldb");
			}
			//print off file
			System.out.println("database="+dbFile.getAbsolutePath());

			//startup the counter
			//---------------------------------------------
			scon=new SQLiteConnection(dbFile);
			scon.open();
			connId++;
			System.out.println("opening connection #"+connId);
			counter = new Counter();
			counter.init(scon);
			//---------------------------------------------

		} catch (SQLiteException x) {
			throw new DSX(x.getErrorCode(),x.getMessage());
		} finally {
			//always close the connection
			if (scon!=null) {scon.dispose();}
			System.out.println("closing connection #"+connId);
		}
	}

	//this will be called from multiple threads.  each thread, i.e. each dialog, needs its own connection.
	public synchronized Conn getConnection() throws DSX {
		SQLiteConnection scon=null;
		try {
			//-----------------------
			scon=new SQLiteConnection(dbFile);
			connId++;
			scon.open();
			System.out.println("opening connection #"+connId);
			return new LiteDBConnection(scon,counter,connId);
		} catch (SQLiteException x) {
			if (scon!=null) {scon.dispose();}
			System.out.println("closing connection #"+connId+" because of exception");
			throw new DSX(x.getErrorCode(),x.getMessage());
		}
	}
}
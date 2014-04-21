package pacioli.h2db;
import pacioli.db.DSX;
import pacioli.db.DataSource;
import pacioli.db.Conn;
import java.io.File;
import java.sql.*;
import org.h2.jdbcx.JdbcConnectionPool;
import org.h2.engine.Constants;

/**
* This is a port of the DB layer to run on H2 instead of Sqlite.
*/

public class H2DB implements DataSource {
	public final static String VERSION="0.05";
	JdbcConnectionPool pool;
	int connId;
	Counter counter;

	//the filename should be relative.  It will be stored in the same directory as the startup script
	//filename should not have an extension, as it is added by database
	public H2DB(String fileName) throws DSX {
		Connection hcon=null;
		try {
			if (fileName==null) {fileName="pacioli";}

			Class.forName("org.h2.Driver");
			String uri="jdbc:h2:./"+fileName;

			//startup connection pool
			pool = JdbcConnectionPool.create(uri,"","");
			if (pool==null) {throw new DSX(ErrorCode.JAVA_LANG_NULLPOINTER,"pool is null");}

			//print off H2 version
			System.out.println("H2 version "+Constants.getVersion());

			//print off LiteDB version
			System.out.println("pacioli.h2db.H2DB version "+VERSION);

			//print off file
			System.out.println("database="+uri);

			//startup the counter
			//---------------------------------------------
			hcon=pool.getConnection();
			connId++;
			System.out.println("opening connection #"+connId);
			counter = new Counter();
			counter.init(hcon);
			//---------------------------------------------

		} catch (SQLException x) {
			throw new DSX(x.getErrorCode(),x.getMessage());
		} catch (ClassNotFoundException x) {
			throw new DSX(ErrorCode.JAVA_LANG_CLASSNOTFOUND,x.getMessage());
		} finally {
			//always close the connection
			try {
				if (hcon!=null) {
					hcon.close();
					System.out.println("closing connection #"+connId);
				}
			} catch (SQLException x) {}  //ignore
		}
	}

	//this will be called from multiple threads.  each thread, i.e. each dialog, needs its own connection.
	public synchronized Conn getConnection() throws DSX {
		Connection hcon=null;
		try {
			//-----------------------
			hcon=pool.getConnection();
			connId++;
			System.out.println("opening connection #"+connId);
			return new H2Connection(hcon,counter,connId);
		} catch (SQLException x) {
			if (hcon!=null) {
				try {
				hcon.close();
				System.out.println("closing connection #"+connId+" because of exception");
				} catch (SQLException x2) {}
			}

			throw new DSX(x.getErrorCode(),x.getMessage());
		}
	}
}
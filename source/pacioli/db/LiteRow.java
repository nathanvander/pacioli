package pacioli.db;
import com.almworks.sqlite4java.*;
import pacioli.util.ClassTableName;

/** LiteRow
*/
public class LiteRow implements Row {
	SQLiteStatement stmt;

	public LiteRow(SQLiteStatement stmt) {
		this.stmt=stmt;
	}

	public String key() throws DSX {
		try {
			return stmt.columnString(0);
		} catch (SQLiteException x) {
			throw new DSX(x.getErrorCode(),"LiteRow.key(): "+x.getMessage());
		}
	}

	public boolean next() throws DSX {
		try {
			return stmt.step();
		} catch (SQLiteException x) {
			throw new DSX(x.getErrorCode(),"LiteRow.next(): "+x.getMessage());
		}
	}

	//get number of columns
	public int getColumnCount() throws DSX {
		try {
			return stmt.columnCount();
		} catch (SQLiteException x) {
			throw new DSX(x.getErrorCode(),"LiteRow.getColumnCount(): "+x.getMessage());
		}
	}

	//will return either:
	//	SQLITE_NULL = 5
	//	SQLITE_INTEGER = 1
	//	SQLITE_TEXT = 3
	//	SQLITE_BLOB = 4
	//	SQLITE_FLOAT = 2
	public int getColumnType(int col) throws DSX {
		try {
			return stmt.columnType(col);
		} catch (SQLiteException x) {
			throw new DSX(x.getErrorCode(),x.getMessage());
		}
	}

	//return the name of the column
	public String getColumnName(int col) throws DSX {
		try {
			return stmt.getColumnName(col);
		} catch (SQLiteException x) {
			throw new DSX(x.getErrorCode(),x.getMessage());
		}
	}

	public String getString(int col) throws DSX {
		try {
		return stmt.columnString(col);
		} catch (SQLiteException x) {
			throw new DSX(x.getErrorCode(),x.getMessage());
		}
	}

	public int getInt(int col) throws DSX {
		try {
		return stmt.columnInt(col);
		} catch (SQLiteException x) {
			throw new DSX(x.getErrorCode(),x.getMessage());
		}
	}

	public long getLong(int col) throws DSX {
		try {
			return stmt.columnLong(col);
		} catch (SQLiteException x) {
			throw new DSX(x.getErrorCode(),x.getMessage());
		}
	}

	public double getDouble(int col) throws DSX {
		try {
			return stmt.columnDouble(col);
		} catch (SQLiteException x) {
			throw new DSX(x.getErrorCode(),x.getMessage());
		}
	}


	//return the value as an object.  The object type will vary
	//   null if SQLITE_NULL
	//	 String if text
	//   Integer or Long if SQLITE_INTEGER
	//	 Double if Float
	//	 byte[] if blob
	public Object getValue(int col) throws DSX {
		try {
			return stmt.columnValue(col);
		} catch (SQLiteException x) {
			throw new DSX(x.getErrorCode(),x.getMessage());
		}
	}

	//just turn the whole thing into an object
	//this doesn't work with joins
	public Object getRow() throws DSX {
		try {
			String tableName=stmt.getColumnTableName(0);
			String className=ClassTableName.unflatten(tableName);
			Class k=Class.forName(className);
			Object o=LiteDBConnection.extractObject(stmt,k);
			return o;
		} catch (SQLiteException x) {
			throw new DSX(x.getErrorCode(),x.getMessage());
		} catch (ClassNotFoundException x2) {
			throw new DSX(ErrorCode.JAVA_LANG_CLASSNOTFOUND,x2.getMessage());
		}
	}
	public void close() {
		stmt.dispose();
	}


}
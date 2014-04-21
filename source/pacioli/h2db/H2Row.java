package pacioli.h2db;
import pacioli.util.ClassTableName;
import pacioli.db.DSX;
import pacioli.db.Row;
import java.sql.*;

/** H2Row. This is a wrapper around ResultSet
*/
public class H2Row implements Row {
	ResultSet rs;

	public H2Row(ResultSet rs) {
		this.rs=rs;
	}

	public boolean next() throws DSX {
		try {
			return rs.next();
		} catch (SQLException x) {
			throw new DSX(x.getErrorCode(),x.getMessage());
		}
	}

	//key is always the 0th column
	//WAIT WAIT WAIT.  Jdbc columns start with 1
	public String key() throws DSX {
		try {
			return rs.getString(1);
		} catch (SQLException x) {
			throw new DSX(x.getErrorCode(),x.getMessage());
		}
	}



	//get number of columns
	public int getColumnCount() throws DSX {
		try {
			ResultSetMetaData meta=rs.getMetaData();
			return meta.getColumnCount();
		} catch (SQLException x) {
			throw new DSX(x.getErrorCode(),x.getMessage());
		}
	}

	//returns the SQL type.  There are a lot of them, I won't list them all
	public int getColumnType(int col) throws DSX {
		try {
			ResultSetMetaData meta=rs.getMetaData();
			return meta.getColumnType(col);
		} catch (SQLException x) {
			throw new DSX(x.getErrorCode(),x.getMessage());
		}
	}

	//return the name of the column
	public String getColumnName(int col) throws DSX {
		try {
			ResultSetMetaData meta=rs.getMetaData();
			return meta.getColumnName(col);
		} catch (SQLException x) {
			throw new DSX(x.getErrorCode(),x.getMessage());
		}
	}

	public String getString(int col) throws DSX {
		try {
			return rs.getString(col);
		} catch (SQLException x) {
			throw new DSX(x.getErrorCode(),x.getMessage());
		}
	}

	public int getInt(int col) throws DSX {
		try {
		return rs.getInt(col);
		} catch (SQLException x) {
			throw new DSX(x.getErrorCode(),x.getMessage());
		}
	}

	public long getLong(int col) throws DSX {
		try {
			return rs.getLong(col);
		} catch (SQLException x) {
			throw new DSX(x.getErrorCode(),x.getMessage());
		}
	}

	public double getDouble(int col) throws DSX {
		try {
			return rs.getDouble(col);
		} catch (SQLException x) {
			throw new DSX(x.getErrorCode(),x.getMessage());
		}
	}


	//return the value as an object.   The type of the Java object will be the default Java object type
	//corresponding to the column's SQL type, following the mapping for built-in types specified in the JDBC specification.
	public Object getValue(int col) throws DSX {
		try {
			return rs.getObject(col);
		} catch (SQLException x) {
			throw new DSX(x.getErrorCode(),x.getMessage());
		}
	}

	//just turn the whole thing into an object
	//this doesn't work with joins
	public Object getRow() throws DSX {
		try {
			ResultSetMetaData meta=rs.getMetaData();
			String tableName=meta.getTableName(1);
			//this comes back in all caps
			tableName=regularize(tableName);

			String className=ClassTableName.unflatten(tableName);
			Class k=Class.forName(className);
			Object o=H2Connection.extractObject(rs,k);
			return o;
		} catch (SQLException x) {
			throw new DSX(x.getErrorCode(),x.getMessage());
		} catch (ClassNotFoundException x2) {
			throw new DSX(ErrorCode.JAVA_LANG_CLASSNOTFOUND,x2.getMessage());
		}
	}

	//a tableName might be in all caps, so make it look regular
	//assume everything is in lowercase except the last word
	private static String regularize(String tableName) {
			String s[]=tableName.split("_");
			String newTableName="";
			for (int i=0;i<s.length;i++) {
				String word1=s[i];
				word1=word1.toLowerCase();
				if (i==(s.length-1)) {
					String first=Character.toString(word1.charAt(0));
					first=first.toUpperCase();
					String remainder=word1.substring(1);
					word1=first+remainder;
				}
				if (i!=0) {
					newTableName+="_";
				}
				newTableName+=word1;
			}
			return newTableName;
	}

	//dont throw an exception
	public void close() {
		try {
			rs.close();
		} catch (SQLException x) {
			System.out.println(x.getErrorCode()+" error closing row");
		}
	}

	//=====================
	public static void main(String[] args) {
		System.out.println(regularize(args[0]));
	}

}
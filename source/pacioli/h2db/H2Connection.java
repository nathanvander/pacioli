package pacioli.h2db;
import pacioli.util.ClassTableName;
import pacioli.db.DSX;
import pacioli.db.Conn;
import pacioli.db.Row;
import java.util.Date;
import java.lang.reflect.Field;
import java.sql.*;

/**
* H2Connection is a wrapper around java.sql.Connection, using the following principles:
*  1. each object in its own table
*  2. a commmon key for objects
*
* Note that tables will have underscores for periods.
*
*/

public class H2Connection implements Conn {
	Connection conn;
	Counter counter;
	int connId;		//used for debugging

	public H2Connection(Connection cx,Counter counter,int connId) {
		this.conn=cx;
		this.counter=counter;
		this.connId=connId;
	}

	public int getConnId() {
		return connId;
	}

	//creates only if table does not exist
	//className has dots in it
	public void createTable(String className) throws DSX {
		if (className==null) throw new DSX(ErrorCode.JAVA_LANG_NULLPOINTER,"className is null");
		String tableName=ClassTableName.flattenedTableName(className);
		if (!tableExists(tableName)) {
			try {
				Class k=Class.forName(className);
				createTable(tableName,k);
			} catch (ClassNotFoundException x) {
				throw new DSX(ErrorCode.JAVA_LANG_CLASSNOTFOUND," class "+className+" not found, "+x.getMessage());
			}
		}
	}

	//put key as the first field
	//maps the following:
	//	String to VARCHAR
	//	boolean to BOOLEAN (not INT)
	private void createTable(String tableName,Class k) throws DSX {
		StringBuffer sql=new StringBuffer();
		sql.append("CREATE TABLE IF NOT EXISTS "+tableName+" (\n");

		//append key
		sql.append("key VARCHAR PRIMARY KEY NOT NULL"+"\n");
		sql.append(",");

		Field[] fields=k.getFields();
		for (int i=0;i<fields.length;i++) {
			if (i!=0) {sql.append(",");}
			Field f=fields[i];  //these are the public fields
			String fieldName=f.getName();
			String ft=f.getType().getName();

			if (ft.equals("java.lang.String")) {
				sql.append(fieldName+" VARCHAR"+"\n");
			} else if (ft.equals("java.util.Date")) {
				sql.append(fieldName+" VARCHAR"+"\n");
			} else if (ft.equals("int")) {
				sql.append(fieldName+" INT"+"\n");
			} else if (ft.equals("long")) {
				sql.append(fieldName+" BIGINT"+"\n");
			} else if (ft.equals("char")) {
				sql.append(fieldName+" INT"+"\n");
			} else if (ft.equals("boolean")) {
				sql.append(fieldName+" BOOLEAN"+"\n");   //this will be 1 or 0
			} else if (ft.equals("float")) {
				sql.append(fieldName+" REAL"+"\n");
			} else if (ft.equals("double")) {
				sql.append(fieldName+" DOUBLE"+"\n");
			} else if (ft.equals("[C")) {  //char array
				sql.append(fieldName+" VARCHAR"+"\n");
			} else if (ft.equals("[B")) {  //byte array
				sql.append(fieldName+" BLOB"+"\n");  //needs more work
			} else if (ft.equals("java.math.BigDecimal")) {
				//same as DECIMAL in H2
				sql.append(fieldName+" NUMERIC"+"\n");
			}
		}
		sql.append(")"+"\n");
		System.out.println(sql);
		try {
			Statement stmt=conn.createStatement();
			stmt.execute(sql.toString());
			stmt.close();
		} catch (SQLException x) {
			throw new DSX(x.getErrorCode(),x.getMessage());
		}
	}

	private boolean tableExists(String tableName) throws DSX {
		//String sql="SELECT name FROM sqlite_master WHERE type = 'table' AND name = '"+tableName+"'";
		//table names in H2 might be uppercase automatically, not sure
		String sql="SELECT COUNT(*) AS count FROM information_schema.tables WHERE upper(table_name) = '"+tableName.toUpperCase()+"'";
		System.out.println(sql);
		Statement stmt=null;
		try {
			stmt=conn.createStatement();
			ResultSet rs=stmt.executeQuery(sql);
			rs.next();
			int count=rs.getInt(1);
			stmt.close();
			return (count==1);
		} catch (SQLException x) {
			throw new DSX(x.getErrorCode(),x.getMessage());
		}
	}


	//insert the object and return the key
	public String insert(Object o) throws DSX {
		if (o==null) {throw new DSX(ErrorCode.PACIOLI_DB_INSERT,"Object is null");}
		try {
		String tableName=ClassTableName.flattenedTableName(o.getClass().getName());
		Field[] fields=o.getClass().getFields();
		String key=counter.nextKey(conn);

		StringBuffer sql=new StringBuffer();
		sql.append("INSERT INTO "+tableName+" \n");
		sql.append("(key,"+fieldNames(fields)+")"+" \n");
		sql.append("VALUES ("+"\n");
		sql.append("'"+key+"'\n");		//key
		sql.append(",");
		for (int i=0;i<fields.length;i++) {
			if (i!=0) {sql.append(",");}
			Field f=fields[i];
			String ft=f.getType().getName();

			if (ft.equals("java.lang.String")) {
				String val=(String)f.get(o);
				if (val==null) {
					sql.append(val+"\n");
				} else {
					sql.append("'"+val+"'\n");
				}
			} else if (ft.equals("java.util.Date")) {
				Date val=(Date)f.get(o);
				if (val==null) {
					sql.append(val+"\n");
				} else {
					sql.append("'"+val.toString()+"'\n");
				}
			} else if (ft.equals("int")) {
				int iv=f.getInt(o);
				sql.append(iv+"\n");
			} else if (ft.equals("long")) {
				long lv=f.getLong(o);
				sql.append(lv+"\n");
			} else if (ft.equals("char")) {
				char cv=f.getChar(o);
				sql.append("'"+cv+"'\n");
			} else if (ft.equals("boolean")) {
				boolean bv=f.getBoolean(o);
				//sql.append("'"+bv+"'\n");
				if (bv) { sql.append("1\n");}
				else {sql.append("0\n");}
			} else if (ft.equals("float")) {
				float fv=f.getFloat(o);
				sql.append(fv+"\n");
			} else if (ft.equals("double")) {
				double dv=f.getDouble(o);
				sql.append(dv+"\n");
			} else if (ft.equals("[C")) {
				Object co=f.get(o);
				char[] ca=(char[])co;
				String sca=String.valueOf(ca);
				sql.append("'"+sca+"'\n");
			} else if (ft.equals("java.math.BigDecimal")) {
				java.math.BigDecimal bd=(java.math.BigDecimal)f.get(o);
				String sbd=bd.toPlainString();
				sql.append(sbd+"\n");
			}
		}
		sql.append(")"+"\n");
		System.out.println(sql);
		Statement st=conn.createStatement();
		st.executeUpdate(sql.toString());
		st.close();
		return key;
		} catch (SQLException x) {
			throw new DSX(x.getErrorCode(),x.getMessage());
		} catch (IllegalAccessException x2) {
			throw new DSX(ErrorCode.JAVA_LANG_ILLEGALACCESS,x2.getMessage());
		}

	}

	private static String fieldNames(Field[] fields) {
		StringBuffer sb=new StringBuffer();
		//get the first field
		Field f=fields[0];
		sb.append(f.getName());
		for (int i=1;i<fields.length;i++) {
			f=fields[i];  //these are the public fields
			String fieldName=f.getName();
			sb.append(","+fieldName);
		}
		return sb.toString();
	}

	private static String fieldNames(String[] fields) {
		StringBuffer sb=new StringBuffer();
		//get the first field
		sb.append(fields[0]);
		for (int i=1;i<fields.length;i++) {
			sb.append(","+fields[1]);
		}
		return sb.toString();
	}

	//className here is the unflattened name, i.e. dots instead of underscores
	//returns null if not found
	public Object get(String key,String className) throws DSX {
		Statement stmt=null;
		try{
			Class k=Class.forName(className);
			String flattened=ClassTableName.flattenedTableName(className);
			String sql="SELECT * FROM "+flattened+" WHERE key='"+key+"'";
			System.out.println(sql);
			stmt = conn.createStatement();
			ResultSet rs=stmt.executeQuery(sql);
			if (rs.next()) {
				Object o=extractObject(rs,k);
				rs.close();
				stmt.close();
				return o;
			} else {
				throw new DSX(ErrorCode.PACIOLI_DB_GET,"requested record at key "+key+" does not exist");
			}
		} catch (SQLException x) {
			throw new DSX(x.getErrorCode(),x.getMessage());
		} catch (ClassNotFoundException x2) {
			throw new DSX(ErrorCode.JAVA_LANG_CLASSNOTFOUND," class "+className+" not found, "+x2.getMessage());
		}
	}

	//key is in the table but not in the object
	public static Object extractObject(ResultSet rs,Class c) throws DSX {
	 	try {
			Object o=c.newInstance();
			ResultSetMetaData meta=rs.getMetaData();
			for (int i=1;i<meta.getColumnCount()+1;i++) {
				String colName=meta.getColumnName(i);	//this might be upper case
				colName=colName.toLowerCase();
				if (colName.equalsIgnoreCase("key")) {continue;}  //the object doesn't have the key in it
				if (rs.getObject(i)==null) {continue;}

			 	Field f=c.getField(colName);
			 	String ft=f.getType().getName();
			 	String value=rs.getString(i);
				if (ft.equals("java.lang.String")) {
					f.set(o,value);
				} else if (ft.equals("java.util.Date")) {
					f.set(o,new java.util.Date(value));
				} else if (ft.equals("int")) {
					f.setInt(o,rs.getInt(i));
				} else if (ft.equals("long")) {
					f.setLong(o,rs.getLong(i));
				} else if (ft.equals("char")) {
					char cv=value.charAt(0);
					f.setChar(o,cv);
				} else if (ft.equals("boolean")) {
					boolean bz=rs.getBoolean(i);
					f.setBoolean(o,bz);
				} else if (ft.equals("float")) {
					f.setFloat(o,rs.getFloat(i));
				} else if (ft.equals("double")) {
					f.setDouble(o,rs.getDouble(i));
				} else if (ft.equals("[C")) {
					char[] ca=value.toCharArray();
					f.set(o,value);
				} else if (ft.equals("java.math.BigDecimal")) {
					java.math.BigDecimal bd=new java.math.BigDecimal(value);
					f.set(o,bd);
				}
		 	}
		 	return o;
		} catch (NullPointerException x) {
			throw new DSX(ErrorCode.JAVA_LANG_NULLPOINTER,x.getMessage());
		} catch (InstantiationException x) {
			throw new DSX(ErrorCode.JAVA_LANG_INSTANTIATION,x.getMessage());
		} catch (SQLException x) {
			throw new DSX(x.getErrorCode(),x.getMessage());
		} catch (IllegalAccessException x) {
			throw new DSX(ErrorCode.JAVA_LANG_ILLEGALACCESS,x.getMessage());
		} catch (NoSuchFieldException x) {
			throw new DSX(ErrorCode.JAVA_LANG_NOSUCHFIELD,x.getMessage());
		}
	}

	//we don't need to specify table because it is already in the name of the object,
	public void update(String key,Object o) throws DSX {
		if (key==null || o==null) throw new DSX(ErrorCode.PACIOLI_DB_UPDATE_NULL,"key or object is null");
		try {
			//see if record exists in table
			Object og=get(key,o.getClass().getName());
		} catch (DSX x) {
			if (x.getErrorCode()==ErrorCode.PACIOLI_DB_GET) {
				//the record does not exist in the database
				throw new DSX(ErrorCode.PACIOLI_DB_UPDATE_MISSING,"attempting to update a record that does not exist");
			} else {
				//rethrow it
				throw x;
			}
		}

		try {
		//create sql for update
		String tableName=ClassTableName.flattenedTableName(o.getClass().getName());
		Field[] fields=o.getClass().getFields();
		long ts=System.currentTimeMillis();
		StringBuffer sql=new StringBuffer();
		sql.append("UPDATE "+tableName+" SET \n");
		for (int i=0;i<fields.length;i++) {
			if (i!=0) {sql.append(",");}
			Field f=fields[i];
			String fn=f.getName();
			String ft=f.getType().getName();
			Object fv=f.get(o);
			if (fv==null) {
				sql.append(fn+"="+fv+"\n");
			} else if (ft.equals("java.lang.String")) {
				String val=(String)fv;
				sql.append(fn+"='"+fv+"'\n");
			} else if (ft.equals("java.util.Date")) {
				String dv=(String)fv.toString();
				sql.append(fn+"='"+dv+"'\n");
			} else if (ft.equals("int")) {
				sql.append(fn+"="+f.getInt(o)+"\n");
			} else if (ft.equals("long")) {
				sql.append(fn+"="+f.getLong(o)+"\n");
			} else if (ft.equals("char")) {
				char cv=f.getChar(o);
				sql.append(fn+"='"+cv+"'\n");
			} else if (ft.equals("boolean")) {
				boolean bv=f.getBoolean(o);
				sql.append(fn+"='"+bv+"'\n");
				//if (bv) { sql.append(fn+"=1\n");}
				//else {sql.append(fn+"=0\n");}
			} else if (ft.equals("float")) {
				float flv=f.getFloat(o);
				sql.append(fn+"="+flv+"\n");
			} else if (ft.equals("double")) {
				double dv=f.getDouble(o);
				sql.append(fn+"="+dv+"\n");
			} else if (ft.equals("[C")) {
				char[] ca=(char[])fv;
				String sca=String.valueOf(ca);
				sql.append(fn+"='"+sca+"'\n");
			} else if (ft.equals("java.math.BigDecimal")) {
				java.math.BigDecimal bd=(java.math.BigDecimal)f.get(o);
				String sbd=bd.toPlainString();
				sql.append(fn+"="+sbd+"\n");
			}
		}
		//add the where clause
		sql.append("WHERE key='"+key+"'"+"\n");
		System.out.println(sql);
			Statement st=conn.createStatement();
			int i=st.executeUpdate(sql.toString());
			if (i==0) {throw new DSX(ErrorCode.PACIOLI_DB_UPDATE_FAILED,"no rows changed");}
		} catch (SQLException x) {
			throw new DSX(x.getErrorCode(),x.getMessage());
		} catch (IllegalAccessException x2) {
			throw new DSX(ErrorCode.JAVA_LANG_ILLEGALACCESS,x2.getMessage());
		}
	}

	//this is simple
	//table has dots
	//public void delete(String key,String className) throws DSX {
	public void delete(String key,String className) throws DSX {
		if (key==null || className ==null) {throw new DSX(ErrorCode.PACIOLI_DB_DELETE_NULL,"dobj is null");}
		String table=ClassTableName.flattenedTableName(className);
		String sql="DELETE FROM "+table+" WHERE key='"+key+"'";
		System.out.println(sql);
		try {
			Statement st=conn.createStatement();
			int i=st.executeUpdate(sql.toString());
			if (i==0) {throw new DSX(ErrorCode.PACIOLI_DB_DELETE_FAILED,"no rows changed");}
		} catch (SQLException x) {
			throw new DSX(x.getErrorCode(),x.getMessage());
		}
	}

	//tableName has dots
	public Row selectAll(String className,String[] sortfields) throws DSX {
		try {
		Class k=Class.forName(className);
		Object o=k.newInstance();
		if (sortfields==null) {sortfields=new String[]{"key"};}

		String table=ClassTableName.flattenedTableName(className);
		String sql="SELECT * FROM "+table+" ORDER BY "+fieldNames(sortfields);
		System.out.println(sql);
			Statement st=conn.createStatement();
			ResultSet rs=st.executeQuery(sql);
			return new H2Row(rs);
		} catch (SQLException x) {
			throw new DSX(x.getErrorCode(),x.getMessage());
		} catch (ClassNotFoundException x2) {
			throw new DSX(ErrorCode.JAVA_LANG_CLASSNOTFOUND,className+" "+x2.getMessage());
		} catch (InstantiationException x3) {
			throw new DSX(ErrorCode.JAVA_LANG_INSTANTIATION,className+" "+x3.getMessage());
		} catch (IllegalAccessException x4) {
			throw new DSX(ErrorCode.JAVA_LANG_ILLEGALACCESS,className+" "+x4.getMessage());
		}
	}

	//for custom queries
	public Row query(String sql) throws DSX {
		try {
			Statement st=conn.createStatement();
			ResultSet rs=st.executeQuery(sql);
			return new H2Row(rs);
		} catch (SQLException x) {
			throw new DSX(x.getErrorCode(),x.getMessage());
		}
	}

	//there really is a difference between execute() and executeUpdate().  Assume that executeUpdate is wanted
	public void exec(String sql) throws DSX {
		try {
			Statement st=conn.createStatement();
			st.executeUpdate(sql);
		} catch (SQLException x) {
			throw new DSX(x.getErrorCode(),x.getMessage());
		}
	}

	//return number of rows in table
	public int count(String className) throws DSX {
		try {
		String tableName=ClassTableName.flattenedTableName(className);
		String sql="SELECT count(*) FROM "+tableName;
		System.out.println(sql);
		Statement st=conn.createStatement();
		ResultSet rs=st.executeQuery(sql);
		rs.next();
		int i=rs.getInt(1);
		st.close();
		return i;
		} catch (SQLException x) {
			throw new DSX(x.getErrorCode(),x.getMessage());
		}
	}

	//you can't use this after it is closed
	//don't propagate exception
	public void close() {
		try {
			conn.close();
			System.out.println("closing connection #"+connId);
		} catch (SQLException x) {
			System.out.println(x.getErrorCode()+" error closing connection");
		}

	}
}
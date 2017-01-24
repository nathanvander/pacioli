package nathanvander.pacioli.table;
import apollo.iface.DataObject;

/**
* This is a wrapper around the user table
*/
public class User implements DataObject {
	public long rowid;	//this is also the userid
	public String username;
	public String firstname;
	public String lastname;
	public String password;		//this is encrypted
	public boolean disabled;	//true if disabled

	public String getTableName() {return "_user";}

	public String[] fields() {return new String[]{"username","firstname","lastname","password","disabled"};}
	public String[] displayNames() {return new String[]{"username","firstname","lastname","password","disabled"};}

	public String index() {return "username";}
	public long getID() {return rowid;}

	//do a deep copy
	public User clone() {
		User u2=new User();
		u2.rowid=rowid;
		u2.username=username;
		u2.firstname=firstname;
		u2.lastname=lastname;
		u2.password=password;
		u2.disabled=disabled;
		return u2;
	}

	//this is obsolete now
	public static String createTableSql() {
		StringBuffer sb=new StringBuffer("CREATE TABLE IF NOT EXISTS _user (");
		sb.append("rowid INTEGER PRIMARY KEY, ");
		sb.append("username TEXT NOT NULL, ");
		sb.append("firstname TEXT, ");
		sb.append("lastname TEXT, ");
		sb.append("password TEXT NOT NULL, ");
		sb.append("disabled BOOLEAN NOT NULL)");
		return sb.toString();
	}

	//return the sql for adding a user
	//the password is already encrypted at this point
	public static String addUser(String username,String password) {
		StringBuffer sb=new StringBuffer("INSERT INTO _user ");
		sb.append("(username, password, disabled) ");
		sb.append("VALUES ('"+username+"','"+password+"','false')");
		return sb.toString();
	}

	public static String lockoutUser(String username) {
		return "UPDATE _user SET disabled='true' WHERE username='"+username+"'";
	}

	public static String selectUserNames() {
		return "SELECT username FROM _user";
	}

	/**
	* return the sql to validate the user.  If it doesn't match
	* the select will return zero rows.
	*/
	public static String validate(String username,String password) {
		return "SELECT username from _user WHERE username='"+username+"' AND password='"+password+"'";
	}
}
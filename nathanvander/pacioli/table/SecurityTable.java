package nathanvander.pacioli.table;
import apollo.iface.DataObject;

/**
* This class describes the structure of the security table.
* It will only have one row.  It could have multiple keys,
* and the date and so forth, but I think this is sufficient.
* We may not even need to store the private_key.
*/
public class SecurityTable implements DataObject {

	public long rowid;	//this will only have one row
	//we definitely don't need to store these
	//but this is just a first version
	//delete them to make the system more secure
	public String p_prime;
	public String q_prime;
	//public_key is really the only one needed
	public String public_key;
	//private_key isn't needed and can be deleted
	public String private_key;

	public String getTableName() {return "_security";}

	public String[] fields() {return new String[]{"p_prime","q_prime","public_key","private_key"};}
	public String[] displayNames() {return new String[]{"p_prime","q_prime","public_key","private_key"};}

	public String index() {return "public_key";}
	public long getID() {return rowid;}
	public DataObject clone() {return null;}	//not implemented

	//obsolete method
	public static String createTableSql() {
		StringBuffer sb=new StringBuffer("CREATE TABLE IF NOT EXISTS _security (");
		sb.append("rowid INTEGER PRIMARY KEY, ");
		sb.append("p_prime TEXT, ");
		sb.append("q_print TEXT, ");
		sb.append("public_key TEXT, ");
		sb.append("private_key TEXT )");
		return sb.toString();
	}

	//these must all have values
	public static String insertSql(String p_prime,String q_prime,String public_key,String private_key) {
		StringBuffer sb=new StringBuffer("INSERT INTO _security ");
		sb.append("(p_prime,q_prime,public_key,private_key)");
		sb.append("VALUES ('"+p_prime+"','"+q_prime+"','"+public_key+"','"+private_key+"')");
		return sb.toString();
	}

	//we expect this to only have one row, so use rowid#1
	public static String updateSql(String p_prime,String q_prime,String public_key,String private_key) {
		StringBuffer sb=new StringBuffer("UPDATE _security ");
		sb.append("SET p_prime='"+p_prime+"',");
		sb.append("q_prime='"+q_prime+"',");
		sb.append("public_key='"+public_key+"',");
		sb.append("private_key='"+private_key+"' ");
		sb.append("WHERE rowid=1");
		return sb.toString();
	}

	//return the public key
	public static String getPublicKey() {
		//this will select everything but there is only one row
		return "SELECT public_key FROM _security";
	}
}
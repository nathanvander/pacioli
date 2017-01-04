package pacioli2.table;
import apollo.iface.*;
import apollo.util.DateYMD;

/**
*  Txn is a short abbreviation for Transaction.  It is given a different name so
* it doesn't conflict with a database transaction.
*/

public class Txn implements DataObject {
	public long rowid;
	public String _key;
	public String id;		//the transaction id.  This is in BASE-12. Similar to _key.  Assigned by the database
	public DateYMD date;	//the transaction date
	public String time;		//time in format HH:MM.  usually transactions don't have time, but you could
							//use it for automated transactions
	public String source;	//use this for journal name or other detail
	public String activity;	//optional.  This is the activity key.  Not free-form, it is a base-12 number
							//this is so you can get more detail on the transaction
	public String desc;		//give a description of the transaction

							//we could also have a field for user who entered it

	public String getTableName() {return "Txn";}

	public String[] fields() {return new String[]{"id","date","time","source","activity","desc"};}

	//default sort order
	public String index() {return "id";}

	public String getKey() {return _key;}
}
package pacioli2.table;
import apollo.iface.*;

/**
* This is the table design for accounts.
*	rowid - used only by database
*	number - this is the account number, could be 3 digits or however long you want
*	name - account name
*	type - one of the 5 basic types
*	parent - parent account number
*	category - if true, can't be used for detail
*	special - has a special code for logic within the accounting system
*		(for example an accounts receivable account will have sub-accounts for clients)
*	description
*	inactive - if it is inactive don't use it. But there might be some old data using it
*
*	Note that there is no sub-account field, but everything that uses this has a sub-account.
* That is because the sub-account is just the matterid.  There is no need to name it separately.
*
* Most accounts do not use the sub-account.  It used only for:
*	Billable Time
*	Billable Costs
*	Accounts Receivable
*	Trust Liability
*	Accounts Payable
*/

public class Account implements DataObject {
	//the 5 basic types of accounts
	public final static int ASSET=1;		//have a debit balance
	public final static int LIABILITY=2;	//have a credit balance
	public final static int EQUITY=3;		//have a credit balance
	public final static int REVENUE=4;		//have a credit balance
	public final static int EXPENSE=5;
	public final static int OTR_REV=6;		//have a credit balance
	public final static int OTR_EXP=7;

	//special codes
	public final static int BILLABLE_TIME=140;
	public final static int BILLABLE_COSTS=140;
	public final static int ACCOUNTS_RECEIVABLE=150;
	public final static int TRUST_LIABILITY=240;
	public final static int ACCOUNTS_PAYABLE=250;

	public long rowid;
	public String _key;
	public int number;		//the account number.  I recommend at least 5 digits
	public String name;
	public int type;		//one of the 5 types above
	public int parent;		//the parent account number. All non-category accounts must have a parent
	public boolean category;	//if this is a category, this will be true
								//in which case you can't use this with transactions.  for reporting only
	public int special;		//optional special code. leave 0 if none
	public String desc;
	public boolean inactive;

	public Account() {}
	public Account(int num,String name) {
		number=num;
		this.name=name;
	}

	public String getTableName() {return "Account";}

	public String[] fields() {return new String[]{"number","name","type","parent","category","special","desc","inactive"};}

	//sub is not included, because it can be null
	public String index() {return "number";}

	public String getKey() {return _key;}

	//user-friendly display
	public static String getType(int i) {
		switch (i) {
			case 1: return "Asset";break;
			case 2: return "Liability";break;
			case 3: return "Equity";break;
			case 4: return "Income";break;
			case 5: return "Expense";break;
			case 6: return "Other Income";break;
			case 7: return "Other Expense";break;
			default: return "Invalid";
		}
	}


}

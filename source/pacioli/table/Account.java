package pacioli.table;
import java.util.*;
import java.awt.Choice;
import pacioli.db.*;

/**
* Account Type is one of: Asset, Liability, Equity, Revenue, Expense, Other Revenue, Other Expense
* It must begin with a capital letter
*
* Account number is just another identifier, and it can be zero.  You can use it to assign 3 or 4 digit account numbers
*
* Special is a special type of account. There should be only one account with each special tag.
*/

public class Account {
	public int number;
	public String name;
	public String type;
	public boolean debit=true;		//does this normally have a debit balance?
	public int special;
	public String desc;
	public boolean deleted=false;  	//should usually be false.  For integrity sake, don't actually allow a delete

	class Special {
		//tag special accounts
		//Note: This looks like a partial chart of accounts, but the numbering is just to make it more logical
		//most accounts do not need a tag and should be 0
		public final static int DEFAULT=0;
		public final static int CASH=10;			//cash and undeposited checks
		public final static int OPERATING_CHECKING=11;
		public final static int TRUST_CHECKING=12;
		public final static int RECEIVABLES=15;		//accounts receivable
		public final static int BILLABLE_TIME=18;	//asset
		public final static int BILLABLE_COST=19;
		public final static int TRUST_LIABILITY=20;
		public final static int PAYABLES=21;
		public final static int SALES=40;
		public final static int POTENTIAL_REVENUE=41;	//for time that hasn't been billed yet
		public final static int LOST_REVENUE=42;		//for written off time
		public final static int UNBILLABLE_TIME=50;
	}

	public Account() {}

	public Account(String type,String name,int special) {
		this.type=type;
		this.name=name;
		this.special=special;
	}

	//get possible types
	public static Choice getTypes() {
		Choice list = new Choice();
		list.add("Asset");
		list.add("Liability");
		list.add("Equity");
		list.add("Revenue");
		list.add("Expense");
		list.add("Other Revenue");
		list.add("Other Expense");
		return list;
	}

	//common accounts
	//	Asset:		Cash
	//				OperatingAccount
	//				Trust Account
	//				Receivables (Accounts Receivable)
	//				Billable Time
	//				Billable Cost
	//
	//  Liability:	Trust Liability
	//				Payable (Accounts Payable)
	//
	//  Equity:     Capital
	//				Drawing
	//				Retained Earnings
	//
	//  Revenue:	Revenue
	//				Billings (should match A/R, because it isn't revenue until it is paid)
	//				Refund
	//
	//  Expense:    Operating Expense
	//
	//  Other Revenue:  Potential Revenue
	//				Lost Revenue
	//
	//  Other Expense:  Unbillable Time

	public static void addAccounts(Conn db) throws DSX {

		//first see if they are in the database
		String sql="SELECT count() FROM pacioli_table_Account";
		Row rs=db.query(sql);
		//this should always return something, even if it is 0
		if (!rs.next()) {
			throw new DSX(ErrorCode.PACIOLI_DB_SELECT,"unexpected result from SELECT count()");
		}
		int count=rs.getInt(0);
		if (count==0) {
			//only add if table is empty
			Account a=new Account("Asset","Cash",Special.CASH);
			db.insert(a);
			a=new Account("Asset","Operating Account",Special.OPERATING_CHECKING);
			db.insert(a);
			a=new Account("Asset","Trust Account",Special.TRUST_CHECKING);
			db.insert(a);
			a=new Account("Asset","Receivables",Special.RECEIVABLES);
			db.insert(a);
			a=new Account("Asset","Billable Time",Special.BILLABLE_TIME);
			db.insert(a);
			a=new Account("Asset","Billable Cost",Special.BILLABLE_COST);
			db.insert(a);
			a=new Account("Liability","Payables",Special.PAYABLES);
			db.insert(a);
			a=new Account("Liability","Trust Liability",Special.TRUST_LIABILITY);
			db.insert(a);
			a=new Account("Equity","Capital",0);
			db.insert(a);
			a=new Account("Equity","Drawing",0);
			db.insert(a);
			a=new Account("Equity","Retained Earnings",0);
			db.insert(a);
			a=new Account("Revenue","Sales",Special.SALES);
			db.insert(a);
			a=new Account("Revenue","Refund",0);					//debit balance
			db.insert(a);
			a=new Account("Expense","Operating Expense",0);
			db.insert(a);
			a=new Account("Other Revenue","Potential Revenue",Special.POTENTIAL_REVENUE);
			db.insert(a);
			a=new Account("Other Revenue","Lost Revenue",Special.LOST_REVENUE);
			db.insert(a);
			a=new Account("Other Expense","Unbillable Time",Special.UNBILLABLE_TIME);
			db.insert(a);
		}
		//dont close connection, it was passed in
	}

}
package pacioli.table;
import java.util.*;
import java.awt.Choice;
import pacioli.db.*;

/**
* Account Type is one of: Asset, Liability, Equity, Revenue, Expense, Other Revenue, Other Expense
*
* Account number is just another identifier, and it can be blank.  You can use it to assign 3 or 4 digit account numbers
*
* Special is a special type of account. There should be only one account with each special tag.
*
* Because each accounting transaction only has 2 accounts, and because the number is recorded only once, it is impossible
* for this to ever go out of balance.
*/

public class Account {
	public int number;
	public String name;
	public int type;
	public int special;
	public String desc;				//description
	public boolean deleted=false;  	//should usually be false.  For integrity sake, don't actually allow a delete

	public Account() {}

	public Account(int type,String name,int special) {
		this.type=type;
		this.name=name;
		this.special=special;
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
		int count=db.count("pacioli.table.Account");
		if (count==0) {
			//only add if table is empty
			Account a=new Account(AccountType.Asset.ordinal(),"Cash",Special.CASH.code());
			db.insert(a);
			a=new Account(AccountType.Asset.ordinal(),"Operating Account",Special.OPERATING_CHECKING.code());
			db.insert(a);
			a=new Account(AccountType.Asset.ordinal(),"Trust Account",Special.TRUST_CHECKING.code());
			db.insert(a);
			a=new Account(AccountType.Asset.ordinal(),"Receivables",Special.RECEIVABLES.code());
			db.insert(a);
			a=new Account(AccountType.Asset.ordinal(),"Billable Time",Special.BILLABLE_TIME.code());
			db.insert(a);
			a=new Account(AccountType.Asset.ordinal(),"Billable Cost",Special.BILLABLE_COST.code());
			db.insert(a);
			a=new Account(AccountType.Liability.ordinal(),"Payables",Special.PAYABLES.code());
			db.insert(a);
			a=new Account(AccountType.Liability.ordinal(),"Trust Liability",Special.TRUST_LIABILITY.code());
			db.insert(a);
			a=new Account(AccountType.Equity.ordinal(),"Capital",0);
			db.insert(a);
			a=new Account(AccountType.Equity.ordinal(),"Drawing",0);
			db.insert(a);
			a=new Account(AccountType.Equity.ordinal(),"Retained Earnings",0);
			db.insert(a);
			a=new Account(AccountType.Revenue.ordinal(),"Sales",Special.SALES.code());
			db.insert(a);
			a=new Account(AccountType.Revenue.ordinal(),"Refund",0);
			db.insert(a);
			a=new Account(AccountType.Expense.ordinal(),"Operating Expense",0);
			db.insert(a);
			a=new Account(AccountType.OtherRevenue.ordinal(),"Potential Revenue",Special.POTENTIAL_REVENUE.code());
			db.insert(a);
			a=new Account(AccountType.OtherRevenue.ordinal(),"Lost Revenue",Special.LOST_REVENUE.code());
			db.insert(a);
			a=new Account(AccountType.OtherExpense.ordinal(),"Unbillable Time",Special.UNBILLABLE_TIME.code());
			db.insert(a);
		}
		//dont close connection, it was passed in
	}

}
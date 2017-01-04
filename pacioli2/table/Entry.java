package pacioli2.table;
import java.math.BigDecimal;
import java.math.MathContext;
import apollo.iface.DataObject;
import apollo.util.DateYMD;

/**
* An Entry is a single-line in an accounting transaction. Has no more information than necessary.
* this uses separate entries for debits and credits to make it absolutely clear.  Obviously the total
* of the debits must equal the totals of the credits.  We could have used negative numbers or Dr/Cr
* but I think I will go with this.
*
* An Entry needs to be a Time Entry as well, so it needs more detail
*/
public class Entry implements DataObject {
	public final static MathContext FORMAT=new MathContext(2);	//uses HALF_UP rounding mode

	public long rowid;
	public String _key;
	public String txnid;		//transaction id
	public int acct;			//account number
	public String sub;			//sub-account.  use for clients
	public BigDecimal debit;
	public BigDecimal credit;

	//for timesheet entries
	public DateYMD timesheet_date;	//I call this timesheet_date instead of time_date because that looks stupid
									//it is for time entries, on the date when it occurred
									//this is unrelated to transaction date
	public String timesheet_time;	//again, unrelated to transaction time
	public double timesheet_elapsed;			//the
	public String timesheet_desc;

	public Entry() {}

	//convenience method
	//obviously only use one of dr and cr
	public Entry(int acct,String dr,String cr) throws NumberFormatException {
		this.acct=acct;
		if (dr!=null && !dr.equals("0.00")) {
			debit=new BigDecimal(dr,FORMAT);
		}
		if (cr!=null && !cr.equals("0.00")) {
			credit=new BigDecimal(cr,FORMAT);
		}
	}

	public void setDebit(String s) {debit=new BigDecimal(s,FORMAT);}
	public void setDebit(double d) {debit=new BigDecimal(d,FORMAT);}
	public void setCredit(String s) {credit=new BigDecimal(s,FORMAT);}
	public void setCredit(double d) {credit=new BigDecimal(d,FORMAT);}

	public String getTableName() {return "Entry";}

	public String[] fields() {return new String[]{"txnid","acct","sub","debit","credit","timesheet_date","timesheet_time","timesheet_elapsed","timesheet_desc"};}

	//default sort order
	public String index() {return "txnid,acct";}

	public String getKey() {return _key;}
}
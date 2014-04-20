package pacioli.report;
import pacioli.db.*;
import pacioli.util.DateYMD;
import pacioli.ui.Display;
import pacioli.table.Client;

/** Need to work on total or balance
*/
public class AccountDetail implements Report {
	DataSource ds;
	Display display;
	public final static String COLOR="#00FF99"; //a shade of green

	public void init(DataSource ds,Display disp) {
		this.ds=ds;
		display=disp;
	}
	public String getName() {
		return "Account Detail";
	}

	public void setStartDate(pacioli.util.DateYMD d) {} //not implemented
	public void setEndDate(pacioli.util.DateYMD d) {} //not implemented

	/**
	*/
	public String print(String accountID) {
		if (accountID==null) {return "";}
		display.setTitle("Account Detail");
		StringBuffer sb=new StringBuffer();

		try {
			//open connections
			Conn c = ds.getConnection();

			//account name
			String sql="SELECT key,number,name from pacioli_table_Account WHERE key='"+accountID+"'";
			System.out.println(sql);
			Row r=c.query(sql);
			boolean b=r.next();
			if (!b) {
				//this should never happen
				throw new DSX(ErrorCode.PACIOLI_DB_SELECT,"no account found for key "+accountID);
			}
			int acctnum=r.getInt(1);
			String acctname=r.getString(2);
			r.close();
			if (acctnum!=0) {
				acctname=String.valueOf(acctnum)+" "+acctname;
			}

			sql="SELECT key,date,type,name,debit_acct,credit_acct,amount FROM pacioli_table_Action ";
			sql+="WHERE (debit_acct='"+accountID+"' or credit_acct='"+accountID+"') ";
			sql+="AND completed=1 AND memo=0 AND deleted=0 ";
			sql+="ORDER BY date,key";
			System.out.println(sql);

			r = c.query(sql);

			sb.append("<table border=1>");
			sb.append("<tr bgcolor="+COLOR+">");
			sb.append("<td colspan=6><b>"+acctname+"</b></td></tr>");
			sb.append("<tr bgcolor="+COLOR+">");
			sb.append("<td><b>Date</b></td>");
			sb.append("<td><b>Key</b></td>");
			sb.append("<td><b>Type</b></td>");
			sb.append("<td><b>Name</b></td>");
			sb.append("<td><b>Debit</b></td>");
			sb.append("<td><b>Credit</b></td>");
			sb.append("</tr>");

			boolean colored=true;

			while (r.next()) {
				colored=!colored;
				String key=r.getString(0);
				String date=r.getString(1);
				String type=r.getString(2);
				String name=r.getString(3);
				String debit_acct=r.getString(4);
				String debit="";
				String credit_acct=r.getString(5);
				String credit="";
				double damt=r.getDouble(6);
				String samt=String.format("%.2g%n", damt);

				if (debit_acct.equals(accountID)) {
					debit=samt;
				} else if (credit_acct.equals(accountID)) {
					credit=samt;
				}

				if (colored) {
					sb.append("<tr bgcolor="+COLOR+">");
				} else {
					sb.append("<tr>");
				}
				sb.append("<td>"+date+"</td><td>"+key+"</td><td>"+type+"</td><td>"+name+"</td><td>"+debit+"</td><td>"+credit+"</td></tr>");
			}
			sb.append("</table>");
			c.close();
		} catch (Exception x) {
			x.printStackTrace();
		}

		//display it
		return sb.toString();
	}

}
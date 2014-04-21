package pacioli.report;
import pacioli.db.*;
import pacioli.table.Account;
import pacioli.table.AccountType;
import pacioli.util.DateYMD;
import pacioli.ui.Display;


public class ListAccounts implements Report {
	DataSource ds;
	Display disp;

	public void init(DataSource ds,Display disp) {
		this.ds=ds;
		this.disp=disp;
	}
	public String getName() {
		return "List Accounts";
	}
	public void setStartDate(DateYMD d) {} //not implemented
	public void setEndDate(DateYMD d) {} //not implemented

	/** The id is not used.
	*/
	public String print(String id) {
		disp.setTitle("List Accounts");
		StringBuffer sb=new StringBuffer();

		try {
			Conn cx=ds.getConnection();
			Row r=cx.selectAll("pacioli.table.Account",new String[]{"type","name"});

			sb.append("<table>");
			sb.append("<tr><td style='border-bottom: 1px solid #000000;'><b>Key</b></td>");
			sb.append("<td style='border-bottom: 1px solid #000000;'><b>Type</b></td>");
			sb.append("<td style='border-bottom: 1px solid #000000;'><b>Number</b></td>");
			sb.append("<td style='border-bottom: 1px solid #000000;'><b>Name</b></td><tr>");
			sb.append("<td style='border-bottom: 1px solid #000000;'><b>Desc</b></td><tr>");

			while (r.next()) {
				Account a=(Account)r.getRow();
				String type=AccountType.lookupName(a.type);
				String number=String.valueOf(a.number);
				if (a.number==0) {
					number="";
				}
				sb.append("<tr><td>"+r.key()+"</td><td>"+type+"</td><td>"+number+"</td><td>"+a.name+"</td><td>"+a.desc+"</td></tr>");
			}
			sb.append("</table>");
			cx.close();
		} catch (Exception x) {
			x.printStackTrace();
		}

		//display it
		return sb.toString();
	}
}
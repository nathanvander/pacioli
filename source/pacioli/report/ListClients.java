package pacioli.report;
import pacioli.db.*;
import pacioli.util.DateYMD;
import pacioli.ui.Display;
import pacioli.table.Client;


public class ListClients implements Report {
	DataSource ds;
	Display display;

	public void init(DataSource ds,Display disp) {
		this.ds=ds;
		display=disp;
	}
	public String getName() {
		return "List Clients";
	}
	public void setStartDate(pacioli.util.DateYMD d) {} //not implemented
	public void setEndDate(pacioli.util.DateYMD d) {} //not implemented

	/** The id is not used.
	*/
	public String print(String id) {
		display.setTitle("List Clients");
		StringBuffer sb=new StringBuffer();

		try {
			//open connections
			Conn c = ds.getConnection();
			Row r = c.selectAll("pacioli.table.Client",new String[]{"lastname","firstname"});

			sb.append("<table>");
			sb.append("<tr><td style='border-bottom: 1px solid #000000;'><b>Key</b></td>");
			sb.append("<td style='border-bottom: 1px solid #000000;'><b>Lastname, Firstname</b></td>");
			sb.append("<td style='border-bottom: 1px solid #000000;'><b>Phone</b></td></tr>");

			while (r.next()) {
				String key=r.key();
				Client cli=(Client)r.getRow();
				sb.append("<tr><td>"+key+"</td><td>"+cli.lastname+", "+cli.firstname+"</td><td>"+cli.phone+"</td></tr>");
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
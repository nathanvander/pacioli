package pacioli.report;
import pacioli.db.*;
import pacioli.table.Client;
import pacioli.util.DateYMD;
import pacioli.ui.Display;

public class ClientDetail implements Report {
	DataSource ds;
	Display disp;

	public void init(DataSource ds, Display disp) {
		this.ds=ds;
		this.disp=disp;
		System.out.println("ClientDetail init");
	}
	public String getName() {
		return "Client Detail";
	}
	public void setStartDate(DateYMD d) {} //not implemented
	public void setEndDate(DateYMD d) {} //not implemented

	public String print(String id) {
		if (id==null) {return "";}
		disp.setTitle("Client Detail");
		StringBuffer sb=new StringBuffer();

		Client c=null;

		try {
			//open connections
			Conn cx = ds.getConnection();
			c=(Client)cx.get(id,"pacioli.table.Client");
			cx.close();
		} catch (DSX x) {
			System.out.println(x.getErrorCode());
			x.printStackTrace();
		}

		sb.append("<table border=1>");
		sb.append("<tr><td>Key</td><td>"+id+"</td>");
		sb.append("<tr><td>First Name</td><td>"+c.firstname+"</td>");
		sb.append("<tr><td>Last Name</td><td>"+c.lastname+"</td>");
		sb.append("<tr><td>Phone</td><td>"+c.phone+"</td>");
		sb.append("<tr><td>Phone2</td><td>"+c.phone2+"</td>");
		sb.append("<tr><td>Email</td><td>"+c.email+"</td>");
		sb.append("<tr><td>Address 1</td><td>"+c.address1+"</td>");
		sb.append("<tr><td>Address 2</td><td>"+c.address2+"</td>");
		sb.append("<tr><td>Rate</td><td>"+c.rate+"</td>");
		sb.append("<tr><td>Notes</td><td>"+c.desc+"</td>");
		sb.append("<tr><td>Closed?</td><td>"+c.closed+"</td>");
		sb.append("</table>");

		//display it
		return sb.toString();
	}
}
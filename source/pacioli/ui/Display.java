package pacioli.ui;
import pacioli.db.DataSource;

/**
*  This is used to send messages to the user.
*
*/
public interface Display {
	//not sure this should be in here
	public DataSource getDataSource();

	public String getVersion();

	public void setTitle(String t);

	public String getDesktop();

	//this is the main body of text
	public void setDesktop(String t);

	public void setStatus(String t);

	public void setStatus2(String t);

	public String getClientID();

	public void setClientID(String cid);
}
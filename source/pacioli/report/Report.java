package pacioli.report;
import pacioli.db.DataSource;
import pacioli.ui.Display;
import pacioli.util.DateYMD;

/**
* A report can allow you to get formatted information from the database.
* This probably needs some tweaking.
*/

public interface Report {
	public void init(DataSource ds, Display disp);
	public String getName();
	public void setStartDate(DateYMD d);
	public void setEndDate(DateYMD d);

	/** The output will be in HTML format.
	*  The id will be report specific, but may be account number, customer number, or transaction number.
	*/
	public String print(String id);
}
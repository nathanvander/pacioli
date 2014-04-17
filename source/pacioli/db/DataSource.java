package pacioli.db;

/** This is designed so you just keep a handle to the datasource and open connections as needed.
* That's because connections aren't thread-safe, and dialog are in new threads.
*/
public interface DataSource {
	public Conn getConnection() throws DSX;
}
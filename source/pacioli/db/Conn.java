package pacioli.db;

/** This is the public interface to the database.
*  Keys are in base-60, just to be different.
*
*  Only public fields in Objects are stored.
*
*  className means the name of the class with dots.
*  tableName means the name of the class with underscores replacing dots
*/
public interface Conn {

	//returns the number of this id, for reference
	public int getConnId();

	//create a table based on the classname, like pacioli.table.Account
	public void createTable(String className) throws DSX;

	/**
	*  Insert an object into the datastore and return the key.  This will automatically create the table if needed.
	* Keys are generated automatically, and will be the first row in the database.
	*/
	public String insert(Object o) throws DSX;

	public void update(String key,Object o) throws DSX;

	public void delete(String key,String className) throws DSX;

	public Object get(String key,String className) throws DSX;

	//sort fields can be null, in which case they will be sorted by key
	public Row selectAll(String className,String[] sortfields) throws DSX;

	public Row query(String sql) throws DSX;

	public void exec(String sql) throws DSX;

	//you can't use this after it is closed
	public void close();
}
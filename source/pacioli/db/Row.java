package pacioli.db;

/**
* This is the equivalent of ResultSet
*/
public interface Row {

	/** Call this before retrieving data. If it returns true, there is data to be read.
	* If it is false, there is no more data
	*/
	public boolean next() throws DSX;

	//You can also retrieve this by getting the string from column zero
	public String key() throws DSX;

	//get number of columns
	public int getColumnCount() throws DSX;

	//will return either:
	//	SQLITE_NULL = 5
	//	SQLITE_INTEGER = 1
	//	SQLITE_TEXT = 3
	//	SQLITE_BLOB = 4
	//	SQLITE_FLOAT = 2
	public int getColumnType(int col) throws DSX;

	//return the name of the column
	public String getColumnName(int col) throws DSX;

	public String getString(int col) throws DSX;

	public int getInt(int col) throws DSX;

	public long getLong(int col) throws DSX;

	public double getDouble(int col) throws DSX;


	//return the value as an object.  The object type will vary
	//   null if SQLITE_NULL
	//	 String if text
	//   Integer or Long if SQLITE_INTEGER
	//	 Double if Float
	//	 byte[] if blob
	public Object getValue(int col) throws DSX;

	//just turn the whole thing into an object
	//this doesn't work with joins
	public Object getRow() throws DSX;

	public void close();
}
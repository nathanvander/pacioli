package pacioli.ui;

/**
* The Cleaner interface is used to make things clean.
* This is the place to shut things down cleanly.  Mainly used for database connections.
*/

public interface Cleaner {
	//this can't throw an exception since it is shutting down
	public void clean();
}
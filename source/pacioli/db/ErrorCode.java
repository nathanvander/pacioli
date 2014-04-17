package pacioli.db;

/** This is a complete list of all possible error codes
*/

public class ErrorCode {
	public static final int UNKNOWN = -1;  //this is the default, we want more detail than this

	//from SQLITE
	public static final int SQLITE_OK = 0;   /* Successful result */
	public static final int SQLITE_ERROR = 1;   /* SQL error or missing database */
	public static final int SQLITE_INTERNAL = 2;   /* Internal logic error in SQLite */
	public static final int SQLITE_PERM = 3;   /* Access permission denied */
	public static final int SQLITE_ABORT = 4;   /* Callback routine requested an abort */
	public static final int SQLITE_BUSY = 5;   /* The database file is locked */
	public static final int SQLITE_LOCKED = 6;   /* A table in the database is locked */
	public static final int SQLITE_NOMEM = 7;   /* A malloc() failed */
	public static final int SQLITE_READONLY = 8;   /* Attempt to write a readonly database */
	public static final int SQLITE_INTERRUPT = 9;   /* Operation terminated by sqlite3_interrupt()*/
  	public static final int SQLITE_IOERR = 10;   /* Some kind of disk I/O error occurred */
  	public static final int SQLITE_CORRUPT = 11;   /* The database disk image is malformed */
  	public static final int SQLITE_NOTFOUND = 12;   /* NOT USED. Table or record not found */
  	public static final int SQLITE_FULL = 13;   /* Insertion failed because database is full */
  	public static final int SQLITE_CANTOPEN = 14;   /* Unable to open the database file */
  	public static final int SQLITE_PROTOCOL = 15;   /* NOT USED. Database lock protocol error */
  	public static final int SQLITE_EMPTY = 16;   /* Database is empty */
  	public static final int SQLITE_SCHEMA = 17;   /* The database schema changed */
  	public static final int SQLITE_TOOBIG = 18;   /* String or BLOB exceeds size limit */
  	public static final int SQLITE_CONSTRAINT = 19;   /* Abort due to constraint violation */
  	public static final int SQLITE_MISMATCH = 20;   /* Data type mismatch */
  	public static final int SQLITE_MISUSE = 21;   /* Library used incorrectly */
  	public static final int SQLITE_NOLFS = 22;   /* Uses OS features not supported on host */
  	public static final int SQLITE_AUTH = 23;   /* Authorization denied */
  	public static final int SQLITE_FORMAT = 24;   /* Auxiliary database format error */
  	public static final int SQLITE_RANGE = 25;   /* 2nd parameter to sqlite3_bind out of range */
  	public static final int SQLITE_NOTADB = 26;   /* File opened that is not a database file */
  	public static final int SQLITE_ROW = 100;  /* sqlite_step() has another row ready */
  	public static final int SQLITE_DONE = 101;  /* sqlite_step() has finished executing */

	//from SqlLite4Java
  	public static final int INTARRAY_INUSE = 210;   /* Attempting to re-bind array while a cursor is traversing old values */
  	public static final int INTARRAY_INTERNAL_ERROR = 212;  /* Some other problem */
  	public static final int INTARRAY_DUPLICATE_NAME = 213;  /* Trying to create an array with a duplicate name */

	//extended error codes - 1st group
	public static final int SQLITE_BUSY_RECOVERY = 261;
	public static final int SQLITE_LOCKED_SHAREDCACHE = 262;
	public static final int SQLITE_READONLY_RECOVERY = 264;
  	public static final int SQLITE_IOERR_READ = 266;
  	public static final int SQLITE_CORRUPT_VTAB = 267;
  	public static final int SQLITE_CANTOPEN_NOTEMPDIR = 270;

  	//extended error codes - 1st group - more
  	public static final int SQLITE_READONLY_CANTLOCK = 520;
  	public static final int SQLITE_IOERR_SHORT_READ = 522;
  	public static final int SQLITE_IOERR_WRITE = 778;
  	public static final int SQLITE_IOERR_FSYNC = 1034;
  	public static final int SQLITE_IOERR_DIR_FSYNC = 1290;
  	public static final int SQLITE_IOERR_TRUNCATE = 1546;
  	public static final int SQLITE_IOERR_FSTAT = 1802;
  	public static final int SQLITE_IOERR_UNLOCK = 2058;
  	public static final int SQLITE_IOERR_RDLOCK = 2314;
  	public static final int SQLITE_IOERR_DELETE = 2570;
  	public static final int SQLITE_IOERR_BLOCKED = 2826;
  	public static final int SQLITE_IOERR_NOMEM = 3082;
  	public static final int SQLITE_IOERR_ACCESS = 3338;
  	public static final int SQLITE_IOERR_CHECKRESERVEDLOCK = 3594;
  	public static final int SQLITE_IOERR_LOCK = 3850;
  	public static final int SQLITE_IOERR_CLOSE = 4106;
  	public static final int SQLITE_IOERR_DIR_CLOSE = 4362;
  	public static final int SQLITE_IOERR_SHMOPEN = 4618;
  	public static final int SQLITE_IOERR_SHMSIZE = 4874;
  	public static final int SQLITE_IOERR_SHMLOCK = 5130;
  	public static final int SQLITE_IOERR_SHMMAP = 5386;
 	public static final int SQLITE_IOERR_SEEK = 5642;

	//wrapper errors
  	public static final int WRAPPER_WEIRD = -99;  //Something strange happened.
  	public static final int WRAPPER_CONFINEMENT_VIOLATED = -98;  //Method called in thread that wasn't allowed.
	public static final int WRAPPER_NOT_OPENED = -97; //* Wasn't opened
  	public static final int WRAPPER_STATEMENT_DISPOSED = -96;  //* Statement disposed
	public static final int WRAPPER_NO_ROW = -95;  //* column() requested when no row returned
	public static final int WRAPPER_COLUMN_OUT_OF_RANGE = -94;
	public static final int WRAPPER_BLOB_DISPOSED = -93;
	public static final int WRAPPER_MISUSE = -92;
	public static final int WRAPPER_CANNOT_LOAD_LIBRARY = -91;
  	public static final int WRAPPER_BACKUP_DISPOSED = -113;
  	public static final int WRAPPER_INVALID_ARG_1 = -11;
  	public static final int WRAPPER_INVALID_ARG_2 = -12;
  	public static final int WRAPPER_INVALID_ARG_3 = -13;
  	public static final int WRAPPER_INVALID_ARG_4 = -14;
  	public static final int WRAPPER_INVALID_ARG_5 = -15;
  	public static final int WRAPPER_INVALID_ARG_6 = -16;
  	public static final int WRAPPER_INVALID_ARG_7 = -17;
  	public static final int WRAPPER_INVALID_ARG_8 = -18;
  	public static final int WRAPPER_INVALID_ARG_9 = -19;
  	public static final int WRAPPER_CANNOT_TRANSFORM_STRING = -20;
  	public static final int WRAPPER_CANNOT_ALLOCATE_STRING = -21;
  	public static final int WRAPPER_OUT_OF_MEMORY = -22;
  	public static final int WRAPPER_WEIRD_2 = -199;
	public static final int WRAPPER_USER_ERROR = -999;

	//java errors
	public static final int JAVA_LANG_NULLPOINTER = 127;
	public static final int JAVA_IO_IOEXCEPTION = 128;
	public static final int JAVA_IO_FILENOTFOUND = 129;
	public static final int JAVA_LANG_RUNTIME = 130;
	public static final int JAVA_LANG_REFLECTIVEOPERATION = 132;
	public static final int JAVA_LANG_CLASSNOTFOUND = 144;
	public static final int JAVA_LANG_ILLEGALARGUMENT = 145;
	public static final int JAVA_LANG_ILLEGALACCESS = 146;
	public static final int JAVA_LANG_INSTANTIATION = 147;
	public static final int JAVA_LANG_NOSUCHFIELD = 148;
	public static final int JAVA_LANG_NUMBERFORMAT = 149;

	//---------------------------------------------------------
	public static final int PACIOLI_DB_CREATE = 32;	//error in create statement
	public static final int PACIOLI_DB_INSERT = 36;  //failed to insert object
	public static final int PACIOLI_DB_SELECT = 40;
	public static final int PACIOLI_DB_GET = 41;   	//failed to retrieve requested record
	public static final int PACIOLI_DB_UPDATE_NULL = 44;  //failed to update object because it is null
	public static final int PACIOLI_DB_UPDATE_MISSING = 45;  //can't update because object is not in table
	public static final int PACIOLI_DB_UPDATE_FAILED = 46;  //the update sql statement didn't actually affect any rows
	public static final int PACIOLI_DB_DELETE_NULL = 48;  //failed to delete object because it is null
	public static final int PACIOLI_DB_DELETE_FAILED = 49;  //the delete sql statement succeeded, but didn't delete any rows
	public static final int PACIOLI_DB_COUNTER = 92;  	//error in Counter.init(), or can't get nextid
	public static final int PACIOLI_UTIL_BASE60 = 96;   //input not in range 0..59

}
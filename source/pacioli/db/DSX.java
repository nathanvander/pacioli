package pacioli.db;
import com.almworks.sqlite4java.*;

/**
* All exceptions should use this class for consistency.
*/
public class DSX extends Exception {
	int errorCode;

	public DSX(int errorCode,String message) {
		super(message);
		this.errorCode=errorCode;
	}

	public int getErrorCode() {
		return errorCode;
	}
}
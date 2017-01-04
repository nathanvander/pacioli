package pacioli2;

/**
* An OutOfBalanceException is thrown when an accounting transaction
* is attempted to be committed but it doesn't balance.
* Maybe it should have a more general name, but I think this will work for now.
*/
public class OutOfBalanceException extends Exception {
	public OutOfBalanceException() {}
}
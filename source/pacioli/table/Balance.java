package pacioli.table;

/** This is used to store balances for report writing.
*
*	Period can be either YYYY for the year, or YYYY-MM for the month
*/

public class Balance {
	public String period;		//this is in the format YYYY-MM or YYYY
	public String account_id;	//account key, not account number
	public double total;
}
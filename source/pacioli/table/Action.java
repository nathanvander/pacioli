package pacioli.table;
import java.awt.Choice;

/**
* Everything is an action.  Time is money, and an accounting transaction.  This is called Action, which means both something
* that is done and a Transaction.
*
* The type is very important.
*		Acctg = an accounting transaction, not involving cash
*		Bill = receiving and scheduling a bill to be paid.  The date is the due date or scheduled time to pay it.
*		Document = a description of an external document.  Check the memo box
*		Drafting = time spent drafting a motion or letter
*		EFile = filing or sending a document, via Lexis, ICCES or ECF
*		Email_Recvd = an email received, with a summary of the message.
*		Email_Sent = an email sent, with a summary of the message
*		EReceipt = receiving a document.  The opposite of efile.  You've been served!
*		Event = a scheduled time and place, i.e. Court date or meeting.
*		Mail_Recvd = U.S. mail
*		Mail_Sent = U.S. mail sent
*		Memo = a note about something, non-billable.  Check the memo box
*		Money = receiving cash or paying cash.
*		Phone = a phone call with a summary of the conversation
*		Task = something that has to be done.  A to-do list item.  Date is the deadline.
*		Text = text message sent or received.  Record only if really important from a client
*		VMail_Recvd = a voicemail message received
*		VMail_Sent = a voicemail message left
*
*	Amount is stored as a double, which is stored as a REAL (8-byte floating point number) in the database.  Make sure
*   to round this before and after multiplying it.
*
*	Date is stored as YYYY-MM-DD
*/

public class Action {
	public String client_id;	//this will always have an entry. id means the key of the client
								//Use "general" if nothing else applies
	public String refnum;		//usually null, can be check number or invoice number
	public String type;			//type is "Task", "Event", "Phone", etc.
	public String name;			//short name or description of the action
	public String date_entered;	//date entered, as opposed to the transaction date, which might be different
	public String date;  		//YYYY-MM-DD.  Also used for a deadline or scheduled time to complete
	public String time;			//format HH:MM, no AM/PM
	public String journal;		//this is the type of transaction, i.e. CashReceipt
	public String otr_prty;		//who did you talk to?
	public float elapsed;		//this is the amount of time spent
	public String desc;  		//textarea, long description including location if event
	public String debit_acct;	//account to debit.  This is the object id of the account, not the account number
	public String credit_acct;
	public double amount;			//always a positive number
	public boolean completed=false;	//this is true if the task is complete.  if false, this is planned, in progress, or budgeted
	public boolean memo=false;		//check true if this is not an accounting transaction
	public boolean deleted=false;  //if this is true, then ignore the transaction
									//also use for a cancelled transaction

	//get possible types
	public static Choice getTypes() {
		Choice list = new Choice();
		list.add("Acctg");
		list.add("Bill");
		list.add("Document");
		list.add("Drafting");
		list.add("Efile");
		list.add("Email_Recvd");
		list.add("Email_Sent");
		list.add("Ereceipt");
		list.add("Event");
		list.add("Mail_Recvd");
		list.add("Mail_Sent");
		list.add("Memo");
		list.add("Money");
		list.add("Phone");
		list.add("Task");
		list.add("Text");
		list.add("Vmail_Recvd");
		list.add("Vmail_Sent");
		return list;
	}
}
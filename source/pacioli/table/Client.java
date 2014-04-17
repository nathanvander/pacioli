package pacioli.table;

public class Client {
	public String type;			//e.g. CR for Criminal
	public String firstname;
	public String lastname;
	public String phone;
	public String phone2;
	public String email;
	public String address1;
	public String address2;
	public String date_opened;
	public String court;
	public String casenum;
	public int rate;		//custom rate to use.  The default rate comes from Sys
	public String desc;
	public boolean closed=false;   	//will be false unless closed
	public boolean deleted=false;	//if this is true, it can be deleted
}
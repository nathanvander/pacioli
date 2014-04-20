package pacioli.module;
import java.awt.*;
import java.awt.event.*;
import javax.swing.JEditorPane;
import pacioli.ui.*;
import pacioli.report.*;
import pacioli.db.*;
import pacioli.util.DateYMD;
import pacioli.table.*;

/**
* Account Details will be implemented later, to let you view all transactions involving the current account.
*/

public class AccountModule extends Menu implements Module,ActionListener {
	Frame parent;
	Display display;

	public AccountModule() {
		super("Accounts");

		MenuItem listAccounts=new MenuItem("List Accounts");
		MenuItem addAccount=new MenuItem("Add Account");
		MenuItem editAccount=new MenuItem("Edit Account");
		MenuItem accountDetails=new MenuItem("Account Details");
		add(listAccounts);
		add(addAccount);
		add(editAccount);
		add(accountDetails);
		listAccounts.addActionListener(this);
		addAccount.addActionListener(this);
		editAccount.addActionListener(this);
		accountDetails.addActionListener(this);
	}

	public void init(Frame p,Display d) {
		parent=p;
		display=d;
	}

	public void actionPerformed(ActionEvent e) {
		//sanity check
		String cmd=e.getActionCommand();
		System.out.println(cmd);
		DataSource ds=display.getDataSource();

		if (cmd.equals("List Accounts")) {
			ListAccounts la=new ListAccounts();
			la.init(ds,display);
			String report=la.print(null);
			display.setDesktop(report);
		} else if (cmd.equals("Add Account")) {
			new AccountDialog(parent,ds,null);
		} else if (cmd.equals("Edit Account")) {
			new SelectAccountDialog(parent,display,"Edit");
		} else if (cmd.equals("Account Details")) {
			//yea its the same thing
			new SelectAccountDialog(parent,display,"View Details");
		}
	}

	//------------------------------------------------
	class AccountDialog extends Dialog implements ActionListener,Cleaner {
		Conn connex;
		TextComponent t_number;
		TextComponent t_name;  	//name
		Choice li_type;  		//type
		Choice li_special;
		Checkbox cb_deleted;		//deleted.  Default is false
		TextComponent t_desc;  	//desc
		String aid;			//account id

		public AccountDialog(Frame f,DataSource ds,String aid) {
			super(f,"Account Dialog");
			this.aid=aid;
			this.setSize(400,300);
			addWindowListener(new DialogListener(this,this));
			setLayout(new BorderLayout());

			//get data
			Account acct=null;
			try {
				connex = ds.getConnection();

				if (aid==null) {
					acct=new Account();
				} else {
					acct=(Account)connex.get(aid,"pacioli.table.Account");
				}
			} catch (DSX x) {
				System.out.println(x.getErrorCode());
			}

			//display it
			Panel top=new Panel(new GridLayout(5,2));
			add(top,BorderLayout.NORTH);

			Panel center=new Panel(new FlowLayout());
			add(center,BorderLayout.CENTER);

			Panel bottom=new Panel(new FlowLayout());
			add(bottom,BorderLayout.SOUTH);

			//account number
			top.add(new Label("Account number"));
			t_number = new TextField(String.valueOf(acct.number), 20);
			top.add(t_number);

			//account name
			top.add(new Label("Name"));
			t_name = new TextField(acct.name, 20);
			top.add(t_name);

			//type
			top.add(new Label("Type"));
			li_type = AccountType.getAccountTypes();
			String sel_type=AccountType.lookupName(acct.type);
			li_type.select(sel_type);
			top.add(li_type);

			//Special
			top.add(new Label("Special"));
			li_special = Special.getSpecials();
			String sel_special=Special.lookupName(acct.special);
			li_special.select(sel_special);
			top.add(li_special);

			//deleted
			top.add(new Label("Deleted?"));
			cb_deleted = new Checkbox("deleted",acct.deleted);
			top.add(cb_deleted);

			//description
			center.add(new Label("Description"));
			t_desc = new TextArea(acct.desc,3,50,TextArea.SCROLLBARS_VERTICAL_ONLY);
			center.add(t_desc);


			Button bp=null;
			if (aid==null) {
				bp=new Button("Add");
			} else {
				bp=new Button("Update");
			}
			bp.addActionListener(this);
			bottom.add(bp);
			this.setVisible(true);
		}

		public void actionPerformed(ActionEvent e) {
			String cmd=e.getActionCommand();

			Account a=new Account();

			//number
			String snum=null;
			try {
				snum=t_number.getText();
				if (snum!=null && !snum.equals("")) {
					a.number=Integer.parseInt(snum);
				}
			} catch (Exception x) {
				//will happen if srate is not an int
				//not fatal
				System.out.println(snum+" is not a number");
			}

			//name
			a.name=t_name.getText();
			if (a.name==null || a.name.equals("")) {
				return;  //problem, an account must have a name
			}

			//type
			String stype=li_type.getSelectedItem();
			//now turn the string into a number
			if (stype!=null && !stype.equals("")) {
				a.type=AccountType.lookupOrdinal(stype);
			}

			//special
			String spec=li_special.getSelectedItem();
			//now get the code associated with the string
			if (spec!=null && !spec.equals("")) {
				Special t=Special.lookupByString(spec);
				a.special=t.code();
			}

			//deleted
			a.deleted=cb_deleted.getState();

			//description
			a.desc=t_desc.getText();

			try {
				if (cmd.equals("Add")) {
					String k=connex.insert(a);
					System.out.println("insert successful, key="+k);
				} else if (cmd.equals("Update")) {
					//update this in database
					connex.update(aid,a);
					System.out.println("update successful, key="+aid);
				}
				connex.close();
			} catch (Exception x) {
				x.printStackTrace();
			}
			this.dispose();
		}

		public void clean() {
			connex.close();
		}
	}

	//------------------------------------------------
	//first
	class SelectAccountDialog extends Dialog implements ActionListener,Cleaner {
		Frame parent;
		Display display;
		DataSource ds;
		List list;

		//button is the button to display, must be either "Edit" or "View Details"
		public SelectAccountDialog(Frame p,Display d,String button) {
			super((Frame)p,"Select Account Dialog");
			this.setSize(350,300);
			parent=p;
			display=d;
			ds=display.getDataSource();

			addWindowListener(new DialogListener(this,this));
			setLayout(new BorderLayout());

			Panel top=new Panel(new FlowLayout());
			add(top,BorderLayout.NORTH);
			list=new List(10);

			try {
				Conn connex=ds.getConnection();
				populateList(connex,list);
				connex.close();
			} catch (DSX x) {
				System.out.println(x.getErrorCode());
				x.printStackTrace();
			}

			top.add(list);

			Panel south=new Panel(new FlowLayout());
			add(south,BorderLayout.SOUTH);

			//this will be either Edit or View Details
			Button bu=new Button(button);
			bu.addActionListener(this);
			south.add(bu);

			this.setVisible(true);
		}

		public void actionPerformed(ActionEvent e) {
			String cmd=e.getActionCommand();
			System.out.println(cmd);
			String selected=list.getSelectedItem();
			//System.out.println("selected="+selected);

			if (cmd.equals("Edit")) {
				if (selected!=null) {
					String[] sa=selected.split(":");
					String aid=sa[0];
					new AccountDialog(parent,ds,aid);
				}
			} else if (cmd.equals("View Details")) {
				if (selected!=null) {
					String[] sa=selected.split(":");
					String aid=sa[0];

					AccountDetail ad=new AccountDetail();
					ad.init(ds,display);
					String report=ad.print(aid);
					display.setDesktop(report);
				}

			}
			this.dispose();
		}

		public void clean() {
			//nothing to do here.  Connection already closed, and subdialogs get their own connections.
		}

		//pass in the List object and this will populate it
		//this should be static but the compiler complains
		private void populateList(Conn c,List list) throws DSX {
			Row r=c.selectAll("pacioli.table.Account",new String[]{"type","name"});

			//System.out.println("populating list");
			while (r.next()) {
				Account a=(Account)r.getRow();
				if (!a.deleted) {
					String s=r.key()+": "+AccountType.lookupName(a.type)+": "+a.name;
					list.add(s);
				}
			}
		}
	}	//SelectAccountDialog
}

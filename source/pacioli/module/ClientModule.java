package pacioli.module;
import java.awt.*;
import java.awt.event.*;
import javax.swing.JEditorPane;
import pacioli.ui.*;
import pacioli.report.*;
import pacioli.db.*;
import pacioli.util.DateYMD;
import pacioli.table.Client;
import pacioli.table.Sys;


public class ClientModule extends Menu implements Module,ActionListener {
	Frame parent;
	Display display;
	public static final String[] sortFields=new String[]{"lastname","firstname"};

	public ClientModule() {
		super("Clients");
		MenuItem listClients=new MenuItem("List Clients");
		MenuItem selectClient=new MenuItem("Select Client");
		MenuItem addClient=new MenuItem("Add Client");
		MenuItem editClient=new MenuItem("Edit Client");
		MenuItem clientDetail=new MenuItem("Client Detail");
		add(listClients);
		add(selectClient);
		add(addClient);
		add(editClient);
		add(clientDetail);
		listClients.addActionListener(this);
		selectClient.addActionListener(this);
		addClient.addActionListener(this);
		editClient.addActionListener(this);
		clientDetail.addActionListener(this);
	}

	public void init(Frame p,Display d) {
		parent=p;
		display=d;
	}

	public void actionPerformed(ActionEvent e) {
		String cmd=e.getActionCommand();
		System.out.println(cmd);

		if (cmd.equals("List Clients")) {
			//System.out.println("List Clients");
			ListClients lc=new ListClients();
			lc.init(display.getDataSource(),display);
			String report=lc.print(null);
			display.setDesktop(report);
		} else if (cmd.equals("Select Client")) {
			new SelectClientDialog(parent,display);
		} else if (cmd.equals("Add Client")) {
			new ClientDialog(parent,display,null);
		} else if (cmd.equals("Edit Client")) {
			String cid=display.getClientID();
			new ClientDialog(parent,display,cid);
		} else if (cmd.equals("Client Detail")) {
			String cid=display.getClientID();
			ClientDetail cd=new ClientDetail();
			cd.init(display.getDataSource(),display);
			String report=cd.print(cid);
			display.setDesktop(report);
		}
	}

	//==============================================================

	class ClientDialog extends Dialog implements ActionListener,Cleaner {
		Frame parent;
		Display display;
		Conn connex;
		TextComponent t1; //type
		TextComponent t2; //firstname
		TextComponent t3; //lastname
		TextComponent t4; //phone
		TextComponent t5; //phone2
		TextComponent t6; //email
		TextComponent t7; //address1
		TextComponent t8; //address2
		TextComponent t9; //date_opened
		TextComponent t10;  //court;
		TextComponent t11;  //casenum;
		TextComponent t12;  //rate;  The default rate comes from Sys
		TextComponent t13;  //desc;
		Checkbox cb1;		//closed.  Default is open=false=unchecked
		String cid;

		public ClientDialog(Frame p,Display d,String cid) {
			super((Frame)p,"Client Dialog");
			parent=p;
			display=d;
			parent.setTitle("Client Dialog");
			this.cid=cid;
			this.setSize(400,500);
			addWindowListener(new DialogListener(this,this));
			setLayout(new BorderLayout());

			Client c=null;
			try {
				DataSource ds=display.getDataSource();
				connex=ds.getConnection();

				//get data
				if (cid==null) {
					c=new Client();
				} else {
					c=(Client)connex.get(cid,"pacioli.table.Client");
				}
			} catch (DSX x) {
				System.out.println(x.getErrorCode());
				x.printStackTrace();
			}

			//display it
			Panel top=new Panel(new GridLayout(13,2));
			add(top,BorderLayout.NORTH);

			Panel center=new Panel(new FlowLayout());
			add(center,BorderLayout.CENTER);

			Panel bottom=new Panel(new FlowLayout());
			add(bottom,BorderLayout.SOUTH);

			top.add(new Label("Type"));
			t1 = new TextField(c.type, 20);
			top.add(t1);

			top.add(new Label("First Name"));
			t2 = new TextField(c.firstname, 20);
			top.add(t2);

			top.add(new Label("Last Name"));
			t3 = new TextField(c.lastname, 20);
			top.add(t3);

			top.add(new Label("Phone"));
			t4 = new TextField(c.phone, 12);
			top.add(t4);

			top.add(new Label("Phone 2"));
			t5 = new TextField(c.phone2, 12);
			top.add(t5);

			top.add(new Label("Email"));
			t6 = new TextField(c.email, 20);
			top.add(t6);

			top.add(new Label("Address 1"));
			t7 = new TextField(c.address1, 20);
			top.add(t7);

			top.add(new Label("Address 2 (City,State ZIP)"));
			t8 = new TextField(c.address2, 20);
			top.add(t8);

			top.add(new Label("Date Opened (YYYY-MM-DD)"));
			if (c.date_opened==null) {
				c.date_opened=DateYMD.getDate();
			}
			t9 = new TextField(c.date_opened, 12);
			top.add(t9);

			top.add(new Label("Court"));
			t10 = new TextField(c.court, 20);
			top.add(t10);

			top.add(new Label("Case Num"));
			t11 = new TextField(c.casenum, 20);
			top.add(t11);

			//rate
			if (c.rate==0) {
				try {
					//get default rate from system object
					Row r=connex.selectAll("pacioli.table.Sys",null);
					if (r.next()) {
						Sys sy=(Sys)r.getRow();
						System.out.println("default rate="+sy.default_rate);
						c.rate=sy.default_rate;
					}
				} catch (DSX x) {
					System.out.println(x.getErrorCode());
					x.printStackTrace();
				}
			}
			top.add(new Label("Rate (integer)"));
			t12 = new TextField(String.valueOf(c.rate), 20);
			top.add(t12);

			//closed?
			top.add(new Label("Closed?"));
			cb1 = new Checkbox("closed",c.closed);
			top.add(cb1);

			//long description
			center.add(new Label("Notes"));
			t13 = new TextArea(c.desc,3,50,TextArea.SCROLLBARS_VERTICAL_ONLY);
			center.add(t13);

			Button bp=null;
			if (cid==null) {
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

			Client c=new Client();
			c.type=t1.getText();
			c.firstname=t2.getText();
			c.lastname=t3.getText();
			c.phone=t4.getText();
			c.phone2=t5.getText();
			c.email=t6.getText();
			c.address1=t7.getText();
			c.address2=t8.getText();
			c.date_opened=t9.getText();
			c.court=t10.getText();
			c.casenum=t11.getText();

			//rate
			try {
				String srate=t12.getText();
				if (srate!=null && !srate.equals("")) {
					c.rate=Integer.parseInt(srate);
				}
			} catch (Exception x) {
				//will happen if srate is not an int
				x.printStackTrace();
			}

			//closed
			c.closed=cb1.getState();

			//description
			c.desc=t13.getText();
			//get rid of newline by replacing it with a space
			c.desc = c.desc.replaceAll("\\r?\\n"," ");

			try {
				if (cmd.equals("Add")) {
					String k=connex.insert(c);
					display.setStatus2("update successful");
					System.out.println("insert successful, key="+k);
				} else if (cmd.equals("Update")) {
					//update this in database
					connex.update(cid,c);
					display.setStatus2("update successful");
					System.out.println("update successful, key="+cid);
				}
				connex.close();
			} catch (DSX x) {
				System.out.println(x.getErrorCode());
				x.printStackTrace();
			}
			this.dispose();
		}

		public void clean() {
			connex.close();
		}
	}

	//----------------------------------------------------------
	class SelectClientDialog extends Dialog implements ActionListener,Cleaner {
		Frame parent;
		Display display;
		Conn connex;
		List list;

		public SelectClientDialog(Frame p, Display d) {
			super((Frame)p,"Select Client Dialog");
			parent=p;
			display=d;
			display.setTitle("Select Client");
			this.setSize(350,300);
			addWindowListener(new DialogListener(this, this));
			setLayout(new BorderLayout());

			Panel top=new Panel(new FlowLayout());
			add(top,BorderLayout.NORTH);
			list=new List(10);

			try {
				DataSource ds=display.getDataSource();
				connex=ds.getConnection();
				populateList(connex);
				connex.close();
			} catch (DSX x) {
				System.out.println(x.getErrorCode());
				x.printStackTrace();
			}
			top.add(list);

			Panel south=new Panel(new FlowLayout());
			add(south,BorderLayout.SOUTH);

			Button bp=new Button("Select");
			bp.addActionListener(this);
			south.add(bp);
			this.setVisible(true);
		}

		public void actionPerformed(ActionEvent e) {
			String cmd=e.getActionCommand();
			System.out.println(cmd);

			if (cmd.equals("Select")) {
				String selected=list.getSelectedItem();
				System.out.println("selected="+selected);
				if (selected!=null) {
					String[] sa=selected.split(":");
					//int i=Integer.parseInt(sa[0]);
					display.setStatus(selected);
					display.setClientID(sa[0]);
				}
			}
			this.dispose();
		}

		public void clean() {
			connex.close();
		}

		private void populateList(Conn c) throws DSX {
			//get list of open clients
			Row r=c.selectAll("pacioli.table.Client",sortFields);

			//System.out.println("populating list");
			while (r.next()) {
				String key=r.key();
				Client cli=(Client)r.getRow();
				if (!cli.closed) {
					String s=key+": "+cli.lastname+","+cli.firstname;
					list.add(s);
				}
			}
		}
	}
	//----------------------------------------------------------------

}
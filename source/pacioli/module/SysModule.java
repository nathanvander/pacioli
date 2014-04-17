package pacioli.module;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import pacioli.ui.*;
import pacioli.db.*;
import pacioli.table.*;

public class SysModule extends Menu implements Module,ActionListener {
	Frame parent;
	Display display;

	public SysModule() {
		super("System");
		MenuItem sysVariables=new MenuItem("System Variables");
		MenuItem saveScreen=new MenuItem("Save Screen");
		MenuItem about=new MenuItem("About");
		MenuItem exit=new MenuItem("Exit");
		add(sysVariables);
		add(saveScreen);
		add(about);
		add(exit);
		sysVariables.addActionListener(this);
		saveScreen.addActionListener(this);
		about.addActionListener(this);
		exit.addActionListener(this);
	}

	public void init(Frame p,Display d) {
		parent=p;
		display=d;
	}

	public void actionPerformed(ActionEvent e) {
		String cmd=e.getActionCommand();
		System.out.println(cmd);

		if (cmd.equals("System Variables")) {
			new SysVariablesDialog(parent,display);
		} else if (cmd.equals("About")) {
			about();
		} else if (cmd.equals("Save Screen")) {
			saveScreen(display.getDesktop());
		} else if (cmd.equals("Exit")) {
			display.setTitle("Exit");
			System.out.println("window closing");
			System.exit(0);
		}
	}

	public void about() {
		StringBuffer sb=new StringBuffer();
		display.setTitle("About");
		sb.append("<html><table border=1><tr><th>Pacioli ver. "+display.getVersion()+"<br>Copyright 2014 Nathan Vanderhoofven</th></tr></table></html>");
		display.setDesktop(sb.toString());
	}

	public void saveScreen(String text) {
		display.setTitle("Save Screen");
		FileDialog fd=new FileDialog(parent,"Save Screen",FileDialog.SAVE);
		fd.setFile("*.html");
		fd.show();
		String fn= fd.getFile();
		String dir=fd.getDirectory();
		String fp=dir+fn;

		System.out.println("filename="+fp);
			if (fp!=null && fp.endsWith(".html")) {
				try {
					File f=new File(fp);
					if (!f.exists()) {
						f.createNewFile();
					}
					FileWriter fw=new FileWriter(f);  //open in append mode
					BufferedWriter bw=new BufferedWriter(fw);
					bw.write(text);
					bw.flush();
					fw.close();
				} catch (Exception x) {
					x.printStackTrace();
				}
			}
			//what if it isn't html?
	}

	//====================
	class SysVariablesDialog extends Dialog implements ActionListener, Cleaner {
		Frame parent;
		Display display;
		Conn con;
		TextComponent t_company;		//company
		TextComponent t_address1;   	//company address line 1
		TextComponent t_address2;
		TextComponent t_phone;
		TextComponent t_fax;
		TextComponent t_email;
		TextComponent t_default_rate;

		String sysid=null;	//the key of the object
		Sys sy=null;

		public SysVariablesDialog(Frame parent,Display display) {
			super(parent,"System Variables Dialog");
			display.setTitle("System Variables");
			this.setSize(400,300);
			addWindowListener(new DialogListener(this,(Cleaner)this));
			setLayout(new BorderLayout());

			//get system variables from database
			sy=null;
			try {
				DataSource ds=display.getDataSource();
				con=ds.getConnection();
				Row r=con.selectAll("pacioli.table.Sys",null);

				//there will be only 1 object
				//System.out.println("checkpoint a");
				if (r.next()) {
					sy=(Sys)r.getRow();
					sysid=r.key();
					System.out.println("Sys found in db, key="+sysid);
				} else {
					System.out.println("Sys not found in db, creating new one");
					sy=new Sys();  //create a new data object
				}
				//System.out.println("checkpoint b");
			} catch (DSX x) {
				System.out.println("Error: "+x.getErrorCode());
				x.printStackTrace();
			}

			//sanity check
			if (sy==null) {
				throw new RuntimeException("sanity check: sy is null");
			}

			//lay out components
			Panel top=new Panel(new GridLayout(7,2));
			add(top,BorderLayout.NORTH);

			Panel center=new Panel(new FlowLayout());
			add(center,BorderLayout.CENTER);

			Panel bottom=new Panel(new FlowLayout());
			add(bottom,BorderLayout.SOUTH);

			top.add(new Label("Company"));
			t_company = new TextField(sy.company, 20);
			top.add(t_company);

			top.add(new Label("Address1"));
			t_address1 = new TextField(sy.address1, 20);
			top.add(t_address1);

			top.add(new Label("Address2"));
			t_address2 = new TextField(sy.address2, 20);
			top.add(t_address2);

			top.add(new Label("Phone"));
			t_phone = new TextField(sy.phone, 20);
			top.add(t_phone);

			top.add(new Label("Fax"));
			t_fax = new TextField(sy.fax, 20);
			top.add(t_fax);

			top.add(new Label("Email"));
			t_email = new TextField(sy.email, 20);
			top.add(t_email);

			top.add(new Label("Default Rate"));
			t_default_rate = new TextField(String.valueOf(sy.default_rate), 20);
			top.add(t_default_rate);

			Button bSave=new Button("Save");
			bSave.addActionListener(this);
			bottom.add(bSave);

			Button bCancel=new Button("Cancel");
			bCancel.addActionListener(this);
			bottom.add(bCancel);

			this.setVisible(true);
		}

		public void actionPerformed(ActionEvent e) {
			String cmd=e.getActionCommand();

			if (cmd.equals("Save")) {
				sy.company=t_company.getText();
				sy.address1=t_address1.getText();
				sy.address2=t_address2.getText();
				sy.phone=t_phone.getText();
				sy.fax=t_fax.getText();
				sy.email=t_email.getText();

				try {
					String t=t_default_rate.getText();
					sy.default_rate=Integer.parseInt(t);
				} catch (Exception x) {
					//print out but do nothing
					System.out.println(x.getMessage());
				}

				try {
					if (sysid==null) {
						sysid=con.insert(sy);
						System.out.println("insert successful, key="+sysid);
					} else {
						con.update(sysid,sy);
						System.out.println("update successful, key="+sysid);
					}
				} catch (DSX x) {
					System.out.println(x.getErrorCode());
					x.printStackTrace();
				}
			}
			con.close();
			this.dispose();
		}

		public void clean() {
			con.close();
		}
	} //end embedded class
}
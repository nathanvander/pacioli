package pacioli.ui;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import pacioli.db.*;
import pacioli.module.*;
import pacioli.table.*;

/**
* This is the control center for the whole program.  It is short and sweet and delegates all the functionality to other classes.
*

*/

public class Pacioli extends Frame implements Display {
	public static String version="0.05";
	DataSource ds;
	Label title;
	JEditorPane desktop;
	Label status;
	Label status2;
	String clientID;

	public Pacioli() {
		super("Pacioli");

		try {
			ds=new LiteDB("pacioli.ldb");

			Conn c=ds.getConnection();
			//these will only be created if they don't already exist
			c.createTable("pacioli.table.Account");
			c.createTable("pacioli.table.Action");
			c.createTable("pacioli.table.Balance");
			c.createTable("pacioli.table.Client");
			c.createTable("pacioli.table.Sys");

			//populate accounts
			Account.addAccounts(c);

			//close the connection
			c.close();

		} catch (DSX x) {
			System.out.println("Error: "+x.getErrorCode());
			x.printStackTrace();
		}

		setSize(500, 400);
		addWindowListener(new WindowListener());
		setLayout(new BorderLayout());

		//title
		title = new Label();
		title.setPreferredSize(new Dimension(100, 16));
		title.setText("Title");
		add(title, BorderLayout.NORTH);

		//add desktop
		ScrollPane pane=new ScrollPane();
		add(pane, BorderLayout.CENTER);
		desktop=new JEditorPane();
		desktop.setContentType("text/html");
		desktop.setEditable(false);
		desktop.setText("Welcome");
		pane.add(desktop);

		MenuBar menuBar=new MenuBar();
		setMenuBar(menuBar);

		//modules
		SysModule sys=new SysModule();
		sys.init(this,this);
		menuBar.add(sys);

		ClientModule client=new ClientModule();
		client.init(this,this);
		menuBar.add(client);

		AccountModule account=new AccountModule();
		account.init(this,this);
		menuBar.add(account);

		//ActivityModule act = new ActivityModule();
		//act.init(this);
		//menuBar.add(act);

		//TransactionModule trans=new TransactionModule();
		//trans.init(this,ds,desktop);
		//menuBar.add(trans);

		//TrustModule trust=new TrustModule();
		//trust.init(this,ds,desktop);
		//menuBar.add(trust);

		//status panel
		Panel statusPanel=new Panel();
		statusPanel.setLayout(new GridLayout(2,1));
		add(statusPanel, BorderLayout.SOUTH);
		status=new Label();
		status.setPreferredSize(new Dimension(100, 16));
		status.setText("Status");
		statusPanel.add(status);
		status2=new Label();
		status2.setPreferredSize(new Dimension(100, 16));
		status2.setText("Status2");
		statusPanel.add(status2);

		setVisible(true);
	}

	//-----------------------------
	public DataSource getDataSource()  {
		return ds;
	}

	public String getVersion() {
		return version;
	}

	public void setTitle(String t) {
		title.setText(t);
	}

	public void setDesktop(String t) {
		desktop.setText(t);
	}

	public String getDesktop() {
		return desktop.getText();
	}

	public void setStatus(String t) {
		status.setText(t);
	}

	public void setStatus2(String t) {
		status2.setText(t);
	}

	public String getClientID() {
		return clientID;
	}

	public void setClientID(String cid) {
		if (cid!=null) {clientID=cid;}
	}

	//===============================
	class WindowListener extends WindowAdapter {
			public void windowClosing(WindowEvent evt) {
				System.out.println("window closing");
				System.exit(0);
			}
	}

	//------------------------------------------------
	public static void main(String[] args) {
		new Pacioli();
	}
}
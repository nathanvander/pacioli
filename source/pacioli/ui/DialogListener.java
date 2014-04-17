package pacioli.ui;
import java.awt.*;
import java.awt.event.*;

public class DialogListener extends WindowAdapter {
		Window w;
		Cleaner c;
		public DialogListener(Window w, Cleaner c) {
			this.w=w;
			this.c=c;
		}
		public void windowClosing(WindowEvent evt) {
			System.out.println("dialog window closing");
			c.clean();
			w.dispose();
		}
}
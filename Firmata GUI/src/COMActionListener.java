import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

public class COMActionListener implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent event) {
		// TODO Auto-generated method stub
		JMenuItem source = (JMenuItem) event.getSource();
		String com = (String) source.getClientProperty("COM");
		Board.COM = com;
		Board.Window.setVisible(false);
		Board.Window = new Mainwindow();
		try {
			if (Board.COM != "COM0")
				new RS232().connect(Board.COM);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

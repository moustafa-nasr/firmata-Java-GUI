import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JToggleButton;

public class OutputToggleListener implements ItemListener {
	@Override
	public void itemStateChanged(ItemEvent e) {
		// TODO Auto-generated method stub
		JToggleButton source = (JToggleButton) e.getSource();
		int pin = Integer.valueOf((String) source.getClientProperty("Pin"));
		if (source.isSelected()) {
			new Orders().setDigitalPin(pin, Orders.Digital_HIGH);
			source.setText("HIGH");
		} else {
			new Orders().setDigitalPin(pin, Orders.Digital_LOW);
			source.setText("LOW");
		}
	}
}

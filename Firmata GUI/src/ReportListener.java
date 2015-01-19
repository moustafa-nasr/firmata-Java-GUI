import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;

public class ReportListener implements ItemListener {

	@Override
	public void itemStateChanged(ItemEvent e) {
		// TODO Auto-generated method stub
		JCheckBox source = (JCheckBox) e.getSource();
		int pin = Integer.valueOf((String) source.getClientProperty("Pin"));
		if (source.isSelected()) {
			if (Board.CurrentModes[pin] == Orders.PinMode_Input) {
				new Orders().reportDigitalPin(pin, true);
			} else if (Board.CurrentModes[pin] == Orders.PinMode_Analog) {
				new Orders().reportAnalogPin(pin, true);
			}
		} else {
			if (Board.CurrentModes[pin] == Orders.PinMode_Input) {
				new Orders().reportDigitalPin(pin, false);
			} else if (Board.CurrentModes[pin] == Orders.PinMode_Analog) {
				new Orders().reportAnalogPin(pin, false);
			}
		}
	}
}

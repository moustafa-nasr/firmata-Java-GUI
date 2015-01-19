import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JRadioButton;

public class ModeListener implements ItemListener {

	@Override
	public void itemStateChanged(ItemEvent e) {
		// TODO Auto-generated method stub
		JRadioButton source = (JRadioButton) e.getSource();
		int pin = Integer.valueOf((String) source.getClientProperty("Pin"));
		if (source.getClientProperty("Mode").equals(
				String.valueOf(Orders.PinMode_Input))) {
			new Orders().setPinMode(pin, Orders.PinMode_Input);
			Mainwindow.report_CheckButton.get(pin).setSelected(true);
			new Orders().reportDigitalPin(pin, true);
			Mainwindow.output_ToggleButton.get(pin).setVisible(false);
			Mainwindow.inputValue_Label.get(pin).setVisible(true);
			Mainwindow.report_CheckButton.get(pin).setEnabled(true);
			Board.CurrentModes[pin] = Orders.PinMode_Input;
		} else if (source.getClientProperty("Mode").equals(
				String.valueOf(Orders.PinMode_Output))) {
			new Orders().setPinMode(pin, Orders.PinMode_Output);
			Mainwindow.report_CheckButton.get(pin).setSelected(false);
			Mainwindow.output_ToggleButton.get(pin).setVisible(true);
			Mainwindow.inputValue_Label.get(pin).setVisible(false);
			Mainwindow.report_CheckButton.get(pin).setEnabled(false);
			Board.CurrentModes[pin] = Orders.PinMode_Output;
		} else if (source.getClientProperty("Mode").equals(
				String.valueOf(Orders.PinMode_Analog))) {
			new Orders().setPinMode(pin, Orders.PinMode_Analog);
			Mainwindow.report_CheckButton.get(pin).setSelected(true);
			Mainwindow.output_ToggleButton.get(pin).setVisible(false);
			Mainwindow.inputValue_Label.get(pin).setVisible(true);
			Mainwindow.report_CheckButton.get(pin).setEnabled(true);
			Board.CurrentModes[pin] = Orders.PinMode_Analog;
		}
	}

}

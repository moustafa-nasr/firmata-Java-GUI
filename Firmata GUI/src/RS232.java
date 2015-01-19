import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

public class RS232 {
	static SerialPort Serialport;

	public void connect(String portname) throws Exception {
		CommPortIdentifier portIdentifier = CommPortIdentifier
				.getPortIdentifier(portname);

		if (portIdentifier.isCurrentlyOwned()) {
			System.out.println("Port in use!");
		} else {
			SerialPort serialport = (SerialPort) portIdentifier.open(
					"MousMashakel", 2000);
			serialport.setSerialPortParams(57600, SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

			ComPortSender.SetOutputStream(serialport.getOutputStream());
			new ComPortReciver(serialport.getInputStream()).start();
		}
	}

	public static void disconnect() throws Exception {
		Serialport.close();
	}
}

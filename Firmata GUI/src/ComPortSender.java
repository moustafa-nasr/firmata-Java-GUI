import java.io.IOException;
import java.io.OutputStream;

public class ComPortSender {

	static OutputStream out;

	public static void SetOutputStream(OutputStream out) {
		ComPortSender.out = out;
	}

	public static void send(byte[] message) {
		// TODO Auto-generated method stub

		System.out.println("SENDING: " + Orders.convertBytesToHex(message));
		// sending through serial port is simply writing into OutputStream
		try {
			out.write(message);
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}

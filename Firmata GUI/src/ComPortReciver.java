import java.io.IOException;
import java.io.InputStream;

public class ComPortReciver extends Thread {

	InputStream in;
	Protocol protocol = new FirmataProtocol();

	public ComPortReciver(InputStream in) {
		this.in = in;
	}

	public void run() {
		try {
			int b;
			while (true) {
				while ((b = in.read()) != -1) {
					protocol.onReceive((byte) b);
				}
				protocol.onStreamClosed();
				sleep(10);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

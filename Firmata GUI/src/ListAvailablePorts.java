import gnu.io.CommPortIdentifier;

import java.util.ArrayList;
import java.util.Enumeration;

public class ListAvailablePorts {

	public static ArrayList<String> COMPorts = new ArrayList<String>();

	public void getList() {
		String comport;
		Enumeration<?> ports = CommPortIdentifier.getPortIdentifiers();
		COMPorts = new ArrayList<String>();
		while (ports.hasMoreElements()) {
			comport = ((CommPortIdentifier) ports.nextElement()).getName();
			COMPorts.add(comport);
			System.out.println(comport);
		}
	}

	public static void main(String[] args) {
		new ListAvailablePorts().getList();
	}
}
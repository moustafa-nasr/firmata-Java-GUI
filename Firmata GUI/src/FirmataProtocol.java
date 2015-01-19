
public class FirmataProtocol implements Protocol {

	byte[] buffer = new byte[2048];
	int bufferPointer = 0;

	// Protocol Commands
	private static final char protocolVersion = 0xF9;
	private static final char sysexStart = 0xF0;
	private static final char sysexEnd = 0xF7;
	private static final char capabilitiesResponse = 0x6C;
	private static final char queryFirmware = 0x79;
	private static final char digitalState = 0x90;
	private static final char analogState = 0xE0;

	private static char messageType = 0;

	@Override
	public void onReceive(byte b) {
		// TODO Auto-generated method stub
		buffer[bufferPointer] = b;
		bufferPointer++;
		if(messageType == 0){
			if (b == ((byte) sysexStart) || b == ((byte) protocolVersion)) {
				messageType = (char) b;
				bufferPointer = 0;
				buffer[bufferPointer] = b;
				bufferPointer++;
			} else if (((byte) (b & 0xF0)) == ((byte) digitalState)) {
				messageType = digitalState;
				bufferPointer = 0;
				buffer[bufferPointer] = b;
				bufferPointer++;
			} else if (((byte) (b & 0xF0)) == ((byte) analogState)) {
				messageType = analogState;
				bufferPointer = 0;
				buffer[bufferPointer] = b;
				bufferPointer++;
			}
		}else{ 
			if (((messageType & 0xff) == (sysexStart & 0xff))
					&& b == ((byte) sysexEnd)) {
				parseCommand();
			} else if (((messageType & 0xff) == (protocolVersion & 0xff))
					&& bufferPointer == 3) {
				parseCommand();
			} else if (((messageType & 0xff) == (digitalState & 0xff)) && bufferPointer == 3) {
				parseCommand();
			} else if (((messageType & 0xff) == (analogState & 0xff)) && bufferPointer == 3) {
				parseCommand();
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void parseCommand() {
		// TODO Auto-generated method stub
		if (bufferPointer != 0) {
			// constructing message
			String message = parseMessage(buffer, bufferPointer);
			System.out.println("RECEIVED MESSAGE: "
					+ Orders.convertBytesToHex(message.getBytes()));

			if (message.contains(String.valueOf(protocolVersion))) {
				byte[] messageBytes = new byte[2];
				message.getBytes(message.indexOf(protocolVersion) + 1,
						message.indexOf(protocolVersion) + 3, messageBytes, 0);
				Mainwindow.FirmwareVersion_Label.setText(String.valueOf(messageBytes[0])
						+ "." + String.valueOf(messageBytes[1]));
				bufferPointer = 0;
				messageType = 0;
			}else if (message.contains(String.valueOf(sysexStart))
					&& message.contains(String.valueOf(sysexEnd))) {
				byte messageBytes[] = new byte[message.length() - 3];
				if (message.contains(String.valueOf(capabilitiesResponse))) {
					String pinEnd = String.valueOf((char) 0x7F);
					int analogCount = 0;
					message.getBytes(message.indexOf(capabilitiesResponse) + 1,
							message.indexOf(sysexEnd), messageBytes, 0);
					String[] pinsData = new String(messageBytes).split(pinEnd);
					Board.PinCount = pinsData.length;
					System.out.println("Number of Pins: "
							+ String.valueOf(Board.PinCount));
					if (pinsData != null) {
						for (int pinNum = 0; pinNum < pinsData.length; pinNum++) {
							byte[] _pinData = pinsData[pinNum].getBytes();
							byte pinMask = (byte) (0x01 << (pinNum % 8));
							int portNum = pinNum / 8;
							int modesNumber = pinsData[pinNum].length() / 2;
							for (int modeCount = 0; modeCount < modesNumber; modeCount++) {
								byte modeType = _pinData[modeCount * 2];
								Board.Mode_Masks[modeType][portNum] |= pinMask;
								if (modeType == Orders.PinMode_Analog) {
									Board.AnalogPinNums[analogCount] = pinNum;
									analogCount++;
								}
							}
						}

					}
					Board.Window.setVisible(false);
					Board.Window = new Mainwindow();
				}else if (message.contains(String.valueOf(queryFirmware))) {
					byte firmwareVersion[] = new byte[2];
					byte rawFirmwareName[] = new byte[message.length()
							- message.indexOf(queryFirmware) - 3];
					message.getBytes(message.indexOf(queryFirmware) + 1,
							message.indexOf(queryFirmware) + 3, firmwareVersion, 0);
					message.getBytes(message.indexOf(queryFirmware) + 3,
							message.length() - 2, rawFirmwareName, 0);

					byte firmwareName[] = new byte[rawFirmwareName.length / 2];
					for (int k = 0; k < rawFirmwareName.length - 2; k += 2) {
						firmwareName[k / 2] = (byte) (rawFirmwareName[k] | (rawFirmwareName[k + 1] << 7));
					}

					Mainwindow.FirmwareVersion_Label.setText(String
							.valueOf(firmwareVersion[0])
							+ "."
							+ String.valueOf(firmwareVersion[1]));
					Mainwindow.FirmwareName_Label.setText(" "
							+ new String(firmwareName) + " ");
					new Orders().requestCapabilityQuery();
				}
				bufferPointer = 0;
				messageType = 0;
			}else if (messageType == digitalState) {
				byte rawPortNumByte[] = { buffer[0] };
				byte rawPortData[] = { buffer[1], buffer[2] };
				byte portNumber = (byte) (rawPortNumByte[0] & 0x0F);
				byte portData = (byte) (rawPortData[0] | (rawPortData[1] << 7));
				Board.DigitalPinState[portNumber] = portData;

				for (int i = 0; i < 8; i++) {
					int reportstate = (Board.DigitalReportMask[portNumber] & (0x01 << i)) >> i;
					if (((Board.CurrentModes[(portNumber * 8) + i] == Orders.PinMode_Input))
							&& (reportstate == 1)) {
						int state = (portData & (0x01 << i)) >> i;

						if (state == 1)
							Mainwindow.inputValue_Label.get(
									(portNumber * 8) + i).setText("LOW");
						else
							Mainwindow.inputValue_Label.get(
									(portNumber * 8) + i).setText("HIGH");
					}
				}
				bufferPointer = 0;
				messageType = 0;
			}else if (messageType == analogState) {
				byte rawPortNumByte[] = { buffer[0] };
				byte rawPortData[] = { buffer[1], buffer[2] };
				byte portNumber = (byte) (rawPortNumByte[0] & 0x0F);
				byte portData = (byte) (rawPortData[0] | (rawPortData[1] << 7));
				int pinNumber = Board.AnalogPinNums[portNumber];
				Board.AnalogPinState[portNumber] = portData;
				Mainwindow.inputValue_Label.get(pinNumber).setText(
						String.valueOf((portData & 0xFF)));
				bufferPointer = 0;
				messageType = 0;
			}
		}
	}

	public String parseMessage(byte[] buffer, int len) {
		return new String(buffer, 0, bufferPointer);
	}

	@Override
	public void onStreamClosed() {
		// TODO Auto-generated method stub
		parseCommand();
	}

}

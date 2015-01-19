public class Orders {

	public static final int PinMode_Input = 0;
	public static final int PinMode_Output = 1;
	public static final int PinMode_Analog = 2;
	public static final int PinMode_PWM = 3;
	public static final int PinMode_Servo = 4;

	public static final char Digital_HIGH = 0x01; // 1 = 00000001
	public static final char Digital_LOW = 0x00; // 0 = 00000000

	public static final byte SysexStart = (byte) 0xF0;
	public static final byte SysexEnd = (byte) 0xF7;

	public void setPinMode(int pinNumber, int pinMode) {
		//
		byte message[] = { (byte) 0xF4, (byte) pinNumber, (byte) pinMode };
		ComPortSender.send(message);

	}

	public void setDigitalPin(int pinNumber, char pinState) {
		//
		//
		byte portNumberByte = (byte) 0x90;
		int portNumber = pinNumber / 8; // PortNumber = PinNumber/8
		portNumber &= 0x0F; // (PortNumber) AND (0x0F)
		portNumberByte |= portNumber; // (0x90) OR PortNumber
		//
		pinNumber %= 8;
		byte pinByte = 0x00;
		pinByte = (byte) (pinState << pinNumber);

		if (pinState == Digital_HIGH) {
			Board.DigitalPinState[portNumber] |= pinByte;
		} else {
			Board.DigitalPinState[portNumber] &= ~(pinByte);
		}
		byte portFirstByte = (byte) (Board.DigitalPinState[portNumber] & 0x7F);
		byte portSecondByte = (byte) (Board.DigitalPinState[portNumber] >>> 7);

		byte message[] = { portNumberByte, portFirstByte, portSecondByte };
		ComPortSender.send(message);
	}

	public void reportAnalogPin(int pinNumber, boolean enable) {
		//
		byte pinByte = (byte) 0xC0;
		for (int i = 0; i < Board.AnalogPinNums.length; i++) {
			if (Board.AnalogPinNums[i] == pinNumber) {
				pinNumber = i;
				break;
			}
		}
		pinNumber &= 0x0F; // (PinNumber) AND 0x0F
		pinByte |= pinNumber; // (0xC0) OR PinNumber
		byte message[] = { pinByte, (byte) 0x00 };
		if (enable)
			message[1] = 1;

		ComPortSender.send(message);
	}

	public void reportDigitalPin(int pinNumber, boolean enable) {
		//
		int portNumber = pinNumber / 8;
		byte portByte = (byte) 0xD0;
		portNumber &= 0x0F; // (PortNumber) AND 0x0F
		portByte |= portNumber; // (0xD0) OR PortNumber
		if (enable)
			Board.DigitalReportMask[portNumber] |= (byte) (0x01 << (pinNumber % 8));
		else
			Board.DigitalReportMask[portNumber] &= (byte) (~(0x01 << (pinNumber % 8)));

		byte message[] = { portByte, Board.DigitalReportMask[portNumber] };
		ComPortSender.send(message);
	}

	public void getPinState(int pinNumber) {
		byte message[] = { SysexStart, (byte) 0x6D, (byte) pinNumber, SysexEnd };
		ComPortSender.send(message);
	}

	public void requestAnalogMapping() {
		byte message[] = { SysexStart, (byte) 0x69, SysexEnd };
		ComPortSender.send(message);
	}

	public void requestCapabilityQuery() {
		byte message[] = { SysexStart, (byte) 0x6B, SysexEnd };
		ComPortSender.send(message);
	}

	public void sendSystemReset() {
		byte message[] = { (byte) 0xFF };
		ComPortSender.send(message);
	}

	public void requestProtocolVersion() {
		byte message[] = { (byte) 0xF9 };
		ComPortSender.send(message);
	}
	
	//-------
	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
	public static String convertBytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}
	//------------
}

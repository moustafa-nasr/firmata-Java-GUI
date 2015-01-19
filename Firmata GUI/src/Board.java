public class Board {
	public static String COM = "COM0";
	public static int PinCount = 0;
	public static int[] AnalogPinNums = new int[16];
	public static byte[] AnalogPinState = new byte[16];
	public static byte[] DigitalPinState = new byte[16];
	public static byte[] DigitalReportMask = new byte[16];

	public static byte[][] Mode_Masks = new byte[7][16];
	public static int[] CurrentModes = new int[127];

	public static Mainwindow Window;
}

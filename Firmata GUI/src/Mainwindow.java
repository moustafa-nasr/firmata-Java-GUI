import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;

public class Mainwindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static JLabel FirmwareVersion_Label = new JLabel();
	public static JLabel FirmwareName_Label = new JLabel();
	public static JLabel ComPort_Label = new JLabel();

	private JPanel mainPanel = new JPanel(new GridBagLayout());
	private static JPanel pinPanel = new JPanel(new GridBagLayout());

	public static ArrayList<JLabel> inputValue_Label = new ArrayList<JLabel>();
	public static ArrayList<JRadioButton> input_RadioButton = new ArrayList<JRadioButton>();
	public static ArrayList<JRadioButton> output_RadioButton = new ArrayList<JRadioButton>();
	public static ArrayList<JRadioButton> analog_RadioButton = new ArrayList<JRadioButton>();
	public static ArrayList<JCheckBox> report_CheckButton = new ArrayList<JCheckBox>();
	public static ArrayList<JToggleButton> output_ToggleButton = new ArrayList<JToggleButton>();

	private static ModeListener modeListener = new ModeListener();
	private static ReportListener reportListener = new ReportListener();
	private static OutputToggleListener outputToggleListener = new OutputToggleListener();
	private static COMActionListener comActionListener = new COMActionListener();

	

	public Mainwindow() {
		// TODO Auto-generated constructor stub
		super("Firmata GUI");
		setSize(460, 650);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		
		int pinCout = 0;
		// ----Menu---
		JMenuBar menuBar = new JMenuBar();
		JMenu toolsMenu = new JMenu("Commands");
		JMenu comMenu = new JMenu("COM Port (" + Board.COM + ")");
		menuBar.add(toolsMenu);
		menuBar.add(comMenu);
		JMenuItem systemReset_MenuItem = new JMenuItem("System Reset");
		JMenuItem protocolVer_MenuItem = new JMenuItem("Check ProtocolVer");
		JMenuItem capabilityQuery_MenuItem = new JMenuItem("Check Capability");
		JMenuItem exit_Button = new JMenuItem("Exit Program");
		systemReset_MenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				new Orders().sendSystemReset();
			}
		});
		protocolVer_MenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				new Orders().requestProtocolVersion();
			}
		});
		capabilityQuery_MenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				new Orders().requestCapabilityQuery();
			}
		});
		exit_Button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				Board.Window.setVisible(false);
			}
		});
		toolsMenu.add(systemReset_MenuItem);
		toolsMenu.add(protocolVer_MenuItem);
		toolsMenu.add(capabilityQuery_MenuItem);
		toolsMenu.addSeparator();
		toolsMenu.add(exit_Button);

		for (int i = 0; i < ListAvailablePorts.COMPorts.size(); i++) {
			JMenuItem COM = new JMenuItem(ListAvailablePorts.COMPorts.get(i));
			COM.putClientProperty("COM", ListAvailablePorts.COMPorts.get(i));
			COM.addActionListener(comActionListener);
			comMenu.add(COM);
		}
		JMenuItem comRefresh_MenuItem = new JMenuItem("Refresh");
		comRefresh_MenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				new ListAvailablePorts().getList();
				Board.Window.setVisible(false);
				Board.Window = new Mainwindow();
				try {
					if (Board.COM != "COM0")
						new RS232().connect(Board.COM);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		comMenu.addSeparator();
		comMenu.add(comRefresh_MenuItem);

		// ----Main Panel---
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		for (pinCout = 0; pinCout < Board.PinCount; pinCout++) {
			// System.out.print(String.valueOf(K)+" ");

			JLabel pinLabel = new JLabel("Pin" + String.valueOf(pinCout) + ": ");
			report_CheckButton.add(new JCheckBox("Report"));
			report_CheckButton.get(pinCout).putClientProperty("Pin",
					String.valueOf(pinCout));
			ButtonGroup modes_ButtonGroup = new ButtonGroup();
			input_RadioButton.add(new JRadioButton("Input"));
			input_RadioButton.get(pinCout).putClientProperty("Mode",
					String.valueOf(Orders.PinMode_Input));
			input_RadioButton.get(pinCout).putClientProperty("Pin",
					String.valueOf(pinCout));
			output_RadioButton.add(new JRadioButton("Output"));
			output_RadioButton.get(pinCout).putClientProperty("Mode",
					String.valueOf(Orders.PinMode_Output));
			output_RadioButton.get(pinCout).putClientProperty("Pin",
					String.valueOf(pinCout));
			analog_RadioButton.add(new JRadioButton("Analog"));
			analog_RadioButton.get(pinCout).putClientProperty("Mode",
					String.valueOf(Orders.PinMode_Analog));
			analog_RadioButton.get(pinCout).putClientProperty("Pin",
					String.valueOf(pinCout));

			output_RadioButton.get(pinCout).setSelected(true);
			Board.CurrentModes[pinCout] = Orders.PinMode_Output;
			modes_ButtonGroup.add(input_RadioButton.get(pinCout));
			modes_ButtonGroup.add(output_RadioButton.get(pinCout));
			modes_ButtonGroup.add(analog_RadioButton.get(pinCout));

			int portNumber = pinCout / 8;
			int pinNumber = pinCout % 8;
			if (((Board.Mode_Masks[Orders.PinMode_Input][portNumber] >> (pinNumber)) & ((byte) 0x01)) == ((byte) 0x01)) {
				input_RadioButton.get(pinCout).setEnabled(true);
			} else {
				input_RadioButton.get(pinCout).setEnabled(false);
			}
			if (((Board.Mode_Masks[Orders.PinMode_Output][portNumber] >> (pinNumber)) & ((byte) 0x01)) == ((byte) 0x01)) {
				output_RadioButton.get(pinCout).setEnabled(true);
			} else {
				output_RadioButton.get(pinCout).setEnabled(false);
			}
			if (((Board.Mode_Masks[Orders.PinMode_Analog][portNumber] >> (pinNumber)) & ((byte) 0x01)) == ((byte) 0x01)) {
				analog_RadioButton.get(pinCout).setEnabled(true);
			} else {
				analog_RadioButton.get(pinCout).setEnabled(false);
			}

			inputValue_Label.add(new JLabel("LOW"));
			inputValue_Label.get(pinCout).setVisible(false);

			output_ToggleButton.add(new JToggleButton("LOW"));
			output_ToggleButton.get(pinCout).putClientProperty("Pin",
					String.valueOf(pinCout));

			report_CheckButton.get(pinCout).setEnabled(false);
			report_CheckButton.get(pinCout).addItemListener(reportListener);
			output_ToggleButton.get(pinCout).addItemListener(outputToggleListener);
			input_RadioButton.get(pinCout).addItemListener(modeListener);
			output_RadioButton.get(pinCout).addItemListener(modeListener);
			analog_RadioButton.get(pinCout).addItemListener(modeListener);

			gridBagConstraints.gridy = pinCout;
			gridBagConstraints.gridx = 0;
			pinPanel.add(pinLabel, gridBagConstraints);
			gridBagConstraints.gridx = 1;
			pinPanel.add(report_CheckButton.get(pinCout), gridBagConstraints);
			gridBagConstraints.gridx = 2;
			pinPanel.add(new JLabel("  |  "), gridBagConstraints);
			gridBagConstraints.gridx = 3;
			pinPanel.add(input_RadioButton.get(pinCout), gridBagConstraints);
			gridBagConstraints.gridx = 4;
			pinPanel.add(output_RadioButton.get(pinCout), gridBagConstraints);
			gridBagConstraints.gridx = 5;
			pinPanel.add(analog_RadioButton.get(pinCout), gridBagConstraints);
			gridBagConstraints.gridx = 6;
			pinPanel.add(new JLabel("  |  "), gridBagConstraints);
			gridBagConstraints.gridx = 8;
			pinPanel.add(inputValue_Label.get(pinCout), gridBagConstraints);
			gridBagConstraints.gridx = 8;
			pinPanel.add(output_ToggleButton.get(pinCout), gridBagConstraints);

		}
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		pinPanel.setAutoscrolls(true);
		JScrollPane scrollFrame = new JScrollPane(pinPanel);
		pinPanel.setAutoscrolls(true);
		scrollFrame.setPreferredSize(new Dimension(400, 525));
		mainPanel.add(scrollFrame, gridBagConstraints);
		
		// ----Footer---
		JPanel footerPanel = new JPanel(new GridBagLayout());
		if (Board.COM != "COM0")
			ComPort_Label.setText(Board.COM + "  |  ");

		footerPanel.add(ComPort_Label);
		footerPanel.add(FirmwareName_Label);
		footerPanel.add(FirmwareVersion_Label);
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		mainPanel.add(footerPanel, gridBagConstraints);
		
		// ----  ---
		add(mainPanel);
		this.setJMenuBar(menuBar);
		revalidate();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new ListAvailablePorts().getList();
		Board.Window = new Mainwindow();
	}

}

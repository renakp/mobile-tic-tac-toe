package main.bluetooth;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;

public class BluetoothTest extends MIDlet {
	// private static final int MAX_TEXT_SIZE = 128;
	private final Display display = Display.getDisplay(this);

	// private WelcomeScreen welcomeScreen;
	private ConnectionScreen connectionScreen;

	// private final Command exitCommand = new Command("Exit", Command.EXIT, 2);
	// private final Command startCommand = new Command("Start", Command.SCREEN,
	// 1);
	// private final Command sendCommand = new Command("Send", Command.ITEM, 1);
	//
	// private final Form form = new Form("Bluetooth Test");
	// private final StringItem messageLabel = new StringItem("Status: ",
	// "Disconnected.");

	// private void createWelcomeScreen() {
	// welcomeScreen = new WelcomeScreen("Welcome To Bluetooth Test",
	// "A test of bluetooth connectivity.\n"
	// + "Bluetooth must be enabled before starting.", this);
	// // StringItem label = new StringItem(null,
	// // "A test of bluetooth connectivity.\n"
	// // + "Bluetooth must be enabled "
	// // + "and the remote device known before starting.");
	// //
	// // Form form = new Form("Bluetooth Test");
	// // form.append(label);
	// //
	// // form.addCommand(exitCommand);
	// // form.addCommand(startCommand);
	// //
	// // welcomeScreen = form;
	// // welcomeScreen.setCommandListener(new CommandListener() {
	// // public void commandAction(Command c, Displayable d) {
	// // if (c == exitCommand) {
	// // destroyApp(false);
	// // notifyDestroyed();
	// // } else
	// // if (c == startCommand) display.setCurrent(bluetoothScreen);
	// // }
	// // });
	// }

	private void createconnectionScreen() {
		// form.addCommand(exitCommand);
		//
		// TextField textField = new TextField("Data: ",
		// "Testing testing, 1 2 3...", MAX_TEXT_SIZE, TextField.ANY);
		// textField.setDefaultCommand(sendCommand);
		// textField.setItemCommandListener(new ItemCommandListener() {
		// public void commandAction(Command c, Item item) {
		// if (c == sendCommand) {
		// bluetoothTest();
		// }
		// }
		// });
		// form.append(textField);
		//
		// messageLabel.setLayout(Item.LAYOUT_LEFT);
		// form.append(messageLabel);
		//
		// bluetoothScreen = form;
		// bluetoothScreen.setCommandListener(new CommandListener() {
		// public void commandAction(Command c, Displayable d) {
		// if (c == exitCommand) {
		// destroyApp(false);
		// notifyDestroyed();
		// }
		// }
		// });

		connectionScreen = new ConnectionScreen(this);
	}

	// private void bluetoothTest() {
	// notifyUser("Retrieving devices...");
	// LocalDevice localdevice;
	// try {
	// localdevice = LocalDevice.getLocalDevice();
	// DiscoveryAgent discoveryAgent = localdevice.getDiscoveryAgent();
	// RemoteDevice[] remoteDevices = discoveryAgent
	// .retrieveDevices(DiscoveryAgent.PREKNOWN);
	// if (remoteDevices == null)
	// throw new Exception("Known devices retrieval failed.");
	// if (remoteDevices.length == 0)
	// throw new Exception("No known devices found.");
	//
	// notifyUser(remoteDevices.length + " devices found.");
	// ChoiceGroup deviceList = new ChoiceGroup("Devices", List.EXCLUSIVE);
	// deviceList.setLabel("Select a device to connect with:");
	// for (int i = 0 ; i < remoteDevices.length ; i++) {
	// String name = remoteDevices[i].toString();
	// notifyUser("Found device: " + name + ".");
	// deviceList.append(name, null);
	// }
	// form.append(deviceList);
	//
	// } catch (Exception e) {
	// notifyUser("Error: " + e.getMessage());
	// }
	// }

	// private void notifyUser(String message) {
	// messageLabel.setText(message);
	// }

	public BluetoothTest() {
		// createWelcomeScreen();
		createconnectionScreen();
		// welcomeScreen.setNextScreen(connectionScreen);
		// connectionScreen.setPrevScreen(welcomeScreen);
	}

	protected void destroyApp(boolean unconditional) {}

	protected void pauseApp() {}

	protected void startApp() {
		display.setCurrent(/* welcomeScreen */connectionScreen);
	}
}

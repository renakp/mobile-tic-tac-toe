package main.bluetooth;

import java.util.Vector;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import javax.microedition.midlet.MIDlet;

public class ConnectionScreen extends List implements DiscoveryListener {
	private final Display display;
	private Bluetooth bluetooth;

	private final Command scanCommand = new Command("Scan", Command.SCREEN, 1);

	private final Vector remoteDevices = new Vector();

	public ConnectionScreen(final MIDlet midlet) {
		super("Bluetooth Connection", Choice.IMPLICIT);
		display = Display.getDisplay(midlet);

		addCommand(scanCommand);

		final ConnectionScreen cs = this;
		setCommandListener(new CommandListener() {
			public void commandAction(Command c, Displayable d) {
				if (c == scanCommand) {
					initBluetooth();
					populateList();
				}
			}
		});
	}

	private void initBluetooth() {
		try {
			bluetooth = Bluetooth.getInstance();
		} catch (BluetoothStateException e) {
			terminateWithError(e);
		}
	}

	private void terminateWithError(Exception e) {
		String message = "Failed to initialize bluetooth. "
				+ "Check that the bluetooth function "
				+ "is enabled on your device.";
		if (e != null) message += "\nError:\n" + e.getMessage();

		bluetooth.cancelInquiry(this);

		Alert alert = new Alert("Bluetooth Failure", message, null,
				AlertType.ERROR);
		alert.setTimeout(Alert.FOREVER);
		display.setCurrent(alert);

		return;
	}

	private void populateList() {
		RemoteDevice[] devices = bluetooth.getKnownDevices();
		addDevices(devices);
		devices = bluetooth.getCachedDevices();
		addDevices(devices);

		setTitle("Scanning for devices...");
		scanForDevices();
	}

	private void scanForDevices() {
		try {
			bluetooth.startInquiry(this);
		} catch (BluetoothStateException e) {
			terminateWithError(e);
		}
	}

	private void addDevices(RemoteDevice[] devices) {
		if (devices != null) {
			for (int i = 0 ; i < devices.length ; i++) {
				addDevice(devices[i]);
			}
		}
	}

	private void addDevice(RemoteDevice device) {
		/*
		 * TODO: 1) Connect to device. If fails - skip it. 2) Get friendly name.
		 * If fails - skip it. 3) Add device to remoteDevices. 4) Add friendly
		 * name to this List.
		 */
		append(device.toString(), null);
	}

	// ////////// handle discovered remote devices ////////// //

	public void deviceDiscovered(RemoteDevice device, DeviceClass deviceClass) {
		addDevice(device);
	}

	public void inquiryCompleted(int disconnectionType) {
		if (disconnectionType == DiscoveryListener.INQUIRY_ERROR)
			terminateWithError(null);
		setTitle("Scan completed.");
	}

	public void servicesDiscovered(int id, ServiceRecord[] record) {
	// TODO Auto-generated constructor stub
	}

	public void serviceSearchCompleted(int id, int code) {
	// TODO Auto-generated constructor stub
	}
}

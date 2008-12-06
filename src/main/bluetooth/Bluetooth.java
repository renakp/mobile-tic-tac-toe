package main.bluetooth;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;

public class Bluetooth {
	private final LocalDevice localdevice;
	private final DiscoveryAgent discoveryAgent;

	private static Bluetooth instance_ = null;

	private Bluetooth() throws BluetoothStateException {
		localdevice = LocalDevice.getLocalDevice();
		discoveryAgent = localdevice.getDiscoveryAgent();
	}

	public static Bluetooth getInstance() throws BluetoothStateException {
		if (instance_ == null) instance_ = new Bluetooth();
		return instance_;
	}

	/**
	 * @return An array of the remote devices already known to the
	 *         implementation, or <code>null</code> if failed to find them.
	 */
	public RemoteDevice[] getKnownDevices() {
		RemoteDevice[] remoteDevices = discoveryAgent
				.retrieveDevices(DiscoveryAgent.PREKNOWN);
		return remoteDevices;
	}

	/**
	 * @return An array of the remote devices already found before, or
	 *         <code>null</code> if failed to find them.
	 */
	public RemoteDevice[] getCachedDevices() {
		RemoteDevice[] remoteDevices = discoveryAgent
				.retrieveDevices(DiscoveryAgent.CACHED);
		return remoteDevices;
	}

	public void startInquiry(DiscoveryListener discoveryListener)
			throws BluetoothStateException {
		discoveryAgent.startInquiry(DiscoveryAgent.GIAC, discoveryListener);
	}

	public void cancelInquiry(DiscoveryListener discoveryListener) {
		discoveryAgent.cancelInquiry(discoveryListener);
	}
}

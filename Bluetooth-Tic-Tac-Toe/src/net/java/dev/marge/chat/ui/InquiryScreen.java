/*
 * Marge, Java Bluetooth Framework
 * Copyright (C) 2006  Project Marge
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * owner@marge.dev.java.net
 * http://marge.dev.java.net
 */

package net.java.dev.marge.chat.ui;

import java.io.IOException;
import java.util.Vector;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

import net.java.dev.marge.chat.MargeMIDlet;
import net.java.dev.marge.entity.config.ClientConfiguration;
import net.java.dev.marge.factory.CommunicationFactory;
import net.java.dev.marge.inquiry.DeviceDiscoverer;
import net.java.dev.marge.inquiry.InquiryListener;
import net.java.dev.marge.inquiry.ServiceDiscoverer;
import net.java.dev.marge.inquiry.ServiceSearchListener;

public class InquiryScreen extends List implements CommandListener,
		ServiceSearchListener, InquiryListener {

	private MainMenu menu;

	private ServiceDiscoverer serviceDiscoverer;

	private DeviceDiscoverer deviceDiscoverer;

	private Command select;

	private Command stopOrBack;

	private Vector devices;

	private CommunicationFactory factory;

	public InquiryScreen(MainMenu menu, CommunicationFactory factory)
			throws BluetoothStateException {
		super("Inquirying...", List.IMPLICIT);
		this.select = null;
		this.menu = menu;
		this.factory = factory;
		this.serviceDiscoverer = ServiceDiscoverer.getInstance();
		this.deviceDiscoverer = DeviceDiscoverer.getInstance();
		this.devices = new Vector(5);
		this.addCommand(this.stopOrBack = new Command("Stop",
				Command.CANCEL, 1));
		this.setCommandListener(this);
		new Thread() {
			public void run() {
				try {
					InquiryScreen.this.deviceDiscoverer.startInquiry(
							DiscoveryAgent.GIAC, InquiryScreen.this);
				} catch (BluetoothStateException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	public void commandAction(Command c, Displayable arg1) {
		if (c == this.stopOrBack) {
			if (c.getCommandType() == Command.CANCEL) {
				this.deviceDiscoverer.cancelInquiry();
				this.removeCommand(c);
				this.stopOrBack = new Command("Back", Command.BACK, 1);
				this.addCommand(this.stopOrBack);
			} else {
				MargeMIDlet.instance.showMainMenu();
			}
		} else {
			if (this.select != null) {
				this.deviceDiscoverer.cancelInquiry();
				try {
					serviceDiscoverer.startSearch((RemoteDevice) this.devices.elementAt(this
									.getSelectedIndex()), this);
				} catch (BluetoothStateException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void deviceNotReachable() {
	}

	public void serviceSearchCompleted(RemoteDevice remoteDevice,
			ServiceRecord[] services) {

		ClientConfiguration config = new ClientConfiguration(services[0],
				this.menu.getGameScreen());
		try {
			this.menu.connectionEstablished(this.factory.connectToServer(config));
		} catch (IOException e) {
			this.menu.errorOnConnection(e);
		}
	}

	public void serviceSearchError() {
	}

	public void deviceDiscovered(RemoteDevice device, DeviceClass deviceClass) {
		if (this.select == null) {
			this.select = new Command("Select", Command.OK, 1);
			this.addCommand(select);
		}

		this.devices.addElement(device);
		this.setTitle("Inquirying... " + this.devices.size());
		try {
			this.append(device.getFriendlyName(false), null);
		} catch (IOException e) {
			this.append(device.getBluetoothAddress(), null);
			e.printStackTrace();
		}
	}

	public void inquiryCompleted(RemoteDevice[] devices) {
		this.setTitle(Integer.toString(this.devices.size()) + " devices found");
		this.removeCommand(this.stopOrBack);
		this.stopOrBack = new Command("Back", Command.BACK, 1);
		this.addCommand(this.stopOrBack);
	}

	public void inquiryError() {

	}

}

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

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

import main.GameScreen;
import net.java.dev.marge.chat.MargeMIDlet;
import net.java.dev.marge.communication.ConnectionListener;
import net.java.dev.marge.entity.ClientDevice;
import net.java.dev.marge.entity.ServerDevice;
import net.java.dev.marge.entity.config.ServerConfiguration;
import net.java.dev.marge.factory.CommunicationFactory;
import net.java.dev.marge.factory.RFCOMMCommunicationFactory;

public class MainMenu extends List implements CommandListener,
		ConnectionListener {

	private final String SELEC_COMMAND_NAME = "Select";

	//private ChatRoom room;
	private GameScreen gameScreen;
	private final Display display = Display.getDisplay(MargeMIDlet.instance); 

	private boolean cancelled;

	public MainMenu() {
		super("Hugs n kisses", List.IMPLICIT);

		this.append("Run client", null);
		this.append("Run server", null);
		this.append("Exit", null);

		this.addCommand(new Command(SELEC_COMMAND_NAME, Command.OK, 1));
		this.setCommandListener(this);
		//this.room = new ChatRoom();
		this.gameScreen = new GameScreen(display);
		
		
		this.cancelled = false;
	}

	public void commandAction(Command c, Displayable d) {
		try {
			LocalDevice.getLocalDevice().setDiscoverable(DiscoveryAgent.GIAC);
			switch (this.getSelectedIndex()) {
			case 0:  //client
				this.performInquiry(new RFCOMMCommunicationFactory());
				break;
			case 1: //server
				this.cancelled = false;
				MargeMIDlet.instance.setCurrent(new LoadingScreen(this));
				CommunicationFactory factory = new RFCOMMCommunicationFactory();
				ServerConfiguration config = new ServerConfiguration(/*room*/gameScreen);
                                config.setAuthenticate(true);
                                config.setAuthorize(true);
                                config.setEncrypt(true);
				config.setMaxNumberOfConnections(8);
				factory.waitClients(config, this);
				break;
			default:
				MargeMIDlet.instance.exit();
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void performInquiry(CommunicationFactory factory) {
		try {
			MargeMIDlet.instance.setCurrent(new InquiryScreen(this, factory));
		} catch (BluetoothStateException e) {
			e.printStackTrace();
		}
	}

	public GameScreen getGameScreen() {
		return gameScreen;
	}

//	public ChatRoom getChatRoom() {
//		return room;
//	}
	
	public void errorOnConnection(IOException exception) {
		MargeMIDlet.instance.showError("inside errorOnConnection: "+exception.getMessage(), this);
	}

	public void connectionEstablished(ClientDevice device) {
		if (!this.cancelled) {
			device.startListening();
//			gameScreen = new GameScreen(display, device, false);
			this.gameScreen.setDevice(device);
			this.gameScreen.setKiss(false);
			MargeMIDlet.instance.setCurrent(gameScreen);
//			this.room.setDevice(device);
//			MargeMIDlet.instance.setCurrent(this.room);
		}
	}

	//at the server side
	public void connectionEstablished(ServerDevice device, RemoteDevice remote) {
		//System.out.println(remote.getBluetoothAddress());
		if (!this.cancelled) {
			device.startListening();
			device.setEnableBroadcast(true);
//			gameScreen = new GameScreen(display, device, true);
			this.gameScreen.setDevice(device);
			this.gameScreen.setKiss(true);
			MargeMIDlet.instance.setCurrent(gameScreen);
			gameScreen.playTurn();
//			this.room.setDevice(device);
//			MargeMIDlet.instance.setCurrent(this.room);
		}
	}

	public void setCancelled() {
		this.cancelled = true;
	}
}
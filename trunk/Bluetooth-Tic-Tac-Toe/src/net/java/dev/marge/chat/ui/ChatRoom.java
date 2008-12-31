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

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;

import net.java.dev.marge.chat.MargeMIDlet;
import net.java.dev.marge.communication.CommunicationListener;
import net.java.dev.marge.entity.Device;

public class ChatRoom extends Form implements CommunicationListener,
		CommandListener {

	private Device device;

	private Command back;

	private TextField chatField;

	public ChatRoom() {
		super("Chat...");
		this.chatField = new TextField("Message", null, 100, TextField.ANY);
		this.append(this.chatField);
		this.addCommand(new Command("Send", Command.OK, 1));
		this.addCommand(this.back = new Command("Back", Command.BACK, 1));
		this.setCommandListener(this);
	}

	public void receiveMessage(byte[] receivedString) {
		this.insert(1, new StringItem("Received: ", new String(receivedString)));
	}

	public void errorOnReceiving(IOException e) {
		e.printStackTrace();
		this.leaveChat();
	}

	public void errorOnSending(IOException e) {
		e.printStackTrace();
		this.leaveChat();
	}

	public void sendMessage(String message) {
		this.device.send(message.getBytes());
		this.insert(1, new StringItem("Sent: ", message));
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	public void leaveChat() {
		this.deleteAll();
		this.append(this.chatField);
		this.device.close();
		MargeMIDlet.instance.showMainMenu();
	}

	public void commandAction(Command c, Displayable d) {
		if (c == this.back) {
			this.leaveChat();
		} else {
				this.sendMessage(this.chatField.getString());
		}
	}
}

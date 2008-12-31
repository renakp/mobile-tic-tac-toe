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

package net.java.dev.marge.chat;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import net.java.dev.marge.chat.ui.MainMenu;
import net.java.dev.marge.entity.Device;

public class MargeMIDlet extends MIDlet {

	public static MargeMIDlet instance;

	private Display display;

	private Device device;

	private MainMenu mainMenu;

	public MargeMIDlet() {
		display = Display.getDisplay(this);
		instance = this;
	}

	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
		notifyDestroyed();
	}

	protected void pauseApp() {
	}

	protected void startApp() throws MIDletStateChangeException {
		setCurrent(this.mainMenu = new MainMenu());
	}

	public void exit() {
		try {
			this.destroyApp(true);
		} catch (MIDletStateChangeException e) {
			e.printStackTrace();
		}
	}

	public void setCurrent(Displayable d) {
		this.display.setCurrent(d);
	}

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	public void showError(String message, Displayable d) {
		Alert alert = new Alert("Error", message, null, AlertType.ERROR);
		alert.setTimeout(2000);
		display.setCurrent(alert, d);

	}

	public void showMainMenu() {
		setCurrent(mainMenu);
	}

}
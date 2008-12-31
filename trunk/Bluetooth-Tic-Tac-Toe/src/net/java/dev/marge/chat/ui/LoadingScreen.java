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

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Gauge;

import net.java.dev.marge.chat.MargeMIDlet;

public class LoadingScreen extends Form implements CommandListener {

	private MainMenu menu;
	
	public LoadingScreen(MainMenu menu) {
		super("Waiting...");
		this.menu = menu;
		this.append(new Gauge("Waiting for connections", false, Gauge.INDEFINITE, Gauge.CONTINUOUS_RUNNING));
		this.addCommand(new Command("Cancel", Command.CANCEL, 1));
		this.setCommandListener(this);
	}

	public void commandAction(Command arg0, Displayable arg1) {
		this.menu.setCancelled();
		MargeMIDlet.instance.showMainMenu();
	}
}

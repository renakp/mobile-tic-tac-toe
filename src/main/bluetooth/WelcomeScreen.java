package main.bluetooth;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;
import javax.microedition.midlet.MIDlet;

public class WelcomeScreen extends Form {
	private final Display display;
	private Displayable nextScreen;

	private final Command exitCommand = new Command("Exit", Command.EXIT, 2);
	private final Command nextCommand = new Command("Next", Command.SCREEN, 1);

	public WelcomeScreen(String title, String message, final MIDlet midlet) {
		super(title);
		display = Display.getDisplay(midlet);

		StringItem label = new StringItem(null, message);
		append(label);

		addCommand(exitCommand);
		setCommandListener(new CommandListener() {
			public void commandAction(Command c, Displayable d) {
				if (c == exitCommand) {
					midlet.notifyDestroyed();
				} else
					if (c == nextCommand) {
						display.setCurrent(nextScreen);
					}
			}
		});
	}

	public void setNextScreen(Displayable nextScreen) {
		this.nextScreen = nextScreen;
		addCommand(nextCommand);
	}
}

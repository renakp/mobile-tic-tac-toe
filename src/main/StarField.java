package main;

import java.util.Random;
import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.GameCanvas;

// A simple example of a game canvas that displays
// a scrolling star field. The UP and DOWN keys
// speed up or slow down the rate of scrolling.

public class StarField extends GameCanvas implements Runnable {

	private static final int SLEEP_INCREMENT = 10;
	private static final int SLEEP_INITIAL = 150;
	private static final int SLEEP_MAX = 300;

	private static final int WHITE = 0x00ffffff;
	private static final int GREEN = 0x0000ff00;
	private static final int BLACK = 0;

	private Graphics graphics;
	private Random random;
	private int sleepTime = SLEEP_INITIAL;
	private volatile Thread thread;

	public StarField() {
		super(true);

		graphics = getGraphics();
		graphics.setColor(0, 0, 0);
		graphics.fillRect(0, 0, getWidth(), getHeight());
	}

	// When the game canvas is hidden, stop the thread.

	protected void hideNotify() {
		thread = null;
	}

	// The game loop.

	public void run() {
		int fontHeight = graphics.getFont().getHeight();

		int w = getWidth();
		int h = getHeight() - 1 - fontHeight;

		while (thread == Thread.currentThread()) {

			// Increment or decrement the scrolling interval
			// based on key presses

			int state = getKeyStates();

			if ((state & DOWN_PRESSED) != 0) {
				sleepTime += SLEEP_INCREMENT;
				if (sleepTime > SLEEP_MAX) sleepTime = SLEEP_MAX;
			} else
				if ((state & UP_PRESSED) != 0) {
					sleepTime -= SLEEP_INCREMENT;
					if (sleepTime < 0) sleepTime = 0;
				}

			// Repaint the screen by first scrolling the
			// existing starfield down one and painting in
			// new stars...

			graphics.copyArea(0, 0, w, h, 0, 1, Graphics.TOP | Graphics.LEFT);

			graphics.setColor(BLACK); // black
			graphics.drawLine(0, 0, w, 0);
			graphics.fillRect(0, h, w, getHeight()); // clears speed text

			graphics.setColor(WHITE); // white
			for (int i = 0 ; i < w ; ++i) {
				int test = Math.abs(random.nextInt()) % 100;
				if (test < 4) {
					graphics.drawLine(i, 0, i, 0);
				}
			}

			graphics.setColor(GREEN);
			graphics.drawString("Speed: " + (SLEEP_MAX - sleepTime), 0, h,
					Graphics.TOP | Graphics.LEFT);

			flushGraphics();

			// Now wait...

			try {
				Thread.currentThread();
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {}
		}
	}

	// When the canvas is shown, start a thread to
	// run the game loop.

	protected void showNotify() {
		random = new Random();

		thread = new Thread(this);
		thread.start();
	}
}

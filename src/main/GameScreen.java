package main;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.GameCanvas;

public class GameScreen extends GameCanvas implements Runnable {
	private final Display display;
	private final Graphics graphics;
	private volatile Thread thread;
	private int board[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	private boolean player = true; // true is kiss
	private int index = 0;

	private int size; // length of the whole board
	private int segment; // length of a segment, not including border lines
	private int canvasWidth;
	private int canvasHeight;

	private static final int BORDER = 6;

	public GameScreen(Display display) {
		super(true);

		this.display = display;
		graphics = getGraphics();
	}

	// make game loop garbage-collectible
	protected void hideNotify() {
		thread = null;
	}

	// draw board and start game loop
	protected void showNotify() {
		canvasWidth = getWidth();
		canvasHeight = getHeight();
		size = (canvasWidth <= canvasHeight ? canvasWidth : canvasHeight);
		segment = (size - 2) / 3; // size - 2 line widths (1 each)
		size = (segment * 3) + 2; // fixing size after truncation

		clearDisplay();
		drawBoard();
		drawHighlight();
		

//		for (int i = 0 ; i < 9 ; i++) {
//			drawKiss(i);
//			drawHug(i);
//		}

		// start game loop
		thread = new Thread(this);
		thread.start();
	}

/**
 * clears the screen	
 */
	private void clearDisplay() {
		graphics.setColor(display.getColor(Display.COLOR_BACKGROUND));
		graphics.fillRect(0, 0, canvasWidth, canvasHeight);
		
	}

	// game loop
	public void run() {
		while (thread == Thread.currentThread()) {
			clearDisplay();
			drawBoard();
			nextHighlited();

			try {
				Thread.currentThread();
				Thread.sleep(100);
			} catch (InterruptedException e) {}
			
		}
	}

	private void nextHighlited() {
		getKeyStates();
		int state = getKeyStates();
		if (state == 0) return;

		int col = index % 3;
		int row = index / 3;
		int tmp;
		
		if ((state & DOWN_PRESSED) != 0) {
			row = (row + 1) % 3;
		} else
			if ((state & UP_PRESSED) != 0) {
				tmp = row - 1;
				row = (tmp < 0)? (tmp + 3) : tmp % 3;
			} else
				if ((state & RIGHT_PRESSED) != 0) {
					col = (col + 1) % 3;
				} else
					if ((state & LEFT_PRESSED) != 0) {
						tmp = col - 1;
						col = (tmp < 0)? (tmp + 3) : tmp % 3;
					}

		index = (row * 3) + col;
		drawHighlight();
	}

	private void drawBoard() {
		int fg = display.getColor(Display.COLOR_FOREGROUND);
		int bg = display.getColor(Display.COLOR_BACKGROUND);
		int border = display.getColor(Display.COLOR_BORDER);
		int color = (bg == border) ? fg : border;
		graphics.setColor(color);

		drawVerticalLine(segment);
		drawVerticalLine((segment * 2) + 1);
		drawHorizontalLine(segment);
		drawHorizontalLine((segment * 2) + 1);
	}

	private void drawVerticalLine(int x) {
		graphics.drawLine(x, 0, x, size - 1);
	}

	private void drawHorizontalLine(int y) {
		graphics.drawLine(0, y, size - 1, y);
	}

	private void drawKiss(int index) {
		int factor = index - ((index / 3) * 3);
		int x = segment * factor + factor;
		x += BORDER;

		factor = index / 3;
		int y = segment * factor + factor;
		y += BORDER;

		int length = segment - 1 - (2 * BORDER);

		graphics.setColor(display.getColor(Display.COLOR_FOREGROUND));
		graphics.drawLine(x, y, x + length, y + length);
		graphics.drawLine(x + length, y, x, y + length);
	}

	private void drawHug(int index) {
		int factor = index - ((index / 3) * 3);
		int x = segment * factor + factor;
		x += BORDER;

		factor = index / 3;
		int y = segment * factor + factor;
		y += BORDER;

		int length = segment - 1 - (2 * BORDER);

		graphics.setColor(display.getColor(Display.COLOR_FOREGROUND));
		graphics.drawArc(x, y, length, length, 0, 360);
	}

	private void drawHighlight() {
		
		int factor = index - ((index / 3) * 3);
		int x = segment * factor + factor;
		x += BORDER / 2;

		factor = index / 3;
		int y = segment * factor + factor;
		y += BORDER / 2;

		int length = segment - 1 - BORDER;

		graphics.setColor(display
				.getColor(Display.COLOR_HIGHLIGHTED_FOREGROUND));
		graphics.setStrokeStyle(Graphics.DOTTED);
		graphics.drawRect(x, y, length, length);
		graphics.setStrokeStyle(Graphics.SOLID);
		flushGraphics();
	}
}

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
	private int col = 0;
	private int row = 0;
	private boolean errorOn = false; // an error message is displayed
	private int victory = 0;

	private int size; // length of the whole board
	private int segment; // length of a segment, not including border lines
	private int canvasWidth;
	private int canvasHeight;

	private static final int BORDER = 6;
	private static final int GREEN = 0x0000ff00;

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
		drawHighlight(true);

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
			nextHighlited();
			playNextMove();
			// clearDisplay();
			drawBoard();
			if (victory != 0) {
				clearDisplay();
				graphics.setColor(GREEN);
				String winner;
				if (victory == 1)
					winner = "Kisses";
				else
					winner = "Hugs";
				graphics.drawString(winner + " has won!", 0, getHeight() / 2,
						Graphics.TOP | Graphics.LEFT);
				flushGraphics();
				setTitle("Game Over");
				display.vibrate(300);
				hideNotify();

			}

			try {
				Thread.currentThread();
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}

		}
	}

	// returns true if the chosen box is already located
	private boolean isLegal() {
		int h = getHeight() - 1 - graphics.getFont().getHeight();
		if (board[index] != 0) {
			errorOn = true;
			graphics.setColor(GREEN);
			graphics.drawString("Illegal move!", 0, h, Graphics.TOP
					| Graphics.LEFT);
			return false;
		}
		// erasing the previous message
		// TODO - fix this silly thing
		if (errorOn) {
			graphics.setColor(display.getColor(Display.COLOR_BACKGROUND));
			graphics.drawString("Illegal move!", 0, h, Graphics.TOP
					| Graphics.LEFT);
		}
		return true;
	}

	private void playNextMove() {
		getKeyStates();
		int state = getKeyStates();
		if (state != 0) {
			if ((state & FIRE_PRESSED) != 0) {
				if (!isLegal())
					return; // the place is already located

				// 1 for a kiss, 2 for a hug
				if (player) {
					board[index] = 1;
					player = false;
				} else {
					board[index] = 2;
					player = true;
				}
				checkVictory();
			}
		}

	}

	private void checkVictory() {
		col = index % 3;
		row = index / 3;
		if (verticalVictory() || horizontalVictory() || diagonalVictory())
			if (player) // O won
				victory = 2;
			else
				// X won
				victory = 1;
	}

	private boolean horizontalVictory() {
		int sum = 1;
		int tmpCol = col;
		for (int i = 0; i < 3; i++) {
			tmpCol = (tmpCol + 1) % 3;
			sum *= board[(row * 3) + tmpCol];
		}
		if (sum == 1 || sum == 8) {
			System.out.println("vertical");
			return true;
		}

		return false;
	}

	private boolean verticalVictory() {
		int sum = 1;
		int tmpRow = row;
		for (int i = 0; i < 3; i++) {
			tmpRow = (tmpRow + 1) % 3;
			sum *= board[(tmpRow * 3) + col];
		}
		if (sum == 1 || sum == 8) {
			System.out.println("horizontal");
			return true;
		}

		return false;
	}

	private boolean diagonalVictory() {
		if (index % 2 == 0) // check only boxes on diagonals
		{
			int sum = 1;
			sum *= board[0];
			sum *= board[4];
			sum *= board[8];
			if (sum == 1 || sum == 8)
				return true;
			sum = 1;
			sum *= board[2];
			sum *= board[4];
			sum *= board[6];
			if (sum == 1 || sum == 8)
				return true;
		}
		return false;
	}

	private void nextHighlited() {
		getKeyStates();
		int state = getKeyStates();
		if (state == 0)
			return;

		drawHighlight(false); // remove the previous highlighting
		int col = index % 3;
		int row = index / 3;
		int tmp;

		if ((state & DOWN_PRESSED) != 0) {
			row = (row + 1) % 3;
		} else if ((state & UP_PRESSED) != 0) {
			tmp = row - 1;
			row = (tmp < 0) ? (tmp + 3) : tmp % 3;
		} else if ((state & RIGHT_PRESSED) != 0) {
			col = (col + 1) % 3;
		} else if ((state & LEFT_PRESSED) != 0) {
			tmp = col - 1;
			col = (tmp < 0) ? (tmp + 3) : tmp % 3;
		}

		index = (row * 3) + col;
		drawHighlight(true);
		// graphics.drawImage(new Image(), x, y, anchor)
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
		for (int i = 0; i < board.length; i++) {
			if (board[i] == 1)
				drawKiss(i);
			else if (board[i] == 2)
				drawHug(i);
		}
		flushGraphics();
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
		flushGraphics();
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

	/**
	 * @param on
	 *            - if true, will be displayed. if false - will be removed
	 */
	private void drawHighlight(boolean on) {

		int factor = index - ((index / 3) * 3);
		int x = segment * factor + factor;
		x += BORDER / 2;

		factor = index / 3;
		int y = segment * factor + factor;
		y += BORDER / 2;

		int length = segment - 1 - BORDER;

		if (on) {
			graphics.setColor(display
					.getColor(Display.COLOR_HIGHLIGHTED_FOREGROUND));
		} else {
			graphics.setColor(display.getColor(Display.COLOR_BACKGROUND));
		}
		graphics.setStrokeStyle(Graphics.DOTTED);
		graphics.drawRect(x, y, length, length);
		graphics.setStrokeStyle(Graphics.SOLID);
		flushGraphics();
	}
}

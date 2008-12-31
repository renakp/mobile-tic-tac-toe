package main;

import java.io.IOException;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.GameCanvas;

import net.java.dev.marge.chat.MargeMIDlet;
import net.java.dev.marge.communication.CommunicationListener;
import net.java.dev.marge.entity.Device;

public class GameScreen extends GameCanvas implements CommunicationListener,
		CommandListener {

	private Command back;
	private Device device;
	private final Display display;
	private final Graphics graphics;
	private int board[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	private boolean kiss = true; // true is kiss
	private int index = 0;
	private int col = 0;
	private int row = 0;
	private boolean errorOn = false; // an error message is displayed

	private int size; // length of the whole board
	private int segment; // length of a segment, not including border lines
	private int canvasWidth;
	private int canvasHeight;
	private int numMoves; // count the number of moves that were played so far

	private static final int BORDER = 6;
	private static final int GREEN = 0x0000ff00;
	private static final int KISS = 1;
	private static final int HUG = 2;

	public GameScreen(Display display) {
		super(true);
		this.display = display;
		this.back = new Command("Back", Command.BACK, 1);
		this.addCommand(back);		
		graphics = getGraphics();
	}

	
	public void setDevice(Device device) {
		this.device = device;
	}
	
	public void setKiss(boolean kiss){
		this.kiss = kiss;
	}
	

	protected void hideNotify() {}

	// draw board
	protected void showNotify() {
		canvasWidth = getWidth();
		canvasHeight = getHeight();
		size = (canvasWidth <= canvasHeight ? canvasWidth : canvasHeight);
		segment = (size - 2) / 3; // size - 2 line widths (1 each)
		size = (segment * 3) + 2; // fixing size after truncation

		clearDisplay();
		drawBoard();
		drawHighlight(true);

	}

	/**
	 * clears the screen
	 */
	private void clearDisplay() {
		graphics.setColor(display.getColor(Display.COLOR_BACKGROUND));
		graphics.fillRect(0, 0, canvasWidth, canvasHeight);

	}

	// game loop
	// public void run() {
	// while (thread == Thread.currentThread()) {
	// nextHighlited();
	// playNextMove();
	// // clearDisplay();
	// drawBoard();
	// if (victory != 0) {
	// clearDisplay();
	// graphics.setColor(GREEN);
	// String winner;
	// if (victory == 1)
	// winner = "Kisses";
	// else
	// if (victory == 2)
	// winner = "Hugs";
	// else
	// winner = "No one"; // in case of a tie
	// graphics.drawString(winner + " has won!", 0, getHeight() / 2,
	// Graphics.TOP | Graphics.LEFT);
	// flushGraphics();
	// setTitle("Game Over");
	// display.vibrate(300);
	// hideNotify();
	// }
	// try {
	// Thread.currentThread();
	// Thread.sleep(100);
	// } catch (InterruptedException e) {}
	// }
	// }

	public void playTurn() {
		Thread t = new Thread(new Runnable() {
			public void run() {
				boolean done = false;
				drawBoard();
				do {
					getKeyStates(); // once to purge previous key state
					int state = getKeyStates();

					if (isArrow(state)) 
						nextHighlited(state);
					if ((state & FIRE_PRESSED) != 0) {
						if (!isLegal()) continue;
						
						done = true;
						
						if (kiss) {
							board[index] = KISS;
						} else {
							board[index] = HUG;
						}
						numMoves++;
						drawBoard();
						sendMessage((byte)index);
						handleVictory(true); // you win
					}
					else{
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {}
					}
				} while (!done); // fire not pressed
				
			}
		});
		t.start();
	}

	private boolean isArrow(int state) {
		return ((state & (DOWN_PRESSED | UP_PRESSED | LEFT_PRESSED | RIGHT_PRESSED)) != 0);
	}

	// returns true if the chosen box is already located
	private boolean isLegal() {
		int h = getHeight() - 1 - graphics.getFont().getHeight();
		if (board[index] != 0) {
			errorOn = true;
			graphics.setColor(GREEN);
			graphics.drawString("Illegal move!", 0, h, Graphics.TOP
					| Graphics.LEFT);
			flushGraphics();
			return false;
		}
		// erasing the previous message
		// TODO - fix this silly thing
		if (errorOn) {
			graphics.setColor(display.getColor(Display.COLOR_BACKGROUND));
			graphics.drawString("Illegal move!", 0, h, Graphics.TOP
					| Graphics.LEFT);
			flushGraphics();
		}
		return true;
	}

	// private void playNextMove() {
	// getKeyStates();
	// int state = getKeyStates();
	// if (state != 0) {
	// if ((state & FIRE_PRESSED) != 0) {
	// if (!isLegal()) return; // the place is already located
	// this.sendMessage("BoolBool");
	// // 1 for a kiss, 2 for a hug
	// if (kiss) {
	// board[index] = 1;
	// kiss = false;
	// } else {
	// board[index] = 2;
	// kiss = true;
	// }
	// numMoves++;
	// checkVictory();
	// }
	// }
	// }

	private void handleVictory(boolean myTurn) {
		col = index % 3;
		row = index / 3;
		String message = "";

		if (verticalVictory() || horizontalVictory() || diagonalVictory())
			message = myTurn ? "You win! :-)" : "You lose :-(";
		else
			if (numMoves == 9)
				message = "It's a tie...";
			else
				return;

		clearDisplay();
		graphics.setColor(GREEN);
		graphics.drawString(message, 0, getHeight() / 2, Graphics.TOP
				| Graphics.LEFT);
		flushGraphics();
		setTitle("Game Over");
		display.vibrate(2000);
		display.flashBacklight(2000);

		leaveGame();
	}

	private boolean horizontalVictory() {
		int sum = 1;
		int tmpCol = col;
		for (int i = 0 ; i < 3 ; i++) {
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
		for (int i = 0 ; i < 3 ; i++) {
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
			if (sum == 1 || sum == 8) return true;
			sum = 1;
			sum *= board[2];
			sum *= board[4];
			sum *= board[6];
			if (sum == 1 || sum == 8) return true;
		}
		return false;
	}

	private void nextHighlited(int state) {
		if (state == 0) return;

		drawHighlight(false); // remove the previous highlighting
		int col = index % 3;
		int row = index / 3;
		int tmp;

		if ((state & DOWN_PRESSED) != 0) {
			row = (row + 1) % 3;
		} else
			if ((state & UP_PRESSED) != 0) {
				tmp = row - 1;
				row = (tmp < 0) ? (tmp + 3) : tmp % 3;
			} else
				if ((state & RIGHT_PRESSED) != 0) {
					col = (col + 1) % 3;
				} else
					if ((state & LEFT_PRESSED) != 0) {
						tmp = col - 1;
						col = (tmp < 0) ? (tmp + 3) : tmp % 3;
					}

		index = (row * 3) + col;
		drawHighlight(true);
		// graphics.drawImage(new Image(), x, y, anchor)
	}

	private void drawBoard() {
		// TODO - can we draw only the new x / o ?
		int fg = display.getColor(Display.COLOR_FOREGROUND);
		int bg = display.getColor(Display.COLOR_BACKGROUND);
		int border = display.getColor(Display.COLOR_BORDER);
		int color = (bg == border) ? fg : border;

		graphics.setColor(color);
		drawVerticalLine(segment);
		drawVerticalLine((segment * 2) + 1);
		drawHorizontalLine(segment);
		drawHorizontalLine((segment * 2) + 1);

		graphics.setColor(fg);
		for (int i = 0 ; i < board.length ; i++) {
			if (board[i] == 1)
				drawKiss(i);
			else
				if (board[i] == 2) drawHug(i);
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
		flushGraphics();
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

	
	public void receiveMessage(byte[] receivedMessage) {

		if(receivedMessage == null)
		{
			showAlert("receivedMessage is null", this, AlertType.INFO);
			return;
		}
		//parse the content of the received message
		//the possible content:
		//0-8 for square index
		//9 for cancellation of game by opponent
		//showAlert((receivedMessage!=null) +"", this, AlertType.INFO);
		byte message = receivedMessage[0];
		if(message == 9){ //end game and inform user
			//TODO
		}
		else
			//save the opponent's selection
			//check for victory
			if(( 0 <= message ) && ( message <= 8 )){
				board[message] = kiss ? HUG : KISS;
				handleVictory(false);
				//enable new move if there is no victory yet
				playTurn();
				
			}
	}

	public void errorOnReceiving(IOException e) {
		showAlert("couldn't recieve message: "+ e.getMessage(), this, AlertType.INFO);
		//e.printStackTrace();
		leaveGame();
	}

	public void errorOnSending(IOException e) {
		showAlert("couldn't send message: "+e.getMessage(), this, AlertType.INFO);
		//e.printStackTrace();
		leaveGame();
	}

	public void showAlert(String message, Displayable d, AlertType type) {
		Alert alert = new Alert("", message, null, type);
		alert.setTimeout(Alert.FOREVER);
		display.setCurrent(alert, d);

	}

	public void leaveGame() {
		device.stopListenning();
		this.device.close();
		MargeMIDlet.instance.showMainMenu();
	}


	public void commandAction(Command c, Displayable d) {
		if (c == this.back)
			leaveGame();
		
	}

	public void sendMessage(byte message) {
		byte[] b = new byte[1];
		b[0] = message;
		device.send(b);

	}

}

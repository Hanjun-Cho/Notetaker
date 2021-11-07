import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Cursor {

	public BufferedImage texture;
	public int cursorX;
	public int cursorY;
	
	public Cursor() {
		importTexture();
	}
	
	private void importTexture() {
		try {
			texture = Main.loadImage(Settings.cursorTexturePath);
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	public void update() {
		moveCursor();
	}
	
	private void moveCursor() {
		//movePos -> position of cursor if the cursor is moving
		//endPos -> position of cursor if the cursor is stationary at the end of the line
		int movePos = Main.activeWindow.startCursorPosition + (Main.FONT_WIDTH * Main.activeWindow.cursorIndex) + 2;
		int endPos = Main.activeWindow.startCursorPosition + ((Main.MAX_CHARACTERS_PER_LINE - 1) * Main.FONT_WIDTH) + 2;
		
		//for targetX, there is a 2 pixel offset at index 0 and is completely fine everywhere else, so the first part is necessary, dont remove...
		int targetX = Main.activeWindow.selectedIndex == 0 ? Main.activeWindow.startCursorPosition : Math.min(movePos, endPos);
		int targetY = Main.activeWindow.cursorYOffset + (Main.LINE_HEIGHT * Main.activeWindow.selectedText);
		cursorX = (int)Maths.Lerp(cursorX, targetX, Settings.cursorXLerpSpeed);
		cursorY = (int)Maths.Lerp(cursorY, targetY, Settings.cursorYLerpSpeed);
	}
	
	public void paint(Graphics g) {
		g.drawImage(texture, cursorX, cursorY, 10, 20, null, null);
	}
}

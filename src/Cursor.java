import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Cursor {

	public BufferedImage texture;
	
	public int cursorX;
	public int cursorY;
	
	public Cursor() {
		try {
			texture = Main.loadImage("res/Cursor.png");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void update() {
		int targetX = Main.activeWindow.windowXOffset + Main.activeWindow.sideBarWidth + (Main.FONT_WIDTH * Main.activeWindow.selectedIndex) + 2;
		int targetY = Main.activeWindow.cursorYOffset + (20 * Main.activeWindow.selectedText) + Main.activeWindow.cursorYOffsetPos;
		cursorX = (int)Main.Lerp(cursorX, targetX, 0.5f);
		cursorY = (int)Main.Lerp(cursorY, targetY, 0.5f);
		cursorX = Math.min(cursorX, Main.SCREEN_WIDTH / 2 - (2 * Main.activeWindow.windowXOffset) - Main.FONT_WIDTH + 3);
	}
	
	public void paint(Graphics g) {
		g.drawImage(texture, cursorX, cursorY, 10, 20, null, null);
	}
}

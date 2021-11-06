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
		int targetX = Main.activeWindow.selectedIndex == 0 ? Main.activeWindow.windowXOffset + Main.activeWindow.sideBarWidth : Math.min(Main.activeWindow.windowXOffset + Main.activeWindow.sideBarWidth + (Main.FONT_WIDTH * Main.activeWindow.cursorIndex) + 2, Main.activeWindow.windowXOffset + Main.activeWindow.sideBarWidth + ((Main.MAX_CHARACTERS_PER_LINE - 1) * Main.FONT_WIDTH) + 2);
		int targetY = Main.activeWindow.cursorYOffset + (20 * Main.activeWindow.selectedText) + Main.activeWindow.cursorYOffsetPos;
		cursorX = (int)Main.Lerp(cursorX, targetX, 0.5f);
		cursorY = (int)Main.Lerp(cursorY, targetY, 0.5f);
	}
	
	public void paint(Graphics g) {
		g.drawImage(texture, cursorX, cursorY, 10, 20, null, null);
	}
}

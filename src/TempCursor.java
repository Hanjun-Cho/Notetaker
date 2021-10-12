import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class TempCursor {

	public BufferedImage texture;
	
	public int cursorX;
	public int cursorY;
	
	public boolean visible = true;
	
	public TempCursor() {
		try {
			texture = Main.loadImage("res/TempCursor.png");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void update() {
		cursorX = Main.activeWindow.windowXOffset + Main.activeWindow.sideBarWidth + (Main.FONT_WIDTH * Main.activeWindow.highlightIndex);
		cursorY = Main.activeWindow.cursorYOffset + (20 * Main.activeWindow.highlightText);
	}
	
	public void paint(Graphics g) {
		if(visible) g.drawImage(texture, cursorX, cursorY, 10, 20, null, null);
	}
}

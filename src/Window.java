import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class Window {
	
	public ArrayList<Text> content = new ArrayList<Text>();
	public boolean active = false;
	public boolean leftWindow = false;
	
	public int selectedText = 0;
	public int selectedIndex = 0;
	
	public int windowXOffset = leftWindow ? 15 : 15 + Main.SCREEN_WIDTH/2;
	public int windowYOffset = 25;
	public int cursorYOffset = 11;
	
	public Window(boolean leftWindow) {
		this.leftWindow = leftWindow;
		windowXOffset = leftWindow ? 15 : 15 + Main.SCREEN_WIDTH/2;
	}
	
	public void paint(Graphics g) {
		g.setColor(new Color(20, 20, 20));
		if(!active) g.setColor(new Color(30, 30, 30));
		
		if(leftWindow) 
			g.fillRect(0, 0, Main.SCREEN_WIDTH / 2, Main.SCREEN_HEIGHT);
		else 
			g.fillRect(Main.SCREEN_WIDTH/2, 0, Main.SCREEN_WIDTH/2, Main.SCREEN_HEIGHT);
		
		g.setColor(new Color(200, 200, 200));
		
		for(int i = 0; i < content.size(); i++) {
			g.drawString(content.get(i).content, windowXOffset, windowYOffset + (i * 20));
		}
	}
}

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class Window {
	
	public ArrayList<Text> content = new ArrayList<Text>();
	public boolean active = false;
	public boolean leftWindow = false;
	
	public int selectedText = 0;
	public int selectedIndex = 0;
	
	public int windowXOffset;
	public int windowYOffset = 25;
	public int cursorYOffset = 11;
	public int sideBarWidth = 61;
	public int textOffsetX = 0;
	int windowRenderOffsetX;
	
	public int highlightText = 0;
	public int highlightIndex = 0;
	public int cursorYOffsetPos = 0;
	
	Color backgroundColor;
	Color sidebarBackgroundColor;
	
	int sideBarTextOpacity;
	int mainTextOpacity;
	
	public Window(boolean leftWindow) {
		this.leftWindow = leftWindow;
	}
	
	//(TODO) -> animate the text when scrolling horizontally!
	public void update() {
		windowXOffset = leftWindow ? 11 : 11 + Main.SCREEN_WIDTH/2 - (sideBarWidth/7);
		windowRenderOffsetX = leftWindow ? 0 : Main.SCREEN_WIDTH/2 - (sideBarWidth/7);
		backgroundColor = active ? new Color(25, 25, 25) : new Color(22, 22, 22);
		sidebarBackgroundColor = active ? new Color(20, 20, 20) : new Color(20, 20, 20);
		textOffsetX = Math.max(0, Main.activeWindow.selectedIndex - Main.MAX_CHARACTERS_PER_LINE + 1) * 11;
		
		sideBarTextOpacity = active ? 255 : 100;
		mainTextOpacity = active ? 255 : 100;
	}
	
	public void paint(Graphics g) {
		g.setColor(backgroundColor);
		g.fillRect(windowRenderOffsetX, 0, Main.SCREEN_WIDTH/2, Main.SCREEN_HEIGHT);
		
		for(int i = 0; i < content.size(); i++) {
			g.setColor(new Color(200, 200, 200, mainTextOpacity));
			g.drawString(content.get(i).content, windowXOffset + sideBarWidth - textOffsetX, windowYOffset + (i * 20));
		}
		
		g.setColor(sidebarBackgroundColor);
		g.fillRect(windowRenderOffsetX, 0, sideBarWidth, Main.SCREEN_HEIGHT);
		
		for(int i = 0; i < content.size(); i++) {
			int Xoffset = leftWindow ? 15 : 15 + Main.SCREEN_WIDTH/2 - (sideBarWidth/7);
			
			if(i >= 0 && i < 9) {
				Xoffset += 20;
			}
			else if(i >= 9 && i < 99) {
				Xoffset += 10;
			}
			
			g.setColor(new Color(155, 155, 155, sideBarTextOpacity));
			g.drawString(String.valueOf(i + 1), Xoffset, windowYOffset + (i * 20));
		}
		
		g.setColor(backgroundColor);
		g.fillRect(sideBarWidth, 0, (sideBarWidth/7), Main.SCREEN_HEIGHT);
	}
}

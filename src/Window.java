import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class Window {
	
	public ArrayList<Text> content = new ArrayList<Text>();
	public boolean active = false;
	public boolean leftWindow = false;
	
	public Window(boolean leftWindow) {
		this.leftWindow = leftWindow;
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
			g.drawString(content.get(i).content, 15, 25 + (i * 20));
		}
	}
}

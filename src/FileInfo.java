import java.awt.Color;
import java.awt.Graphics;
import java.io.File;

public class FileInfo {

	public int infoHeight = 30;
	public String newFileString = "";
	public String openFileString = "";
	public String path = "";
	public String originalPath = "";
	public int selectedIndex = 0;
	
	public FileInfo() {
		File oriPath = new File("files");
		path = oriPath.getAbsolutePath() + "/";
		originalPath = path;
	}
	
	public void paint(Graphics g) {
		g.setColor(new Color(30, 30, 30));
		g.fillRect(0, 0, Main.SCREEN_WIDTH, infoHeight);
		
		if(Main.activeWindow.state == ProgramState.Editor) {			
			String fileName = Main.activeWindow.selectedFile == null ? "new file" : Main.activeWindow.selectedFile.getName();
			int width = g.getFontMetrics().stringWidth(fileName);
			int x = width + 16;
			g.setColor(new Color(180, 180, 180, 200));
			g.drawString(fileName, 10, 21);
			
			if(Main.activeWindow.needsSave) {				
				g.setColor(new Color(212, 99, 99, 200));
				g.drawString("*", x, 23);
			}
			
			String lineCounter = "lines: " + String.valueOf(Main.activeWindow.content.size());
			width = g.getFontMetrics().stringWidth(lineCounter);
			g.setColor(new Color(180, 180, 180, 200));
			g.drawString(lineCounter, Main.SCREEN_WIDTH - width - 25, 21);
		}
		else if(Main.activeWindow.state == ProgramState.NewFile) {
			g.setColor(new Color(99, 142, 212, 200));
			g.drawString("new file: ", 10, 21);
			int width = g.getFontMetrics().stringWidth("new file: ");
			
			g.setColor(new Color(210, 210, 210, 210));
			g.drawString(newFileString, width + 5, 21);
		}
		else if(Main.activeWindow.state == ProgramState.OpenFile) {
			g.setColor(new Color(99, 142, 212, 200));
			g.drawString("open file: ", 10, 21);
			int width = g.getFontMetrics().stringWidth("open file: ");
			
			g.setColor(new Color(210, 210, 210, 210));
			g.drawString(openFileString, width + 5, 21);
		}
	}
}

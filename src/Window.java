import java.awt.Color;
import java.awt.Graphics;
import java.io.File;
import java.util.ArrayList;

public class Window {
	
	public ProgramState state = ProgramState.Editor;
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
	
	public int cursorIndex = 0;
	
	private Color backgroundColor = new Color(0, 0, 0);
	private Color sidebarBackgroundColor = new Color(0, 0, 0);
	
	public int highlightSelectedText = 0;
	public int highlightSelectedIndex = 0;
	
	public int cursorYIndex;
	public int selectedLeftIndex = 0;
	public int selectedTopText = 0;
	public int startCursorPosition = 0;
	public int textOffsetY;
	public int fileSelectIndex = 0;
	public int fileTopSelectIndex = 0;
	
	public boolean needsSave = false;
	
	public ArrayList<File> files = new ArrayList<File>();
	public File selectedFile;
	
	public Window(boolean leftWindow) {
		this.leftWindow = leftWindow;
		if(leftWindow) active = true;
		content.add(new Text());
	}
	
	public void update() {
		selectedLeftIndex = Math.max(0, selectedIndex - Main.MAX_CHARACTERS_PER_LINE + 1);
		selectedTopText = Math.max(0, selectedText - Main.MAX_LINES + 2);
		fileTopSelectIndex = Math.max(0, fileSelectIndex - Main.MAX_FILE_DISPLAY + 1);
		windowXOffset = leftWindow ? 11 : 11 + Main.SCREEN_WIDTH/2 - (sideBarWidth/7);
		backgroundColor = active ? new Color(25, 25, 25) : new Color(22, 22, 22);
		sidebarBackgroundColor = active ? new Color(20, 20, 20) : new Color(20, 20, 20);
		cursorIndex = selectedIndex - selectedLeftIndex;
		startCursorPosition = windowXOffset + sideBarWidth;
		cursorYIndex = selectedText - selectedTopText;
		fileSelectIndex = Math.min(fileSelectIndex, files.size());
		
		int targetTextOffsetX = Math.max(0, selectedLeftIndex) * 11;
		int targetTextOffsetY = selectedTopText * Main.LINE_HEIGHT;
		textOffsetX = (int)Maths.Lerp(textOffsetX, targetTextOffsetX, Settings.textXLerpSpeed);
		textOffsetY = (int)Maths.Lerp(textOffsetY, targetTextOffsetY, Settings.textYLerpSpeed);
	}
	
	public void paintBackground(Graphics g) {
		g.setColor(backgroundColor);
		g.fillRect(leftWindow ? -7 : Main.SCREEN_WIDTH/2 - (sideBarWidth/7), 0, Main.SCREEN_WIDTH/2, Main.SCREEN_HEIGHT);
	}
	
	public void paintContent(Graphics g) {
		for(int i = Math.max(0, selectedTopText); i < Math.min(content.size(), selectedTopText + Main.MAX_LINES - 1); i++) {
			g.setColor(new Color(200, 200, 200, active ? 255 : 100));
			
			if(i >= 0 && i < content.size()) {				
				if(!leftWindow) {
					if(active) g.drawString(content.get(i).content, windowXOffset + sideBarWidth - textOffsetX, windowYOffset + (i * 20) - textOffsetY + Main.fileInfo.infoHeight);
					if(!active) g.drawString(content.get(i).content.substring(Math.min(Math.max(0, selectedLeftIndex - 1), content.get(i).content.length())), 
							0 > selectedLeftIndex - 1 ? windowXOffset + sideBarWidth - textOffsetX : windowXOffset + sideBarWidth - Main.FONT_WIDTH, windowYOffset + (i * 20) - textOffsetY + Main.fileInfo.infoHeight);
				}
				else {
					if(active) g.drawString(content.get(i).content, windowXOffset + sideBarWidth - textOffsetX, windowYOffset + (i * 20) - textOffsetY + Main.fileInfo.infoHeight);
					if(!active) g.drawString(content.get(i).content.substring(0, Math.min(content.get(i).content.length(), selectedLeftIndex + Main.MAX_CHARACTERS_PER_LINE + 1)), windowXOffset + sideBarWidth - textOffsetX, windowYOffset + (i * 20) - textOffsetY + Main.fileInfo.infoHeight);
				}
			}
		}
	}
	
	public void paintSideBarBackground(Graphics g) {
		g.setColor(state == ProgramState.Editor ? sidebarBackgroundColor : backgroundColor);
		if(state != ProgramState.Editor) System.out.println("TEST");
		g.fillRect(leftWindow ? 0 : Main.SCREEN_WIDTH/2 - (sideBarWidth/7), 0, sideBarWidth, Main.SCREEN_HEIGHT);
	}
	
	public void paintSideBarText(Graphics g) {
		for(int i = 0; i < Math.min(content.size(), Main.MAX_LINES - 1); i++) {
			int j = i + selectedTopText;
			int Xoffset = leftWindow ? 15 : 15 + Main.SCREEN_WIDTH/2 - (sideBarWidth/7);
			
			if(j >= 0 && j < 9) {
				Xoffset += 20;
			}
			else if(j >= 9 && j < 99) {
				Xoffset += 10;
			}
			
			g.setColor(new Color(155, 155, 155, active ? 255 : 100));
			g.drawString(String.valueOf(j + 1), Xoffset, windowYOffset + (i * 20) + Main.fileInfo.infoHeight);
		}
	}
	
	public void paint(Graphics g) {
		paintBackground(g);
		
		if(state == ProgramState.Editor) {			
			paintContent(g);
			paintSideBarBackground(g);
			paintSideBarText(g);
		}
		else if(state == ProgramState.NewFile || state == ProgramState.OpenFile) {
			boolean newFile = state == ProgramState.NewFile;
			files = FileManager.getFiles(Main.fileInfo.path, newFile ? Main.fileInfo.newFileString : Main.fileInfo.openFileString);
			
			if(files != null) {
				for(int i = 0; i < Math.min(Main.MAX_FILE_DISPLAY, files.size()); i++) {			
					int yOffset = (i * 58);
					int xOffset = leftWindow ? 5 : Main.SCREEN_WIDTH/2 - (sideBarWidth/7) + 5;
					if(i >= 1) yOffset -= 2;
					if(i == (fileSelectIndex - fileTopSelectIndex)) {
						g.setColor(new Color(25, 25, 25));
					}
					else {
						g.setColor(new Color(30, 30, 30));						
					}
					g.fillRect(xOffset, windowYOffset + 10 + yOffset, Main.SCREEN_WIDTH / 2 - 17, i == 0 ? 51 : 53);
					
					g.setColor(new Color(200, 200, 200, 150));
					if(fileTopSelectIndex + i < files.size()) g.drawString(files.get(fileTopSelectIndex + i).getName(), xOffset + 15, windowYOffset + 10 + yOffset + 31);
				}
			}
		}
	}
}

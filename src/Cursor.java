import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Cursor {

	public BufferedImage cursorTexture;
	public BufferedImage tempCursorTexture;
	public int cursorX;
	public int cursorY;
	private boolean tempCursor;
	public boolean render = true;
	
	public Cursor(boolean tempCursor) {
		importTexture();
		this.tempCursor = tempCursor;
	}
	
	private void importTexture() {
		try {
			cursorTexture = Main.loadImage(Settings.cursorTexturePath);
			tempCursorTexture = Main.loadImage(Settings.tempCursorTexturePath);
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	public void update() {
		moveCursor();
		
		if(tempCursor) {			
			if(Main.activeWindow.selectedLeftIndex <= Main.activeWindow.highlightSelectedIndex) {
				if(Main.activeWindow.highlightSelectedText >= Main.activeWindow.selectedTopText) {
					render = true;
				}
				else {
					render = false;
				}
			}
			else {
				render = false;
			}
		}
	}
	
	private void moveCursor() {
		if(!tempCursor) {
			if(Main.activeWindow.state == ProgramState.Editor) {				
				//movePos -> position of cursor if the cursor is moving
				//endPos -> position of cursor if the cursor is stationary at the end of the line
				int movePos = Main.activeWindow.startCursorPosition + (Main.FONT_WIDTH * Main.activeWindow.cursorIndex) + 2;
				int endPos = Main.activeWindow.startCursorPosition + ((Main.MAX_CHARACTERS_PER_LINE - 1) * Main.FONT_WIDTH) + 2;
				
				//for targetX, there is a 2 pixel offset at index 0 and is completely fine everywhere else, so the first part is necessary, dont remove...
				int targetX = Main.activeWindow.selectedIndex == 0 ? Main.activeWindow.startCursorPosition : Math.min(movePos, endPos);
				int targetY = Math.max(Main.activeWindow.cursorYOffset + Main.fileInfo.infoHeight, Math.min(Main.activeWindow.cursorYOffset + (Main.LINE_HEIGHT * Main.activeWindow.cursorYIndex) + Main.fileInfo.infoHeight, Main.activeWindow.cursorYOffset + (Main.LINE_HEIGHT * (Main.MAX_LINES - 2) + Main.fileInfo.infoHeight)));
				cursorX = (int)Maths.Lerp(cursorX, targetX, Settings.cursorXLerpSpeed);
				cursorY = (int)Maths.Lerp(cursorY, targetY, Settings.cursorYLerpSpeed);			
			}
			else if(Main.activeWindow.state == ProgramState.NewFile || Main.activeWindow.state == ProgramState.OpenFile) {
				//for targetX, there is a 2 pixel offset at index 0 and is completely fine everywhere else, so the first part is necessary, dont remove...
				int targetX = (Main.activeWindow.state == ProgramState.NewFile ? 100 : 110) + (Main.FONT_WIDTH * Main.fileInfo.selectedIndex) + 6;
				cursorX = (int)Maths.Lerp(cursorX, targetX, Settings.cursorXLerpSpeed);
				cursorY = (int)Maths.Lerp(cursorY, 7, Settings.cursorYLerpSpeed);
			}
		}
		else {
			//movePos -> position of cursor if the cursor is moving
			//endPos -> position of cursor if the cursor is stationary at the end of the line
			int movePos = Main.activeWindow.startCursorPosition + (Main.FONT_WIDTH * Main.activeWindow.highlightSelectedIndex);
			int endPos = Main.activeWindow.startCursorPosition + ((Main.MAX_CHARACTERS_PER_LINE - 1) * Main.FONT_WIDTH) + 2;
			
			cursorX = Main.activeWindow.highlightSelectedIndex == 0 ? Main.activeWindow.startCursorPosition : Math.min(movePos, endPos);
			cursorY = Main.activeWindow.cursorYOffset + (Main.LINE_HEIGHT * Main.activeWindow.highlightSelectedText) - 2 + Main.fileInfo.infoHeight;
		}
	}
	
	public void paint(Graphics g) {
		if(render) g.drawImage(tempCursor ? tempCursorTexture : cursorTexture, cursorX, cursorY, 10, 20, null, null);
	}
}

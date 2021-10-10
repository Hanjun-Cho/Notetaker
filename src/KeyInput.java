

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyInput extends KeyAdapter {

	boolean controlHeld = false;
	boolean shiftHeld = false;
	boolean altHeld = false;
	
	boolean StartofLine = false;
	boolean EndofLine = false;
	
	int shiftStartLine = 0;
	int shiftStartIndex = 0;
	int shiftEndLine = 0;
	int shiftEndIndex = 0;
	
	boolean startedTyping = false;
	
	public void keyPressed(KeyEvent e) {
		if(MainApp.state == MainApp.programState.editor) {			
			highlight(e);
			if(!controlHeld) type(e);
			move(e);
			delete(e);
		}
		else if(MainApp.state == MainApp.programState.newFile) {
			typeFileName(e);
			deleteFileName(e);
		}
		else if(MainApp.state == MainApp.programState.openFile) {
			moveFileSelection(e);
		}

		commands(e);
	}
	
	public void moveFileSelection(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_UP) {
			if(MainApp.fileSelectionIndex != 0) {
				MainApp.fileSelectionIndex--;
			}
		}
		
		if(e.getKeyCode() == KeyEvent.VK_DOWN) {
			if(MainApp.fileSelectionIndex < MainApp.files.length - 1) {
				MainApp.fileSelectionIndex++;
			}
		}
	}
	
	public void deleteFileName(KeyEvent e) {
		if(e.getKeyCode() == 8) {			
			if(MainApp.fileNameIndex != 0) {
				MainApp.fileNameIndex--;
						
				String text = MainApp.fileName.substring(0, Math.max(0, MainApp.fileNameIndex));
				text += MainApp.fileName.substring(MainApp.fileNameIndex + 1);
				MainApp.fileName = text;
			}
		}
	}
	
	public void typeFileName(KeyEvent e) {
		if((e.getKeyCode() >= 44 && e.getKeyCode() <= 57) ||
				(e.getKeyCode() >= 65 && e.getKeyCode() <= 93) ||
				(e.getKeyCode() == 59) ||
				(e.getKeyCode() == 32) ||
				(e.getKeyCode() == 61) ||
				(e.getKeyCode() == 151) ||
				(e.getKeyCode() == 152) ||
				(e.getKeyCode() == 153) ||
				(e.getKeyCode() == 222)) {
			String text = MainApp.fileName.substring(0, MainApp.fileNameIndex);
			text += e.getKeyChar() + MainApp.fileName.substring(MainApp.fileNameIndex);
			MainApp.fileName = text;
			MainApp.fileNameIndex++;
		}
	}
	
	public void commands(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_CONTROL) {
			controlHeld = true;
		}
	
		if(e.getKeyCode() == KeyEvent.VK_SHIFT) {
			if(!shiftHeld) {
				shiftStartLine = MainApp.selectedText;
				shiftStartIndex = MainApp.selectedIndex;				
			}
			
			shiftHeld = true;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_ALT) {
			altHeld = true;
		}
		
		if(controlHeld && e.getKeyCode() == KeyEvent.VK_S) {
			MainApp.save();
		}
		
		if(controlHeld && e.getKeyCode() == KeyEvent.VK_N) {
			MainApp.state = MainApp.programState.newFile;
			MainApp.listFiles();
		}
		
		if(controlHeld && e.getKeyCode() == KeyEvent.VK_O) {
			MainApp.state = MainApp.programState.openFile;
			MainApp.listFiles();
		}
		
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			MainApp.state = MainApp.programState.editor;
		}
	
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			if(MainApp.state == MainApp.programState.newFile) {
				MainApp.state = MainApp.programState.editor;
				System.out.println("Created File " + MainApp.fileName);
				MainApp.createFile();
			}
			else if(MainApp.state == MainApp.programState.openFile) {
				MainApp.state = MainApp.programState.editor;
				System.out.println("Opened File " + MainApp.files[MainApp.fileSelectionIndex].getName());
				MainApp.openFile();
			}
		}
	}
	
	public void highlight(KeyEvent e) {
		MainApp.changed = true;
		if(!shiftHeld && controlHeld && e.getKeyCode() == KeyEvent.VK_H) {
			if(shiftStartLine == shiftEndLine) {
				MainApp.inStart(Math.min(shiftEndIndex, shiftStartIndex), Math.max(shiftEndIndex, shiftStartIndex), HighlightType.Blue);
			}
			else {
				for(int i = Math.min(shiftStartLine, shiftEndLine) + 1; i < Math.max(shiftStartLine, shiftEndLine); i++) {
					MainApp.texts.get(i).highlights.add(new Highlight(0, MainApp.texts.get(i).text.length(), HighlightType.Blue));
				}
				
				if(shiftStartLine < shiftEndLine) {
					MainApp.texts.get(shiftStartLine).highlights.add(new Highlight(shiftStartIndex, MainApp.texts.get(shiftStartLine).text.length(), HighlightType.Blue));
					MainApp.texts.get(shiftEndLine).highlights.add(new Highlight(0, shiftEndIndex, HighlightType.Blue));
				}
				else {
					MainApp.texts.get(shiftEndLine).highlights.add(new Highlight(shiftEndIndex, MainApp.texts.get(shiftEndLine).text.length(), HighlightType.Blue));
					MainApp.texts.get(shiftStartLine).highlights.add(new Highlight(0, shiftStartIndex, HighlightType.Blue));
				}
			}
		}
		
		if(!shiftHeld && controlHeld && e.getKeyCode() == KeyEvent.VK_J) {
			if(shiftStartLine == shiftEndLine) {
				MainApp.inStart(Math.min(shiftEndIndex, shiftStartIndex), Math.max(shiftEndIndex, shiftStartIndex), HighlightType.Red);
			}
			else {
				for(int i = Math.min(shiftStartLine, shiftEndLine) + 1; i < Math.max(shiftStartLine, shiftEndLine); i++) {
					MainApp.texts.get(i).highlights.add(new Highlight(0, MainApp.texts.get(i).text.length(), HighlightType.Red));
				}
				
				if(shiftStartLine < shiftEndLine) {
					MainApp.texts.get(shiftStartLine).highlights.add(new Highlight(shiftStartIndex, MainApp.texts.get(shiftStartLine).text.length(), HighlightType.Red));
					MainApp.texts.get(shiftEndLine).highlights.add(new Highlight(0, shiftEndIndex, HighlightType.Red));
				}
				else {
					MainApp.texts.get(shiftEndLine).highlights.add(new Highlight(shiftEndIndex, MainApp.texts.get(shiftEndLine).text.length(), HighlightType.Red));
					MainApp.texts.get(shiftStartLine).highlights.add(new Highlight(0, shiftStartIndex, HighlightType.Red));
				}
			}
		}
	}
	
	public void delete(KeyEvent e) {
		MainApp.changed = true;
		if(e.getKeyCode() == 8) {
			if(!controlHeld) {				
				if(MainApp.selectedIndex != 0) {
					MainApp.selectedIndex--;
					
					String text = MainApp.texts.get(MainApp.selectedText).text.substring(0, Math.max(0, MainApp.selectedIndex));
					text += MainApp.texts.get(MainApp.selectedText).text.substring(MainApp.selectedIndex + 1);
					MainApp.texts.get(MainApp.selectedText).text = text;
					
					if(MainApp.texts.get(MainApp.selectedText).lastSpaces.size() != 0 && MainApp.texts.get(MainApp.selectedText).lastSpaces.get(0) == MainApp.selectedIndex) {
						MainApp.texts.get(MainApp.selectedText).lastSpaces.remove(0);
					}
				}
				else {
					if(MainApp.selectedIndex == 0 && MainApp.selectedText != 0) {
						String text = MainApp.texts.get(MainApp.selectedText).text;
						MainApp.texts.remove(MainApp.selectedText);							
						
						MainApp.selectedText--;
						int length = MainApp.texts.get(MainApp.selectedText).text.length();
						MainApp.texts.get(MainApp.selectedText).text += text;
						
						for(int i = 0; i < text.length(); i++) {
							if(text.charAt(i) == ' ') {
								MainApp.texts.get(MainApp.selectedText).lastSpaces.add(0, i + length);
							}
						}
						
						MainApp.selectedIndex = MainApp.texts.get(MainApp.selectedText).text.length() - text.length();
					}
				}
			}
			else {
				if(MainApp.texts.get(MainApp.selectedText).lastSpaces.size() > 0) {
					String text = MainApp.texts.get(MainApp.selectedText).text.substring(0, 
							MainApp.texts.get(MainApp.selectedText).lastSpaces.remove(0));
					int length = MainApp.texts.get(MainApp.selectedText).text.substring(MainApp.selectedIndex).length();
					text += MainApp.texts.get(MainApp.selectedText).text.substring(MainApp.selectedIndex);
					MainApp.texts.get(MainApp.selectedText).text = text;
					MainApp.selectedIndex = text.length() - length;
					
					if(MainApp.selectedIndex == 0 && MainApp.selectedText != 0) {
						if(MainApp.texts.get(MainApp.selectedText).text.length() == 0) {
							MainApp.texts.remove(MainApp.selectedText);							
							MainApp.selectedIndex = MainApp.texts.get(MainApp.selectedText - 1).text.length();
						}
						
						MainApp.selectedText--;
					}
					else {
						MainApp.texts.get(MainApp.selectedText).lastSpaces.add(0);						
					}
				}
			}
		}
		
		if(controlHeld && shiftHeld && e.getKeyCode() == KeyEvent.VK_D) {
			MainApp.texts.remove(MainApp.selectedText);
			
			if(MainApp.texts.size() == 0) {
				MainApp.texts.add(new Text(0, 0));
				MainApp.selectedIndex = 0;
			}
			else {
				MainApp.selectedText--;
				MainApp.selectedIndex = MainApp.texts.get(MainApp.selectedText).text.length();
			}
		}
	}
	
	public void type(KeyEvent e) {
		MainApp.changed = true;
		if((e.getKeyCode() >= 44 && e.getKeyCode() <= 57) ||
				(e.getKeyCode() >= 65 && e.getKeyCode() <= 93) ||
				(e.getKeyCode() == 59) ||
				(e.getKeyCode() == 32) ||
				(e.getKeyCode() == 61) ||
				(e.getKeyCode() == 151) ||
				(e.getKeyCode() == 152) ||
				(e.getKeyCode() == 153) ||
				(e.getKeyCode() == 222)) {
			String text = MainApp.texts.get(MainApp.selectedText).text.substring(0, MainApp.selectedIndex);
			text += e.getKeyChar() + MainApp.texts.get(MainApp.selectedText).text.substring(MainApp.selectedIndex);
			MainApp.texts.get(MainApp.selectedText).text = text;
			
			if(e.getKeyChar() == KeyEvent.VK_SPACE) {
				MainApp.texts.get(MainApp.selectedText).lastSpaces.add(0, MainApp.selectedIndex);
			}
			
			MainApp.selectedIndex++;
			
			if(MainApp.texts.get(MainApp.selectedText).text.length() > MainApp.characters) {
				MainApp.texts.add(MainApp.selectedText + 1, new Text(0, 0));
				
				if(MainApp.texts.get(MainApp.selectedText).lastSpaces.get(0) != 0) {					
					text = MainApp.texts.get(MainApp.selectedText).text.substring(MainApp.texts.get(MainApp.selectedText).lastSpaces.get(0) + 1);
					MainApp.texts.get(MainApp.selectedText).text = MainApp.texts.get(MainApp.selectedText).text.substring(0, MainApp.texts.get(MainApp.selectedText).lastSpaces.remove(0));
					MainApp.texts.get(MainApp.selectedText + 1).text += text;
					MainApp.selectedText++;
					MainApp.selectedIndex = text.length();
				}
				else {
					MainApp.selectedText++;
					MainApp.selectedIndex = 0;
				}
			}
		}
		
		if(!shiftHeld && e.getKeyCode() == 10) {
			String text = MainApp.texts.get(MainApp.selectedText).text.substring(MainApp.selectedIndex);
			MainApp.texts.get(MainApp.selectedText).text = MainApp.texts.get(MainApp.selectedText).text.substring(0, MainApp.selectedIndex);
			
			for(int i = 0; i < MainApp.texts.get(MainApp.selectedText).lastSpaces.size(); i++) {
				if(MainApp.texts.get(MainApp.selectedText).lastSpaces.get(i) >= MainApp.selectedIndex) {
					MainApp.texts.get(MainApp.selectedText).lastSpaces.remove(i);
					i--;
				}
			}
			
			MainApp.selectedText++;
			MainApp.selectedIndex = 0;
			MainApp.texts.add(MainApp.selectedText, new Text(text, 0, 0));
			
			for(int i = 0; i < text.length(); i++) {
				if(text.charAt(i) == ' ') {
					MainApp.texts.get(MainApp.selectedText).lastSpaces.add(0, i);
				}
			}
		}
	}
	
	int turns = 0;
	int turns2 = 0;
	
	public void move(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
			if(!controlHeld) {				
				if(MainApp.selectedIndex + 1 <= MainApp.texts.get(MainApp.selectedText).text.length()) {				
					MainApp.selectedIndex++;
				}
				else if(MainApp.selectedIndex == MainApp.texts.get(MainApp.selectedText).text.length() && MainApp.selectedText + 1 < MainApp.texts.size()) {
					MainApp.selectedIndex = 0;
					MainApp.selectedText++;
				}
			}
			else {
				int lowestIndex = -1;
				int lowestDistance = Integer.MAX_VALUE;
				
				for(int i = 0; i < MainApp.texts.get(MainApp.selectedText).lastSpaces.size(); i++) {
					if(Math.abs(MainApp.texts.get(MainApp.selectedText).lastSpaces.get(i) - MainApp.selectedIndex) < lowestDistance &&
							MainApp.texts.get(MainApp.selectedText).lastSpaces.get(i) > MainApp.selectedIndex) {
						lowestDistance = MainApp.texts.get(MainApp.selectedText).lastSpaces.get(i);
						lowestIndex = i;
					}
				}
				
				MainApp.selectedIndex = lowestIndex == -1 ? MainApp.texts.get(MainApp.selectedText).text.length() : MainApp.texts.get(MainApp.selectedText).lastSpaces.get(lowestIndex);
			
				if(EndofLine) {
					if(MainApp.selectedText < MainApp.texts.size() - 1 && MainApp.selectedIndex == MainApp.texts.get(MainApp.selectedText).text.length()) {
						MainApp.selectedText++;
						MainApp.selectedIndex = 0;
						EndofLine = false;
					}
					else if(MainApp.selectedIndex != MainApp.texts.get(MainApp.selectedText).text.length()) {
						EndofLine = false;
					}
				}
				
				if(MainApp.selectedIndex != MainApp.texts.get(MainApp.selectedText).text.length()) {
					EndofLine = false;
				}
				
				if(MainApp.selectedIndex == MainApp.texts.get(MainApp.selectedText).text.length()) {
					EndofLine = true;
				}
			}
		}
		
		if(e.getKeyCode() == KeyEvent.VK_LEFT) {
			if(!controlHeld) {				
				if(MainApp.selectedIndex - 1 >= 0) {				
					MainApp.selectedIndex--;
				}
				else if(MainApp.selectedIndex == 0 && MainApp.selectedText != 0) {
					MainApp.selectedText--;
					MainApp.selectedIndex = MainApp.texts.get(MainApp.selectedText).text.length();
				}
			}
			else {
				int lowestIndex = -1;
				int lowestDistance = Integer.MAX_VALUE;
				
				for(int i = 0; i < MainApp.texts.get(MainApp.selectedText).lastSpaces.size(); i++) {
					if(Math.abs(MainApp.texts.get(MainApp.selectedText).lastSpaces.get(i) - MainApp.selectedIndex) < lowestDistance &&
							MainApp.texts.get(MainApp.selectedText).lastSpaces.get(i) < MainApp.selectedIndex) {
						lowestDistance = Math.abs(MainApp.texts.get(MainApp.selectedText).lastSpaces.get(i) - MainApp.selectedIndex);
						lowestIndex = i;
					}
				}
				
				MainApp.selectedIndex = lowestIndex == -1 ? 0 : MainApp.texts.get(MainApp.selectedText).lastSpaces.get(lowestIndex);
				
				if(StartofLine) {
					if(MainApp.selectedText != 0 && MainApp.selectedIndex == 0) {
						MainApp.selectedText--;
						MainApp.selectedIndex = MainApp.texts.get(MainApp.selectedText).text.length();
						StartofLine = false;
						turns2 = 0;
					}
					else if(MainApp.selectedIndex != 0) {
						StartofLine = false;
						turns2 = 0;
					}
				}
				
				if(MainApp.selectedIndex != 0) {
					StartofLine = false;
				}
				
				if(MainApp.selectedIndex == 0) {
					StartofLine = true;
				}
			}
		}
		
		if(e.getKeyCode() == KeyEvent.VK_UP) {
			if(!controlHeld) {
				MainApp.selectedText = Math.max(0, MainApp.selectedText - 1);
				MainApp.selectedIndex = Math.min(MainApp.selectedIndex, MainApp.texts.get(MainApp.selectedText).text.length());
			}
			else {
				MainApp.selectedText = Math.max(0, MainApp.lastEmptyText());
				MainApp.selectedIndex = Math.min(MainApp.selectedIndex, MainApp.texts.get(MainApp.selectedText).text.length());
			}
		}
		
		if(e.getKeyCode() == KeyEvent.VK_DOWN) {
			if(!controlHeld) {				
				MainApp.selectedText = Math.min(MainApp.selectedText + 1, MainApp.texts.size() - 1);
				MainApp.selectedIndex = Math.min(MainApp.selectedIndex, MainApp.texts.get(MainApp.selectedText).text.length());
			}
			else {
				MainApp.selectedText = MainApp.nextEmptyText() == -1 ? MainApp.texts.size() - 1 : MainApp.nextEmptyText();
				MainApp.selectedIndex = Math.min(MainApp.selectedIndex, MainApp.texts.get(MainApp.selectedText).text.length());
			}
		}
	}
	
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_CONTROL) {
			controlHeld = false;
		}

		if(e.getKeyCode() == KeyEvent.VK_SHIFT) {
			shiftHeld = false;
			shiftEndLine = MainApp.selectedText;
			shiftEndIndex = MainApp.selectedIndex;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_ALT) {
			altHeld = false;
		}
	}
}
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Inputs extends KeyAdapter {
	
	private String TAB = "    ";
	private boolean shortcut = false;

	public void keyPressed(KeyEvent e) {
		shortcut = false;
		
		shortcuts(e);

		if (!shortcut) {
			if (!e.isControlDown()) {
				type(e);				
			}
			
			if (!e.isShiftDown()) {				
				move(e);
				delete(e);
			}
		}
	}
	
	private void shortcuts(KeyEvent e) {
		for(int i = 0; i < Settings.commands.length; i++) {
			ArrayList<String> shortcut = new ArrayList<String>();			
			boolean hasAll = true;
			
			for(int j = 0; j < Settings.shortcuts[i].size(); j++) {
				shortcut.add(Settings.shortcuts[i].get(j));
			}
			
			if((shortcut.contains("CTRL") && !e.isControlDown()) || (!shortcut.contains("CTRL") && e.isControlDown())) {
				hasAll = false;
			}
			else {
				if(shortcut.contains("CTRL")) shortcut.remove(shortcut.indexOf("CTRL"));
			}
			
			if((shortcut.contains("SHIFT") && !e.isShiftDown()) || (!shortcut.contains("SHIFT") && e.isShiftDown())) {
				hasAll = false;
			}
			else {
				if(shortcut.contains("SHIFT")) shortcut.remove(shortcut.indexOf("SHIFT"));
			}
			
			for(int j = 0; j < shortcut.size(); j++) {
				if(Settings.convert.contains(shortcut.get(j))) {
					if(e.getKeyCode() != Settings.converter.get(shortcut.get(j))) {
						hasAll = false;
						break;
					}
				}
				else {
					hasAll= false;
					break;
				}
			}
			
			if(hasAll) executeShortcut(Settings.commands[i]);
		}
	}
	
	private void executeShortcut(String command) {
		if(command.equals("Switch To Right Window") && Main.activeWindow.state == ProgramState.Editor) {
			Main.switchToRightWindow();
			shortcut = true;
		}
		
		if(command.equals("Switch To Left Window") && Main.activeWindow.state == ProgramState.Editor) {
			Main.switchToLeftWindow();
			shortcut = true;
		}
		
		if(command.equals("Delete Line") && Main.activeWindow.state == ProgramState.Editor) {
			deleteLine();
			shortcut = true;
		}
		
		if(command.equals("Change Highlight Start Position") && Main.activeWindow.state == ProgramState.Editor) {
			Main.activeWindow.highlightSelectedIndex = Main.activeWindow.selectedIndex;
			Main.activeWindow.highlightSelectedText = Main.activeWindow.selectedText;
			shortcut = true;
		}
		
		if(command.equals("New File") && Main.activeWindow.state == ProgramState.Editor) {
			Main.activeWindow.state = ProgramState.NewFile;
			shortcut = true;
		}
		
		if(command.equals("Save File") && Main.activeWindow.state == ProgramState.Editor) {
			FileManager.saveFile();
			shortcut = true;
		}
		
		if(command.equals("Return to Editor") && Main.activeWindow.state != ProgramState.Editor) {
			returnToEditor();
			shortcut = true;
		}
		
		if(command.equals("Open File") && Main.activeWindow.state != ProgramState.OpenFile) {
			Main.activeWindow.state = ProgramState.OpenFile;
			Main.fileInfo.newFileString = "";
			Main.fileInfo.selectedIndex = 0;
			shortcut = true;
		}
		
		if(command.equals("Go Back A Folder") && (Main.activeWindow.state == ProgramState.OpenFile || Main.activeWindow.state == ProgramState.NewFile)) {
			int i = Main.fileInfo.path.length() - 2;
			
			for(; i >= 0; i--) {
				if(Main.fileInfo.path.charAt(i) == '/' || Main.fileInfo.path.charAt(i) == '\\') {
					break;
				}
			}
			
			String s = Main.fileInfo.path.substring(0, Math.max(0, i)) + "/";
			if(s.length() < Main.fileInfo.originalPath.length()) s = Main.fileInfo.originalPath;
			Main.fileInfo.path = s;
			shortcut = true;
		}
	}
	
	private void returnToEditor() {
		Main.activeWindow.state = ProgramState.Editor;
		Main.fileInfo.newFileString = "";
		Main.fileInfo.selectedIndex = 0;
	}

	private void deleteLine() {
		if (Main.activeWindow.selectedText > 0) {
			//when we are current not on the first line
			//we remove the current line we're on
			//go to previous line and selected the very last index
			//and move the line inspector so it matches
			Main.activeWindow.content.remove(Main.activeWindow.selectedText);
			Main.activeWindow.selectedText--;
			Main.activeWindow.selectedIndex = Main.activeWindow.content.get(Main.activeWindow.selectedText).content.length();
			Main.activeWindow.selectedLeftIndex = Math.max(0, Main.activeWindow.selectedIndex - Main.MAX_CHARACTERS_PER_LINE + 1);
			Main.activeWindow.needsSave = true;
		} else {
			//when we are currently on the first line
			//we remove the contents of the current line
			//change the selected to 0 and left index = 0
			Main.activeWindow.content.get(Main.activeWindow.selectedText).content = "";
			Main.activeWindow.selectedIndex = 0;
			Main.activeWindow.selectedLeftIndex = 0;
			Main.activeWindow.needsSave = true;
		}
	}

	private void type(KeyEvent e) {
		if(Main.activeWindow.state == ProgramState.Editor) {			
			if (typable(e)) {
				//next 3 lines are straight-forward, you basically just add in the character entered right between the text before the index and right after the index
				String content = Main.activeWindow.content.get(Main.activeWindow.selectedText).content.substring(0, Main.activeWindow.selectedIndex);
				Main.activeWindow.content.get(Main.activeWindow.selectedText).content = content + e.getKeyChar() + Main.activeWindow.content.get(Main.activeWindow.selectedText).content.substring(Main.activeWindow.selectedIndex);
				Main.activeWindow.selectedIndex++;
				Main.activeWindow.needsSave = true;
				//once the index is over the end of line, just increment the left index so the line inspector moves as well
				// that 2nd line after this line is what stops the text from acting stupid and not move the inspector when you delete something from the selected line
				if ((Main.activeWindow.selectedIndex > Main.MAX_CHARACTERS_PER_LINE - 1)) Main.activeWindow.selectedLeftIndex++;
				if (((Main.activeWindow.selectedIndex < Main.activeWindow.content.get(Main.activeWindow.selectedText).content.length())) && Main.activeWindow.cursorIndex < Main.MAX_CHARACTERS_PER_LINE - 1) Main.activeWindow.selectedLeftIndex--;
			} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				createNewLine();
				Main.activeWindow.needsSave = true;
			} 
			else if(e.getKeyCode() == KeyEvent.VK_TAB) {
				//next 3 lines are straight-forward, you basically just add in the TAB characters entered right between the text
				String content = Main.activeWindow.content.get(Main.activeWindow.selectedText).content.substring(0, Main.activeWindow.selectedIndex);
				Main.activeWindow.content.get(Main.activeWindow.selectedText).content = content + TAB + Main.activeWindow.content.get(Main.activeWindow.selectedText).content.substring(Main.activeWindow.selectedIndex);
				Main.activeWindow.selectedIndex += TAB.length();
				
				//no idea why this work but it does so I'll keep it
				if ((Main.activeWindow.selectedIndex > Main.MAX_CHARACTERS_PER_LINE - 1)) Main.activeWindow.selectedLeftIndex += Math.max(0, Main.activeWindow.selectedIndex - (Main.MAX_CHARACTERS_PER_LINE - 1));
				if (((Main.activeWindow.selectedIndex < Main.activeWindow.content.get(Main.activeWindow.selectedText).content.length())) && Main.activeWindow.cursorIndex < Main.MAX_CHARACTERS_PER_LINE - 1) Main.activeWindow.selectedLeftIndex -= Math.max(0, Main.activeWindow.selectedIndex - (Main.MAX_CHARACTERS_PER_LINE - 1));
				Main.activeWindow.needsSave = true;
			}
		}
		else if(Main.activeWindow.state == ProgramState.NewFile || Main.activeWindow.state == ProgramState.OpenFile) {
			boolean newFile = Main.activeWindow.state == ProgramState.NewFile;
			
			if(typable(e) && (newFile ? Main.fileInfo.newFileString.length() : Main.fileInfo.openFileString.length()) < 50) {
				String content = newFile ? Main.fileInfo.newFileString.substring(0, Main.fileInfo.selectedIndex) : Main.fileInfo.openFileString.substring(0, Main.fileInfo.selectedIndex);
				if(newFile) Main.fileInfo.newFileString = content + e.getKeyChar() + Main.fileInfo.newFileString.substring(Main.fileInfo.selectedIndex);
				else Main.fileInfo.openFileString = content + e.getKeyChar() + Main.fileInfo.openFileString.substring(Main.fileInfo.selectedIndex);
				Main.fileInfo.selectedIndex++;
			}
			else if(e.getKeyCode() == KeyEvent.VK_ENTER) {
				if(newFile) {					
					int status = FileManager.createFile(Main.fileInfo.path);
					
					if(status == 1) {					
						returnToEditor();
					}
				}
				else {
					FileManager.loadFile(Main.fileInfo.path);
				}
			}
		}
	}
	
	private void createNewLine() {
		//first 2 lines make sure that we cut off the current line where we pressed enter so the content after that index is saved
		//we then create a new line and past that content in and increment the values we need to increment
		String content = Main.activeWindow.content.get(Main.activeWindow.selectedText).content.substring(Main.activeWindow.selectedIndex);
		Main.activeWindow.content.get(Main.activeWindow.selectedText).content = Main.activeWindow.content.get(Main.activeWindow.selectedText).content.substring(0, Main.activeWindow.selectedIndex);
		Main.activeWindow.content.add(Main.activeWindow.selectedText + 1, new Text());
		Main.activeWindow.content.get(Main.activeWindow.selectedText + 1).content = content;
		Main.activeWindow.selectedText++;
		Main.activeWindow.selectedIndex = 0;
		Main.activeWindow.selectedLeftIndex = 0;
	}
	
	private void move(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			moveRight(e);
		} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			moveLeft(e);
		} else if (e.getKeyCode() == KeyEvent.VK_UP) {
			moveUp(e);
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			moveDown(e);
		}
	}

	private void delete(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
			if (!e.isControlDown()) {
				deleteCharacter();
			} else {
				deleteWord();
			}
		}
	}

	private int nextSpace(String direction) {
		if (direction.equals("right")) {
			if(Main.activeWindow.state == ProgramState.Editor) {				
				if (Main.activeWindow.selectedIndex < Main.activeWindow.content.get(Main.activeWindow.selectedText).content.length()) {
					int i = Main.activeWindow.selectedIndex + 1;
					
					for(; i < Main.activeWindow.content.get(Main.activeWindow.selectedText).content.length() - 1; i++) {
						if(Main.activeWindow.content.get(Main.activeWindow.selectedText).content.charAt(i) == ' ' && 
								Main.activeWindow.content.get(Main.activeWindow.selectedText).content.charAt(i - 1) != ' ') {
							return i;
						}
					}
					
					return Main.activeWindow.content.get(Main.activeWindow.selectedText).content.length();
				} else {
					if (Main.activeWindow.selectedText < Main.activeWindow.content.size() - 1) {
						Main.activeWindow.selectedText++;
						return 0;
					} else {
						return Main.activeWindow.content.get(Main.activeWindow.selectedText).content.length();
					}
				}
			}
			else if(Main.activeWindow.state == ProgramState.NewFile || Main.activeWindow.state == ProgramState.OpenFile) {
				boolean newFile = Main.activeWindow.state == ProgramState.NewFile;
				int i = Main.fileInfo.selectedIndex + 1;
				
				for(; i < (newFile ? Main.fileInfo.newFileString.length() - 1 : Main.fileInfo.openFileString.length() - 1); i++) {
					if(newFile) {						
						if((Main.fileInfo.newFileString.charAt(i) == ' ' && 
								Main.fileInfo.newFileString.charAt(i - 1) != ' ') || 
								Character.isUpperCase(Main.fileInfo.newFileString.charAt(i))) {
							return i;
						}
					}
					else {
						if((Main.fileInfo.openFileString.charAt(i) == ' ' && 
								Main.fileInfo.openFileString.charAt(i - 1) != ' ') || 
								Character.isUpperCase(Main.fileInfo.openFileString.charAt(i))) {
							return i;
						}
					}
				}
				
				return (newFile ? Main.fileInfo.newFileString.length(): Main.fileInfo.openFileString.length());
			}
		} else if (direction.equals("left") || direction.equals("leftdel")) {
			if(Main.activeWindow.state == ProgramState.Editor) {				
				if (Main.activeWindow.selectedIndex != 0) {
					int i = Math.max(Main.activeWindow.selectedIndex - 1, 0);
					
					for(; i > 0; i--) {
						if(Main.activeWindow.content.get(Main.activeWindow.selectedText).content.charAt(i) == ' ' && 
								Main.activeWindow.content.get(Main.activeWindow.selectedText).content.charAt(i - 1) != ' ') {
							return i;
						}
					}
					
					return 0;
				} else {
					if (Main.activeWindow.selectedText > 0 && !direction.equals("leftdel")) {
						Main.activeWindow.selectedText--;
						Main.activeWindow.selectedLeftIndex = Math.max(0, Main.activeWindow.content.get(Main.activeWindow.selectedText).content.length() - Main.MAX_CHARACTERS_PER_LINE + 1);
						return Main.activeWindow.content.get(Main.activeWindow.selectedText).content.length();
					} else {
						return 0;
					}
				}
			}
			else if(Main.activeWindow.state == ProgramState.NewFile || Main.activeWindow.state == ProgramState.OpenFile) {
				boolean newFile = Main.activeWindow.state == ProgramState.NewFile;
				int i = Math.max(Main.fileInfo.selectedIndex - 1, 0);
				
				for(; i > 0; i--) {
					if(newFile) {
						if((Main.fileInfo.newFileString.charAt(i) == ' ' && 
								Main.fileInfo.newFileString.charAt(i - 1) != ' ') || 
								Character.isUpperCase(Main.fileInfo.newFileString.charAt(i))) {
							return i;
						}
					}
					else {
						if((Main.fileInfo.openFileString.charAt(i) == ' ' && 
								Main.fileInfo.openFileString.charAt(i - 1) != ' ') || 
								Character.isUpperCase(Main.fileInfo.openFileString.charAt(i))) {
							return i;
						}
					}
				}
				
				return 0;
			}
		} else if (direction.equals("up")) {
			for (int i = Math.max(Main.activeWindow.selectedText - 1, 0); i >= 0; i--) {
				if (Main.activeWindow.content.get(i).content.length() == 0) {
					Main.activeWindow.selectedIndex = 0;
					return i;
				}
			}

			Main.activeWindow.selectedIndex = Main.activeWindow.content.get(0).content.length();
			return 0;
		} else if (direction.equals("down")) {
			if (Main.activeWindow.selectedText < Main.activeWindow.content.size()) {
				for (int i = Main.activeWindow.selectedText + 1; i < Main.activeWindow.content.size(); i++) {
					if (Main.activeWindow.content.get(i).content.length() == 0) {
						Main.activeWindow.selectedIndex = 0;
						return i;
					}
				}

				Main.activeWindow.selectedIndex = Main.activeWindow.content
						.get(Main.activeWindow.content.size() - 1).content.length();
				return Main.activeWindow.content.size() - 1;
			}
		}

		return -1;
	}
	
	private void deleteCharacter() {
		if(Main.activeWindow.state == ProgramState.Editor) {			
			if (Main.activeWindow.selectedIndex > 0) {
				//if there is stuff to delete, just delete that character and adjust the variables
				String content = Main.activeWindow.content.get(Main.activeWindow.selectedText).content.substring(Main.activeWindow.selectedIndex);
				Main.activeWindow.content.get(Main.activeWindow.selectedText).content = Main.activeWindow.content.get(Main.activeWindow.selectedText).content.substring(0, Main.activeWindow.selectedIndex - 1) + content;
				Main.activeWindow.selectedIndex--;
				if(Main.activeWindow.selectedLeftIndex > 0) Main.activeWindow.selectedLeftIndex--;		
			} else {
				if (Main.activeWindow.selectedText > 0) {
					//if there isn't anything to delete, remove the current line and move onto the previous line and just stay at the end of line
					Main.activeWindow.content.remove(Main.activeWindow.selectedText);
					Main.activeWindow.selectedText--;
					Main.activeWindow.selectedIndex = Main.activeWindow.content.get(Main.activeWindow.selectedText).content.length();
					Main.activeWindow.selectedLeftIndex = Math.max(0, Main.activeWindow.content.get(Main.activeWindow.selectedText).content.length() - Main.MAX_CHARACTERS_PER_LINE + 1);
				}
			}
			
			Main.activeWindow.needsSave = true;
		}
		else if(Main.activeWindow.state == ProgramState.NewFile || Main.activeWindow.state == ProgramState.OpenFile) {
			boolean newFile = Main.activeWindow.state == ProgramState.NewFile;
			if(Main.fileInfo.selectedIndex > 0) {
				if(newFile) Main.fileInfo.newFileString = Main.fileInfo.newFileString.substring(0, Main.fileInfo.selectedIndex - 1) + Main.fileInfo.newFileString.substring(Main.fileInfo.selectedIndex);
				else Main.fileInfo.openFileString = Main.fileInfo.openFileString.substring(0, Main.fileInfo.selectedIndex - 1) + Main.fileInfo.openFileString.substring(Main.fileInfo.selectedIndex);
				Main.fileInfo.selectedIndex--;
			}
		}
	}
	
	private void deleteWord() {
		if(Main.activeWindow.state == ProgramState.Editor) {			
			//nextLeft first grabs what the next space on the left is
			int nextLeft = nextSpace("leftdel");
			int textIndex = Main.activeWindow.selectedText;
			
			if (Main.activeWindow.selectedIndex == 0 && Main.activeWindow.selectedText > 0) {
				//is this is the beginning of the line
				//goes through all lines previous for the next line with text on it
				int i = Main.activeWindow.selectedText - 1;
				
				for(; i >= 0; i--) {
					if(Main.activeWindow.content.get(i).content.length() != 0) {
						break;
					}
				}
				
				//when found, grabs the next left index and removes the last word of that line
				//pastes all the words from the original line onto the end because that shouldn't just poof out of existence
				//and removes all lines between the lines so it doesn't look weird
				Main.activeWindow.selectedText = i;
				Main.activeWindow.selectedIndex = Main.activeWindow.content.get(Main.activeWindow.selectedText).content.length();
				nextLeft = nextSpace("left");
				Main.activeWindow.content.get(Main.activeWindow.selectedText).content = Main.activeWindow.content.get(Main.activeWindow.selectedText).content.substring(0, nextLeft) + Main.activeWindow.content.get(textIndex).content.substring(0);
				Main.activeWindow.selectedIndex = nextLeft;
				
				if(i + 1 != textIndex) {						
					for(int j = i + 1; j < textIndex; j++) {
						if(j < Main.activeWindow.content.size()) {
							Main.activeWindow.content.remove(j);
							j--;							
						}
					}
				}
				else {
					Main.activeWindow.content.remove(textIndex);
				}
				
				Main.activeWindow.selectedLeftIndex = Math.max(0, Main.activeWindow.content.get(Main.activeWindow.selectedText).content.length() - Main.MAX_CHARACTERS_PER_LINE + 1);
			} else {
				if (Main.activeWindow.selectedIndex > 0) {
					//if are words remaining behind, it just removes it and changes the index
					Main.activeWindow.content.get(Main.activeWindow.selectedText).content = Main.activeWindow.content.get(Main.activeWindow.selectedText).content.substring(0, nextLeft) + 
							Main.activeWindow.content.get(Main.activeWindow.selectedText).content.substring(Main.activeWindow.selectedIndex);
					Main.activeWindow.selectedIndex = nextLeft;
					if (Main.activeWindow.selectedIndex < Main.activeWindow.selectedLeftIndex) Main.activeWindow.selectedLeftIndex = Math.max(0, Main.activeWindow.selectedIndex - Main.MAX_CHARACTERS_PER_LINE + 1);
				} else {
					//if no more words left, just makes content into nothing
					Main.activeWindow.content.get(Main.activeWindow.selectedText).content = "";
					Main.activeWindow.selectedIndex = 0;
					Main.activeWindow.selectedLeftIndex = 0;
				}
			}
			
			Main.activeWindow.needsSave = true;
		}
		else if(Main.activeWindow.state == ProgramState.NewFile|| Main.activeWindow.state == ProgramState.OpenFile) {
			boolean newFile = Main.activeWindow.state == ProgramState.NewFile;
			int nextLeft = nextSpace("left");
			if(newFile) Main.fileInfo.newFileString = Main.fileInfo.newFileString.substring(0, nextLeft) + Main.fileInfo.newFileString.substring(Main.fileInfo.selectedIndex);
			else Main.fileInfo.openFileString = Main.fileInfo.openFileString.substring(0, nextLeft) + Main.fileInfo.openFileString.substring(Main.fileInfo.selectedIndex);
			Main.fileInfo.selectedIndex = nextLeft;
		}
	}
	
	private void moveRight(KeyEvent e) {
		if(Main.activeWindow.state == ProgramState.Editor) {			
			if (!e.isControlDown()) {
				//everything here is done by character
				if (Main.activeWindow.selectedIndex < Main.activeWindow.content.get(Main.activeWindow.selectedText).content.length()) {
					//if the current index is less than the length of the whole line, it just increments the index so that it moves along
					Main.activeWindow.selectedIndex++;
					if (Main.activeWindow.selectedIndex > Main.activeWindow.selectedLeftIndex + Main.MAX_CHARACTERS_PER_LINE - 1) Main.activeWindow.selectedLeftIndex++;
				} else {
					//if the current index is at the end of the line, it'll check if there is another line afterwards
					//if there is a line, it'll adjust all values to fit the increment
					if (Main.activeWindow.selectedText < Main.activeWindow.content.size() - 1) {
						Main.activeWindow.selectedIndex = 0;
						Main.activeWindow.selectedText++;
						Main.activeWindow.selectedLeftIndex = 0;
					}
				}
			} else {
				//everything here is done by word
				//nextRight will tell us where the next space is to teleport to
				//indexes are adjusted
				int nextRight = nextSpace("right");
				Main.activeWindow.selectedIndex = nextRight;
				Main.activeWindow.selectedLeftIndex = Math.max(0, Main.activeWindow.selectedIndex - Main.MAX_CHARACTERS_PER_LINE + 1);
			}
		}
		else if(Main.activeWindow.state == ProgramState.NewFile || Main.activeWindow.state == ProgramState.OpenFile) {
			boolean newFile = Main.activeWindow.state == ProgramState.NewFile;
			if(!e.isControlDown()) {				
				if(Main.fileInfo.selectedIndex < (newFile ? Main.fileInfo.newFileString.length() : Main.fileInfo.openFileString.length())) {
					Main.fileInfo.selectedIndex++;
				}
			}
			else {
				int nextRight = nextSpace("right");
				Main.fileInfo.selectedIndex = nextRight;
			}
		}
	}
	
	private void moveLeft(KeyEvent e) {
		if(Main.activeWindow.state == ProgramState.Editor) {			
			if (!e.isControlDown()) {
				//everything here is done by character
				if (Main.activeWindow.selectedIndex > 0) {
					//if there are more characters remaining behind the current index, just move down the string
					//adjust all necessary values as well
					Main.activeWindow.selectedIndex--;
					if (Main.activeWindow.selectedIndex < Main.activeWindow.selectedLeftIndex) Main.activeWindow.selectedLeftIndex = Math.max(0, Main.activeWindow.selectedIndex - Main.MAX_CHARACTERS_PER_LINE + 1);
				} else {
					//if the index is at the beginning of the line, it will look for a line before the current one
					//if such line exists, it will move to the end of the previous line and adjust all variables as necessary
					if (Main.activeWindow.selectedText > 0) {
						Main.activeWindow.selectedText--;
						Main.activeWindow.selectedIndex = Main.activeWindow.content.get(Main.activeWindow.selectedText).content.length();
						Main.activeWindow.selectedLeftIndex = Math.max(0, Main.activeWindow.selectedIndex - Main.MAX_CHARACTERS_PER_LINE + 1);
					}
				}
			} else {
				//everything here is done by word
				//nextRight will tell us where the next space is to teleport to
				//indexes are adjusted
				int nextLeft = nextSpace("left");
				Main.activeWindow.selectedIndex = nextLeft;
				if (Main.activeWindow.selectedIndex < Main.activeWindow.selectedLeftIndex) Main.activeWindow.selectedLeftIndex = Math.max(0, Main.activeWindow.selectedIndex - Main.MAX_CHARACTERS_PER_LINE + 1);
			}
		}
		else if(Main.activeWindow.state == ProgramState.NewFile || Main.activeWindow.state == ProgramState.OpenFile) {
			if(!e.isControlDown()) {				
				if(Main.fileInfo.selectedIndex > 0) {
					Main.fileInfo.selectedIndex--;
				}
			}
			else {
				int nextLeft = nextSpace("left");
				Main.fileInfo.selectedIndex = nextLeft;
			}
		}
	}
	
	private void moveUp(KeyEvent e) {
		if(Main.activeWindow.state == ProgramState.Editor) {			
			if (!e.isControlDown()) {
				if (Main.activeWindow.selectedText > 0) {
					//if there is a line before the currently selected one
					//adjust all variables so it moves to that line
					Main.activeWindow.selectedText--;
					Main.activeWindow.selectedIndex = Math.min(Main.activeWindow.selectedIndex, Main.activeWindow.content.get(Main.activeWindow.selectedText).content.length());
					Main.activeWindow.selectedLeftIndex = Math.max(0, Main.activeWindow.selectedIndex - Main.MAX_CHARACTERS_PER_LINE + 1);
					
					if(Main.activeWindow.selectedText < Main.activeWindow.selectedTopText) {
						Main.activeWindow.selectedTopText--;
					}
				}
			} else {
				//looks for the next gap between paragraphs
				//adjusts variables so it moves to that gap
				int nextUp = nextSpace("up");
				Main.activeWindow.selectedText = nextUp;
				Main.activeWindow.selectedLeftIndex = 0;
				
				if(nextUp < Main.activeWindow.selectedTopText) {
					Main.activeWindow.selectedTopText = Math.max(0,  Main.activeWindow.selectedText - Main.MAX_LINES - 1);
				}
			}
		}
		else if(Main.activeWindow.state == ProgramState.NewFile || Main.activeWindow.state == ProgramState.OpenFile) {
			if(Main.activeWindow.fileSelectIndex > 0) {
				Main.activeWindow.fileSelectIndex--;
			}
		}
	}
	
	private void moveDown(KeyEvent e) {
		if(Main.activeWindow.state == ProgramState.Editor) {			
			if (!e.isControlDown()) {
				if (Main.activeWindow.selectedText < Main.activeWindow.content.size() - 1) {
					//if there is a line after the currently selected one
					//adjust all variables so it moves to that line
					Main.activeWindow.selectedText++;
					Main.activeWindow.selectedIndex = Math.min(Main.activeWindow.selectedIndex, Main.activeWindow.content.get(Main.activeWindow.selectedText).content.length());
					Main.activeWindow.selectedLeftIndex = Math.max(0, Main.activeWindow.selectedIndex - Main.MAX_CHARACTERS_PER_LINE + 1);
				}
			} else {
				//looks for the next gap between paragraphs
				//adjusts variables so it moves to that gap
				int nextDown = nextSpace("down");
				Main.activeWindow.selectedText = nextDown;
				Main.activeWindow.selectedLeftIndex = 0;
				
				if(nextDown > Main.activeWindow.selectedTopText + Main.MAX_LINES - 1) {
					Main.activeWindow.selectedTopText = Main.activeWindow.content.size() - Main.MAX_LINES - 1;
				}
			}
		}
		else if(Main.activeWindow.state == ProgramState.NewFile || Main.activeWindow.state == ProgramState.OpenFile){
			if(Main.activeWindow.fileSelectIndex < Main.activeWindow.files.size() - 1) {
				Main.activeWindow.fileSelectIndex++;
			}
		}
	}
	
	private boolean typable(KeyEvent e) {
		return (e.getKeyCode() >= 44 && e.getKeyCode() <= 57) || (e.getKeyCode() >= 65 && e.getKeyCode() <= 93)
				|| (e.getKeyCode() == 59) || (e.getKeyCode() == 32) || (e.getKeyCode() == 61) || (e.getKeyCode() == 151)
				|| (e.getKeyCode() == 152) || (e.getKeyCode() == 153) || (e.getKeyCode() == 222) || (e.getKeyCode() == 32);
	}
}

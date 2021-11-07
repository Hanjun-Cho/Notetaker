import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Inputs extends KeyAdapter {
	
	private boolean controlDown = false;
	private boolean shiftDown = false;
	private boolean shortcut = false;

	public void keyPressed(KeyEvent e) {
		shortcut = false;
		
		if (e.getKeyCode() == KeyEvent.VK_CONTROL && !controlDown) {			
			controlDown = true;
		}
		
		if (e.getKeyCode() == KeyEvent.VK_SHIFT && !shiftDown) {
			shiftDown = true;
		}
		
		shortcuts(e);

		if (!shortcut) {
			if (!controlDown) {
				type(e);				
			}
			
			if (!shiftDown) {				
				move(e);
				delete(e);
			}
		}
	}

	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_CONTROL) {			
			controlDown = false;
		}
	
		if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
			shiftDown = false;
		}
	}

	private void shortcuts(KeyEvent e) {
		if (controlDown && shiftDown && e.getKeyCode() == KeyEvent.VK_RIGHT) {
			Main.switchToRightWindow();
			shortcut = true;
		}
		if (controlDown && shiftDown && e.getKeyCode() == KeyEvent.VK_LEFT) {
			Main.switchToLeftWindow();
			shortcut = true;
		}
		if (controlDown && shiftDown && e.getKeyCode() == KeyEvent.VK_D) {
			deleteLine();
			shortcut = true;
		}
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
		} else {
			//when we are currently on the first line
			//we remove the contents of the current line
			//change the selected to 0 and left index = 0
			Main.activeWindow.content.get(Main.activeWindow.selectedText).content = "";
			Main.activeWindow.selectedIndex = 0;
			Main.activeWindow.selectedLeftIndex = 0;
		}
	}

	private void type(KeyEvent e) {
		if (typable(e)) {
			//next 3 lines are straight-forward, you basically just add in the character entered right between the text before the index and right after the index
			String content = Main.activeWindow.content.get(Main.activeWindow.selectedText).content.substring(0, Main.activeWindow.selectedIndex);
			Main.activeWindow.content.get(Main.activeWindow.selectedText).content = content + e.getKeyChar() + Main.activeWindow.content.get(Main.activeWindow.selectedText).content.substring(Main.activeWindow.selectedIndex);
			Main.activeWindow.selectedIndex++;

			//once the index is over the end of line, just increment the left index so the line inspector moves as well
			// that 2nd line after this line is what stops the text from acting stupid and not move the inspector when you delete something from the selected line
			if ((Main.activeWindow.selectedIndex > Main.MAX_CHARACTERS_PER_LINE - 1)) Main.activeWindow.selectedLeftIndex++;
			if (((Main.activeWindow.selectedIndex < Main.activeWindow.content.get(Main.activeWindow.selectedText).content.length())) && Main.activeWindow.cursorIndex < Main.MAX_CHARACTERS_PER_LINE - 1) Main.activeWindow.selectedLeftIndex--;
		} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			createNewLine();
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
			moveRight();
		} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			moveLeft();
		} else if (e.getKeyCode() == KeyEvent.VK_UP) {
			moveUp();
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			moveDown();
		}
	}

	private void delete(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
			if (!controlDown) {
				deleteCharacter();
			} else {
				deleteWord();
			}
		}
	}

	private int nextSpace(String direction) {
		if (direction.equals("right")) {
			if (Main.activeWindow.selectedIndex < Main.activeWindow.content.get(Main.activeWindow.selectedText).content.length()) {
				for (int i = Main.activeWindow.selectedIndex + 1; i < Main.activeWindow.content.get(Main.activeWindow.selectedText).content.length(); i++) {
					if (Main.activeWindow.content.get(Main.activeWindow.selectedText).content.charAt(i) == ' ') {
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
		} else if (direction.equals("left") || direction.equals("leftdel")) {
			if (Main.activeWindow.selectedIndex != 0) {
				for (int i = Math.max(Main.activeWindow.selectedIndex - 1, 0); i >= 0; i--) {
					if (Main.activeWindow.content.get(Main.activeWindow.selectedText).content.charAt(i) == ' ') {
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
	}
	
	private void deleteWord() {
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
	}
	
	private void moveRight() {
		if (!controlDown) {
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
	
	private void moveLeft() {
		if (!controlDown) {
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
	
	private void moveUp() {
		if (!controlDown) {
			if (Main.activeWindow.selectedText > 0) {
				//if there is a line before the currently selected one
				//adjust all variables so it moves to that line
				Main.activeWindow.selectedText--;
				Main.activeWindow.selectedIndex = Math.min(Main.activeWindow.selectedIndex, Main.activeWindow.content.get(Main.activeWindow.selectedText).content.length());
				Main.activeWindow.selectedLeftIndex = Math.max(0, Main.activeWindow.selectedIndex - Main.MAX_CHARACTERS_PER_LINE + 1);
			}
		} else {
			//looks for the next gap between paragraphs
			//adjusts variables so it moves to that gap
			int nextUp = nextSpace("up");
			Main.activeWindow.selectedText = nextUp;
			Main.activeWindow.selectedLeftIndex = 0;
		}
	}
	
	private void moveDown() {
		if (!controlDown) {
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
		}
	}
	
	private boolean typable(KeyEvent e) {
		return (e.getKeyCode() >= 44 && e.getKeyCode() <= 57) || (e.getKeyCode() >= 65 && e.getKeyCode() <= 93)
				|| (e.getKeyCode() == 59) || (e.getKeyCode() == 32) || (e.getKeyCode() == 61) || (e.getKeyCode() == 151)
				|| (e.getKeyCode() == 152) || (e.getKeyCode() == 153) || (e.getKeyCode() == 222) || (e.getKeyCode() == 32);
	}
}

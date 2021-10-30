import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Inputs extends KeyAdapter {

	boolean controlDown = false;
	boolean shiftDown = false;
	
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_CONTROL) controlDown = true;
		if(e.getKeyCode() == KeyEvent.VK_SHIFT) shiftDown = true;
		shortcuts(e);
		if(!controlDown) type(e);
		if(!shiftDown) move(e);
		if(!shiftDown) delete(e);
	}
	
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_CONTROL) controlDown = false;
		if(e.getKeyCode() == KeyEvent.VK_SHIFT) shiftDown = false;
	}
	
	private void shortcuts(KeyEvent e) {
		if(controlDown && shiftDown && e.getKeyCode() == KeyEvent.VK_RIGHT) Main.switchToRightWindow();
		if(controlDown && shiftDown && e.getKeyCode() == KeyEvent.VK_LEFT) Main.switchToLeftWindow();
		if(controlDown && shiftDown && e.getKeyCode() == KeyEvent.VK_D) deleteLine();
		if(controlDown && shiftDown && e.getKeyChar() == KeyEvent.VK_SPACE) Main.changeHighlightCursorLocation();
	}
	
	private void deleteLine() {
		if(Main.activeWindow.selectedText > 0) {
			Main.activeWindow.content.remove(Main.activeWindow.selectedText);
			Main.activeWindow.selectedText--;
			Main.activeWindow.selectedIndex = Main.activeWindow.content.get(Main.activeWindow.selectedText).content.length();
		}
		else {
			Main.activeWindow.content.get(Main.activeWindow.selectedText).content = "";
			Main.activeWindow.selectedIndex = 0;
		}
	}
	
	private void wrapCheck(int index) {
		while(index < Main.activeWindow.content.size()) {
			if(Main.activeWindow.content.get(index).content.length() >= Main.MAX_CHARACTERS_PER_LINE) {				
				int lastSpace = 0;
				int spaceCount = 0;
				
				for(int i = Math.max(Main.activeWindow.content.get(index).content.length() - 1, 0); i >= 0; i--) {
					if(Main.activeWindow.content.get(index).content.charAt(i) == ' ') {
						spaceCount++;
						lastSpace = i;
					}
				}
				
				if(lastSpace == 0) {
					String wrap = Main.activeWindow.content.get(index).content.substring(Main.activeWindow.content.get(index).content.length() - 1);
					Main.activeWindow.content.get(index).content = Main.activeWindow.content.get(index).content.substring(0, Main.activeWindow.content.get(index).content.length() - 1);
					
					if(index + 1 >= Main.activeWindow.content.size()) {
						Main.activeWindow.content.add(index + 1, new Text());
						Main.activeWindow.content.get(index + 1).content = wrap;
						Main.activeWindow.content.get(index + 1).wrapped = true;
					}
					else {
						if(Main.activeWindow.content.get(index + 1).wrapped) {
							Main.activeWindow.content.get(index + 1).content = wrap + Main.activeWindow.content.get(index + 1).content;
						}
						else {
							Main.activeWindow.content.add(index + 1, new Text());
							Main.activeWindow.content.get(index + 1).content = wrap;
							Main.activeWindow.content.get(index + 1).wrapped = true;
						}
					}
				}
			}
			
			index++;
		}
	}
	
	//(TODO) -> Finished the text wrapping which has a space at index = 0 and nowhere else
	//(TODO) -> Go and finish the text wrapping which uses the word
	private void wrapText() {
		if(Main.activeWindow.content.get(Main.activeWindow.selectedText).content.length() >= Main.MAX_CHARACTERS_PER_LINE) {
			Text text = Main.activeWindow.content.get(Main.activeWindow.selectedText);
			System.out.println("TEST");
			
			int last = nextSpace("left");
			
			if(last == 0) {
				String toWrap = text.content.substring(text.content.length() - 1);
				
				if(Main.activeWindow.selectedIndex >= Main.MAX_CHARACTERS_PER_LINE) {
					Main.activeWindow.selectedIndex = 1;
					Main.activeWindow.selectedText++;
					
					if(Main.activeWindow.selectedText < Main.activeWindow.content.size()) {					
						int index = Main.activeWindow.selectedText + 1;
						wrapCheck(index);
					}
					else {
						Main.activeWindow.content.add(Main.activeWindow.selectedText, new Text());
						Main.activeWindow.content.get(Main.activeWindow.selectedText).content = toWrap;
						Main.activeWindow.content.get(Main.activeWindow.selectedText).wrapped = true;
					}
				}
				else {
					if(Main.activeWindow.selectedText < Main.activeWindow.content.size()) {						
						int index = Main.activeWindow.selectedText + 1;
						
						while(index < Main.activeWindow.content.size()) {
							if(Main.activeWindow.content.get(index).wrapped == false) {
								Main.activeWindow.content.add(index, new Text());
								Main.activeWindow.content.get(index).content = toWrap;
								Main.activeWindow.content.get(index).wrapped = true;
								break;
							}
							
							Main.activeWindow.content.get(index).content = toWrap + Main.activeWindow.content.get(index).content;
							toWrap = Main.activeWindow.content.get(index).content.substring(Main.activeWindow.content.get(index).content.length() - 1);

							if(Main.activeWindow.content.get(index).content.length() < Main.MAX_CHARACTERS_PER_LINE) {
								break;
							}
							
							Main.activeWindow.content.get(index).content = Main.activeWindow.content.get(index).content.substring(0, Main.activeWindow.content.get(index).content.length() - 1);
							index++;
							
							if(index >= Main.activeWindow.content.size()) {
								Main.activeWindow.content.add(index, new Text());
								Main.activeWindow.content.get(index).content = toWrap;
								Main.activeWindow.content.get(index).wrapped = true;
							}
						}
					}
					else {
						Main.activeWindow.content.add(Main.activeWindow.selectedText + 1, new Text());
						Main.activeWindow.content.get(Main.activeWindow.selectedText + 1).content = toWrap;
						Main.activeWindow.content.get(Main.activeWindow.selectedText + 1).wrapped = true;
					}
				}
			
				text.content = text.content.substring(0, text.content.length() - 1);
			}
		}
	}
	
	private void type(KeyEvent e) {
		if((e.getKeyCode() >= 44 && e.getKeyCode() <= 57) ||
				(e.getKeyCode() >= 65 && e.getKeyCode() <= 93) ||
				(e.getKeyCode() == 59) || (e.getKeyCode() == 32) ||
				(e.getKeyCode() == 61) || (e.getKeyCode() == 151) ||
				(e.getKeyCode() == 152) || (e.getKeyCode() == 153) ||
				(e.getKeyCode() == 222) || (e.getKeyCode() == 32)) {
				String content = Main.activeWindow.content.get(Main.activeWindow.selectedText).content.substring(0, Main.activeWindow.selectedIndex);
				Main.activeWindow.content.get(Main.activeWindow.selectedText).content = content + e.getKeyChar() + Main.activeWindow.content.get(Main.activeWindow.selectedText).content.substring(Main.activeWindow.selectedIndex);
				Main.activeWindow.selectedIndex++;
				wrapText();
		}
		else if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			if(Main.activeWindow.highlightText == Main.activeWindow.selectedText && Main.activeWindow.highlightIndex >= Main.activeWindow.selectedIndex) {
				Main.tempCursor.visible = false;
			}

			String content = Main.currentText.content.substring(Main.activeWindow.selectedIndex);
			Main.currentText.content = Main.currentText.content.substring(0, Main.activeWindow.selectedIndex);
			Main.activeWindow.content.add(Main.activeWindow.selectedText + 1, new Text());
			Main.activeWindow.selectedText++;
			Main.activeWindow.content.get(Main.activeWindow.selectedText).content = content;
			Main.activeWindow.selectedIndex = 0;
		}
	}
	
	private void move(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
			if(!controlDown) {				
				if(Main.activeWindow.selectedIndex < Main.currentText.content.length()) {
					Main.activeWindow.selectedIndex++;
				}
				else {
					if(Main.activeWindow.selectedText < Main.activeWindow.content.size() - 1) {
						Main.activeWindow.selectedIndex = 0;
						Main.activeWindow.selectedText++;
					}
				}
			}
			else {
				int nextRight = nextSpace("right");
				Main.activeWindow.selectedIndex = nextRight;
			}
		}
		else if(e.getKeyCode() == KeyEvent.VK_LEFT) {
			if(!controlDown) {					
				if(Main.activeWindow.selectedIndex > 0) {
					Main.activeWindow.selectedIndex--;
				}
				else {
					if(Main.activeWindow.selectedText > 0) {
						Main.activeWindow.selectedText--;
						Main.activeWindow.selectedIndex = Main.activeWindow.content.get(Main.activeWindow.selectedText).content.length();
					}
				}
			}
			else {
				int nextLeft = nextSpace("left");
				Main.activeWindow.selectedIndex = nextLeft;
			}
		}
		else if(e.getKeyCode() == KeyEvent.VK_UP) {
			if(!controlDown) {
				if(Main.activeWindow.selectedText > 0) {
					Main.activeWindow.selectedText--;
					Main.activeWindow.selectedIndex = Math.min(Main.activeWindow.selectedIndex, Main.activeWindow.content.get(Main.activeWindow.selectedText).content.length());
				}
			}
			else {
				int nextUp = nextSpace("up");
				Main.activeWindow.selectedText = nextUp;
			}
		}
		else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
			if(!controlDown) {
				if(Main.activeWindow.selectedText < Main.activeWindow.content.size() - 1) {
					Main.activeWindow.selectedText++;
					Main.activeWindow.selectedIndex = Math.min(Main.activeWindow.selectedIndex, Main.activeWindow.content.get(Main.activeWindow.selectedText).content.length());
				}
			}
			else {
				int nextDown = nextSpace("down");
				Main.activeWindow.selectedText = nextDown;
			}
		}
	}
	
	private void delete(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
			if(!controlDown) {			
				if(Main.activeWindow.selectedIndex > 0) {					
					String content = Main.currentText.content.substring(Main.activeWindow.selectedIndex);
					Main.currentText.content = Main.currentText.content.substring(0, Main.activeWindow.selectedIndex - 1) + content;
					Main.activeWindow.selectedIndex--;
					
					if(Main.activeWindow.selectedText == Main.activeWindow.highlightText && Main.activeWindow.selectedIndex < Main.activeWindow.highlightIndex) {
						Main.tempCursor.visible = false;
					}
				}
				else {
					if(Main.activeWindow.selectedText > 0) {
						if(Main.activeWindow.selectedText == Main.activeWindow.highlightText && Main.activeWindow.selectedIndex == Main.activeWindow.highlightIndex) {
							Main.tempCursor.visible = false;
						}
						
						Main.activeWindow.content.remove(Main.activeWindow.selectedText);
						Main.activeWindow.selectedText--;
						Main.activeWindow.selectedIndex = Main.activeWindow.content.get(Main.activeWindow.selectedText).content.length();
					}
				}
			}
			else {
				int nextLeft = nextSpace("leftdel");
				System.out.println(nextLeft + ", " + Main.activeWindow.selectedIndex + ", " + Main.activeWindow.selectedText);
				int textIndex = Main.activeWindow.selectedText;
				
				if(Main.activeWindow.selectedIndex == 0 && Main.activeWindow.selectedText > 0) {
					Main.activeWindow.selectedText--;
					Main.activeWindow.selectedIndex = Main.activeWindow.content.get(Main.activeWindow.selectedText).content.length();
					nextLeft = nextSpace("left");
					Main.activeWindow.content.get(Main.activeWindow.selectedText).content = Main.activeWindow.content.get(Main.activeWindow.selectedText).content.substring(0, nextLeft);
					Main.activeWindow.selectedIndex = nextLeft;
					
					if(Main.activeWindow.highlightText == Main.activeWindow.selectedText && Main.activeWindow.highlightIndex >= Main.activeWindow.selectedIndex) {
						Main.tempCursor.visible = false;
					}
					
					for(int i = Main.activeWindow.selectedText + 1; i < textIndex; i++) {
						if(i < Main.activeWindow.content.size()) {							
							Main.activeWindow.content.remove(i);
							i--;
						}
					}
				}
				else {					
					if(Main.activeWindow.selectedIndex > 0) {						
						Main.currentText.content = Main.currentText.content.substring(0, nextLeft) + Main.currentText.content.substring(Main.activeWindow.selectedIndex);
						Main.activeWindow.selectedIndex = nextLeft;
					
						if(Main.activeWindow.highlightText == Main.activeWindow.selectedText && Main.activeWindow.highlightIndex >= Main.activeWindow.selectedIndex) {
							Main.tempCursor.visible = false;
						}
					}
					else {
						Main.currentText.content = "";
						Main.activeWindow.selectedIndex = 0;
						
						if(Main.activeWindow.highlightText == Main.activeWindow.selectedText && Main.activeWindow.highlightIndex >= Main.activeWindow.selectedIndex) {
							Main.tempCursor.visible = false;
						}
					}
				}
			}
		}
	}
	
	private int nextSpace(String direction) {
		if(direction.equals("right")) {
			if(Main.activeWindow.selectedIndex < Main.currentText.content.length()) {				
				for(int i = Main.activeWindow.selectedIndex + 1; i < Main.currentText.content.length(); i++) {
					if(Main.activeWindow.content.get(Main.activeWindow.selectedText).content.charAt(i) == ' ') {
						return i;
					}
				}
				
				return Main.currentText.content.length();
			}
			else {
				if(Main.activeWindow.selectedText < Main.activeWindow.content.size() - 1) {					
					Main.activeWindow.selectedText++;
					return 0;
				}
				else {
					return Main.currentText.content.length(); 
				}
			}
		}
		else if(direction.equals("left") || direction.equals("leftdel")) {
			if(Main.activeWindow.selectedIndex != 0) {				
				for(int i = Math.max(Main.activeWindow.selectedIndex - 1, 0); i >= 0; i--) {
					if(Main.activeWindow.content.get(Main.activeWindow.selectedText).content.charAt(i) == ' ') {
						return i;
					}
				}
				
				return 0;
			}
			else {
				if(Main.activeWindow.selectedText > 0 && !direction.equals("leftdel")) {
					Main.activeWindow.selectedText--;
					return Main.activeWindow.content.get(Main.activeWindow.selectedText).content.length();
				}
				else {
					return 0;
				}
			}
		}
		else if(direction.equals("up")) {
			for(int i = Math.max(Main.activeWindow.selectedText - 1, 0); i >= 0; i--) {
				if(Main.activeWindow.content.get(i).content.length() == 0) {
					Main.activeWindow.selectedIndex = 0;
					return i;
				}
			}
			
			Main.activeWindow.selectedIndex = Main.activeWindow.content.get(0).content.length();
			return 0;
		}
		else if(direction.equals("down")) {
			if(Main.activeWindow.selectedText < Main.activeWindow.content.size()) {
				for(int i = Main.activeWindow.selectedText + 1; i < Main.activeWindow.content.size(); i++) {
					if(Main.activeWindow.content.get(i).content.length() == 0) {
						Main.activeWindow.selectedIndex = 0;
						return i;
					}
				}
				
				Main.activeWindow.selectedIndex = Main.activeWindow.content.get(Main.activeWindow.content.size() - 1).content.length();
				return Main.activeWindow.content.size() - 1;
			}
		}
		
		return -1;
	}
}

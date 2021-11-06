import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Inputs extends KeyAdapter {

	boolean controlDown = false;
	boolean shiftDown = false;
	boolean shortcut = false;

	public void keyPressed(KeyEvent e) {
		shortcut = false;
		if (e.getKeyCode() == KeyEvent.VK_CONTROL)
			controlDown = true;
		if (e.getKeyCode() == KeyEvent.VK_SHIFT)
			shiftDown = true;
		shortcuts(e);

		if (!shortcut) {
			if (!controlDown)
				type(e);
			if (!shiftDown)
				move(e);
			if (!shiftDown)
				delete(e);
		}
	}

	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_CONTROL)
			controlDown = false;
		if (e.getKeyCode() == KeyEvent.VK_SHIFT)
			shiftDown = false;
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
		if (controlDown && shiftDown && e.getKeyChar() == KeyEvent.VK_SPACE) {
			Main.changeHighlightCursorLocation();
			shortcut = true;
		}
	}

	private void deleteLine() {
		if (Main.activeWindow.selectedText > 0) {
			Main.activeWindow.content.remove(Main.activeWindow.selectedText);
			Main.activeWindow.selectedText--;
			Main.activeWindow.selectedIndex = Main.activeWindow.content.get(Main.activeWindow.selectedText).content
					.length();
		} else {
			Main.activeWindow.content.get(Main.activeWindow.selectedText).content = "";
			Main.activeWindow.selectedIndex = 0;
		}
	}

	private void type(KeyEvent e) {
		if ((e.getKeyCode() >= 44 && e.getKeyCode() <= 57) || (e.getKeyCode() >= 65 && e.getKeyCode() <= 93)
				|| (e.getKeyCode() == 59) || (e.getKeyCode() == 32) || (e.getKeyCode() == 61) || (e.getKeyCode() == 151)
				|| (e.getKeyCode() == 152) || (e.getKeyCode() == 153) || (e.getKeyCode() == 222)
				|| (e.getKeyCode() == 32)) {
			String content = Main.activeWindow.content.get(Main.activeWindow.selectedText).content.substring(0,
					Main.activeWindow.selectedIndex);
			Main.activeWindow.content.get(Main.activeWindow.selectedText).content = content + e.getKeyChar()
					+ Main.activeWindow.content.get(Main.activeWindow.selectedText).content
							.substring(Main.activeWindow.selectedIndex);
			Main.activeWindow.selectedIndex++;

			if ((Main.activeWindow.selectedIndex > Main.MAX_CHARACTERS_PER_LINE - 1)) {
				Main.activeWindow.selectedLeftIndex++;
			}

			if (((Main.activeWindow.selectedIndex < Main.activeWindow.content.get(Main.activeWindow.selectedText).content.length()))
					&& Main.activeWindow.selectedIndex - Main.activeWindow.selectedLeftIndex < Main.MAX_CHARACTERS_PER_LINE - 1) {
				Main.activeWindow.selectedLeftIndex--;
			}
		} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			if (Main.activeWindow.highlightText == Main.activeWindow.selectedText
					&& Main.activeWindow.highlightIndex >= Main.activeWindow.selectedIndex) {
				Main.tempCursor.visible = false;
			}

			String content = Main.currentText.content.substring(Main.activeWindow.selectedIndex);
			Main.currentText.content = Main.currentText.content.substring(0, Main.activeWindow.selectedIndex);
			Main.activeWindow.content.add(Main.activeWindow.selectedText + 1, new Text());
			Main.activeWindow.content.get(Main.activeWindow.selectedText + 1).content = content;
			Main.activeWindow.selectedText++;
			Main.activeWindow.selectedIndex = 0;

			for (int i = 0; i < Main.activeWindow.content.size() - 1; i++) {
				Main.activeWindow.selectedLeftIndex = 0;
			}
		}
	}

	private void move(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			if (!controlDown) {
				if (Main.activeWindow.selectedIndex < Main.currentText.content.length()) {
					Main.activeWindow.selectedIndex++;

					if (Main.activeWindow.selectedIndex > Main.activeWindow.selectedLeftIndex + Main.MAX_CHARACTERS_PER_LINE - 1) {
						Main.activeWindow.selectedLeftIndex++;
					}
				} else {
					if (Main.activeWindow.selectedText < Main.activeWindow.content.size() - 1) {
						Main.activeWindow.selectedIndex = 0;
						Main.activeWindow.selectedText++;
					}
				}
			} else {
				int nextRight = nextSpace("right");
				Main.activeWindow.selectedIndex = nextRight;

				if (Main.activeWindow.selectedIndex > Main.activeWindow.selectedLeftIndex + Main.MAX_CHARACTERS_PER_LINE - 1) {
					Main.activeWindow.selectedLeftIndex = Main.activeWindow.selectedIndex
									- Main.MAX_CHARACTERS_PER_LINE + 1;
				}
			}
		} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			if (!controlDown) {
				if (Main.activeWindow.selectedIndex > 0) {
					Main.activeWindow.selectedIndex--;

					if (Main.activeWindow.selectedIndex < Main.activeWindow.selectedLeftIndex) {
						Main.activeWindow.selectedLeftIndex = Math.max(0,
								Main.activeWindow.selectedIndex - Main.MAX_CHARACTERS_PER_LINE + 1);
					}
				} else {
					if (Main.activeWindow.selectedText > 0) {
						Main.activeWindow.selectedText--;
						Main.activeWindow.selectedIndex = Main.activeWindow.content
								.get(Main.activeWindow.selectedText).content.length();
					}
				}
			} else {
				int nextLeft = nextSpace("left");
				Main.activeWindow.selectedIndex = nextLeft;

				if (Main.activeWindow.selectedIndex < Main.activeWindow.selectedLeftIndex) {
					Main.activeWindow.selectedLeftIndex = Math.max(0,
							Main.activeWindow.selectedIndex - Main.MAX_CHARACTERS_PER_LINE + 1);
				}
			}
		} else if (e.getKeyCode() == KeyEvent.VK_UP) {
			if (!controlDown) {
				if (Main.activeWindow.selectedText > 0) {
					Main.activeWindow.selectedText--;
					Main.activeWindow.selectedIndex = Math.min(Main.activeWindow.selectedIndex,
							Main.activeWindow.content.get(Main.activeWindow.selectedText).content.length());
					Main.activeWindow.selectedLeftIndex = Math.max(0, Main.activeWindow.selectedIndex - Main.MAX_CHARACTERS_PER_LINE + 1);
				}
			} else {
				int nextUp = nextSpace("up");
				Main.activeWindow.selectedText = nextUp;
				Main.activeWindow.selectedLeftIndex = 0;
			}
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			if (!controlDown) {
				if (Main.activeWindow.selectedText < Main.activeWindow.content.size() - 1) {
					Main.activeWindow.selectedText++;
					Main.activeWindow.selectedIndex = Math.min(Main.activeWindow.selectedIndex,
							Main.activeWindow.content.get(Main.activeWindow.selectedText).content.length());
					Main.activeWindow.selectedLeftIndex = Math.max(0, Main.activeWindow.selectedIndex - Main.MAX_CHARACTERS_PER_LINE + 1);
				}
			} else {
				int nextDown = nextSpace("down");
				Main.activeWindow.selectedText = nextDown;
				Main.activeWindow.selectedLeftIndex = 0;
			}
		}
	}

	private void delete(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
			if (!controlDown) {
				if (Main.activeWindow.selectedIndex > 0) {
					String content = Main.currentText.content.substring(Main.activeWindow.selectedIndex);
					Main.currentText.content = Main.currentText.content.substring(0,
							Main.activeWindow.selectedIndex - 1) + content;
					Main.activeWindow.selectedIndex--;

					if (Main.activeWindow.selectedText == Main.activeWindow.highlightText
							&& Main.activeWindow.selectedIndex < Main.activeWindow.highlightIndex) {
						Main.tempCursor.visible = false;
					}

					if (Main.activeWindow.selectedIndex < Main.activeWindow.selectedLeftIndex) {
						Main.activeWindow.selectedLeftIndex = Math.max(0,
								Main.activeWindow.selectedIndex - Main.MAX_CHARACTERS_PER_LINE + 1);
					}
				} else {
					if (Main.activeWindow.selectedText > 0) {
						if (Main.activeWindow.selectedText == Main.activeWindow.highlightText
								&& Main.activeWindow.selectedIndex == Main.activeWindow.highlightIndex) {
							Main.tempCursor.visible = false;
						}

						Main.activeWindow.content.remove(Main.activeWindow.selectedText);
						Main.activeWindow.selectedText--;
						Main.activeWindow.selectedIndex = Main.activeWindow.content
								.get(Main.activeWindow.selectedText).content.length();
						Main.activeWindow.selectedLeftIndex = Math.max(0,
								Main.activeWindow.content.get(Main.activeWindow.selectedText).content.length()
										- Main.MAX_CHARACTERS_PER_LINE + 1);
					}
				}
			} else {
				int nextLeft = nextSpace("leftdel");
				int textIndex = Main.activeWindow.selectedText;

				if (Main.activeWindow.selectedIndex == 0 && Main.activeWindow.selectedText > 0) {
					Main.activeWindow.selectedText--;
					Main.activeWindow.selectedIndex = Main.activeWindow.content
							.get(Main.activeWindow.selectedText).content.length();
					nextLeft = nextSpace("left");
					Main.activeWindow.content.get(Main.activeWindow.selectedText).content = Main.activeWindow.content
							.get(Main.activeWindow.selectedText).content.substring(0, nextLeft);
					Main.activeWindow.selectedIndex = nextLeft;

					if (Main.activeWindow.highlightText == Main.activeWindow.selectedText
							&& Main.activeWindow.highlightIndex >= Main.activeWindow.selectedIndex) {
						Main.tempCursor.visible = false;
					}

					for (int i = Main.activeWindow.selectedText + 1; i < textIndex; i++) {
						if (i < Main.activeWindow.content.size()) {
							Main.activeWindow.content.remove(i);
							i--;
						}
					}

					Main.activeWindow.selectedLeftIndex = Math.max(0,
							Main.activeWindow.content.get(Main.activeWindow.selectedText).content.length()
									- Main.MAX_CHARACTERS_PER_LINE + 1);
				} else {
					if (Main.activeWindow.selectedIndex > 0) {
						Main.currentText.content = Main.currentText.content.substring(0, nextLeft)
								+ Main.currentText.content.substring(Main.activeWindow.selectedIndex);
						Main.activeWindow.selectedIndex = nextLeft;

						if (Main.activeWindow.highlightText == Main.activeWindow.selectedText
								&& Main.activeWindow.highlightIndex >= Main.activeWindow.selectedIndex) {
							Main.tempCursor.visible = false;
						}

						if (Main.activeWindow.selectedIndex < Main.activeWindow.selectedLeftIndex) {
							Main.activeWindow.selectedLeftIndex = Math
									.max(0, Main.activeWindow.selectedIndex - Main.MAX_CHARACTERS_PER_LINE + 1);
						}
					} else {
						Main.currentText.content = "";
						Main.activeWindow.selectedIndex = 0;

						if (Main.activeWindow.highlightText == Main.activeWindow.selectedText
								&& Main.activeWindow.highlightIndex >= Main.activeWindow.selectedIndex) {
							Main.tempCursor.visible = false;
						}
					}
				}
			}
		}
	}

	private int nextSpace(String direction) {
		if (direction.equals("right")) {
			if (Main.activeWindow.selectedIndex < Main.currentText.content.length()) {
				for (int i = Main.activeWindow.selectedIndex + 1; i < Main.currentText.content.length(); i++) {
					if (Main.activeWindow.content.get(Main.activeWindow.selectedText).content.charAt(i) == ' ') {
						return i;
					}
				}

				return Main.currentText.content.length();
			} else {
				if (Main.activeWindow.selectedText < Main.activeWindow.content.size() - 1) {
					Main.activeWindow.selectedText++;
					return 0;
				} else {
					return Main.currentText.content.length();
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
					Main.activeWindow.selectedLeftIndex = Math.max(0,
							Main.activeWindow.content.get(Main.activeWindow.selectedText).content.length()
									- Main.MAX_CHARACTERS_PER_LINE + 1);
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
}

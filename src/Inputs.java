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
	}
	
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_CONTROL) controlDown = false;
		if(e.getKeyCode() == KeyEvent.VK_SHIFT) shiftDown = false;
	}
	
	private void shortcuts(KeyEvent e) {
		if(controlDown && shiftDown && e.getKeyCode() == KeyEvent.VK_O) {
			Main.switchWindow();
		}
	}
	
	private void type(KeyEvent e) {
		if((e.getKeyCode() >= 44 && e.getKeyCode() <= 57) ||
				(e.getKeyCode() >= 65 && e.getKeyCode() <= 93) ||
				(e.getKeyCode() == 59) || (e.getKeyCode() == 32) ||
				(e.getKeyCode() == 61) || (e.getKeyCode() == 151) ||
				(e.getKeyCode() == 152) || (e.getKeyCode() == 153) ||
				(e.getKeyCode() == 222)) {
			Main.activeWindow.selectedIndex++;
			Main.activeWindow.content.get(Main.activeWindow.selectedText).content += e.getKeyChar();
		}
	}
}

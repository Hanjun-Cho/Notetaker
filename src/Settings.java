import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;

public class Settings {
	//Cursor Data
	public static String cursorTexturePath = "res/Cursor.png";
	public static String tempCursorTexturePath = "res/TempCursor.png";
	public static float cursorXLerpSpeed = 0.5f; //going below this number can be bad for offsets...
	public static float cursorYLerpSpeed = 0.5f; //same for this value
	
	//Font Data (Dynamic font sizes will come later)
	public static String fontPath = "res/SpaceMono-Regular.ttf";
	
	//Text Data (the lerp values are prolly the lowest it can be, don't fuck around with this)
	public static float textXLerpSpeed = 0.5f;
	public static float textYLerpSpeed = 0.5f;
	
	//Shortcuts (shortcuts should only be prefix + a singular letter i.e CTRL-SHIFT-ALT-A)
	public static String[] commands = new String[] {
		"switchToRightWindow",
		"switchToLeftWindow",
		"deleteLine",
		"changeHighlightStartPosition"
	};
	
	public static ArrayList<String> convert = new ArrayList<String>();
	public static ArrayList<String>[] shortcuts = new ArrayList[commands.length];
	public static HashMap<String, Integer> converter = new HashMap<String, Integer>();
	
	public Settings() {
		for(int i = 0; i < commands.length; i++) shortcuts[i] = new ArrayList<String>(); 
		addShortcut(0, "CTRL-SHIFT-RIGHT");
		addShortcut(1, "CTRL-SHIFT-LEFT");
		addShortcut(2, "CTRL-SHIFT-D");
		addShortcut(3, "CTRL-SHIFT-SPACE");
		setupConvert();
	}
	
	private void setupConvert() {
		addConvert("A", 65);
		addConvert("B", 66);
		addConvert("C", 67);
		addConvert("D", 68);
		addConvert("E", 69);
		addConvert("F", 70);
		addConvert("G", 71);
		addConvert("H", 72);
		addConvert("I", 73);
		addConvert("J", 74);
		addConvert("K", 75);
		addConvert("L", 76);
		addConvert("M", 77);
		addConvert("N", 78);
		addConvert("O", 79);
		addConvert("P", 80);
		addConvert("Q", 81);
		addConvert("R", 82);
		addConvert("S", 83);
		addConvert("T", 84);
		addConvert("U", 85);
		addConvert("V", 86);
		addConvert("W", 87);
		addConvert("X", 88);
		addConvert("Y", 89);
		addConvert("Z", 90);
		
		addConvert("RIGHT", KeyEvent.VK_RIGHT);
		addConvert("LEFT", KeyEvent.VK_LEFT);
		addConvert("SPACE", KeyEvent.VK_SPACE);
	}
	
	private void addConvert(String command, int id) {
		convert.add(command);
		converter.put(command, id);
	}

	public void addShortcut(int index, String command) {
		String[] parts = command.split("-");
		
		for(int i = 0; i < parts.length; i++) {
			shortcuts[index].add(parts[i]);
		}
	}
}
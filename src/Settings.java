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
}

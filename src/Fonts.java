import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;

public class Fonts {
	
	public Font font;
	private float size;
	
	public Fonts(String path, float size) {
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, new File(path)).deriveFont(size);
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
		
		this.size = size;
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		ge.registerFont(font);
	}
	
	public int getFontPixelWidth() {
		return (int)(size*0.65f);
	}
}

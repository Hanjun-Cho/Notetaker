import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.EventListener;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Main extends JPanel implements EventListener {
	
	private static final long serialVersionUID = 1L;
	public static int SCREEN_WIDTH = 1920;
	public static int SCREEN_HEIGHT = 1080;
	public static int FONT_WIDTH = 11;
	public static int MAX_CHARACTERS_PER_LINE;
	
	static Window leftWindow = new Window(true);
	static Window rightWindow = new Window(false);
	public static Window activeWindow = leftWindow;
	
	int FPS;
	int TARGET_FPS = 144;
	boolean isRunning = false;
	public static Robot robot;
	JFrame frame;
	Fonts font = new Fonts("res/SpaceMono-Regular.ttf", 18f);
	public static Cursor cursor = new Cursor();
	static Graphics graphics;
	public static Text currentText;
	public static TempCursor tempCursor = new TempCursor();
	
	public static BufferedImage leftWindowImage;
	public static int SCREEN_RESOLUTION_WIDTH = 0;
	public static int SCREEN_RESOLUTION_HEIGHT = 0;
	
	public Main() {
		leftWindow.active = true;
		leftWindow.content.add(new Text());
		rightWindow.content.add(new Text());
		currentText = leftWindow.content.get(0);
		this.addKeyListener(new Inputs());
		
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		SCREEN_RESOLUTION_WIDTH = gd.getDisplayMode().getWidth();
		SCREEN_RESOLUTION_HEIGHT = gd.getDisplayMode().getHeight();
		
		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
		
		createWindow();
		run();
	}
	
	public static int getTextWidth(String text) {
		return graphics.getFontMetrics().stringWidth(text);
	}
	
	public static float Lerp(float a, float b, float t) {
		return (float)((1.0 - t) * a + b * t);
	}
	
	public static BufferedImage loadImage(String path) throws IOException{
        BufferedImage image = ImageIO.read(new File(path));
        return image;
    }
	
	public static void switchToLeftWindow() {
		leftWindow.active = true;
		rightWindow.active = false;
		activeWindow = leftWindow;
		currentText = leftWindow.content.get(leftWindow.selectedText);
	}
	
	public static void switchToRightWindow() {
		leftWindow.active = false;
		rightWindow.active = true;
		
		int leftEdge = Main.SCREEN_RESOLUTION_WIDTH/2 - Main.SCREEN_WIDTH/2 + 10;
		int topEdge = Main.SCREEN_RESOLUTION_HEIGHT/2 - Main.SCREEN_HEIGHT/2 + 10;
		
		leftWindowImage = new BufferedImage(Main.SCREEN_WIDTH/2 - (Main.activeWindow.sideBarWidth/7), Main.SCREEN_HEIGHT, BufferedImage.TYPE_INT_ARGB);
		Color pixelColor = null;

		for(int x = 0; x < leftWindowImage.getWidth(); x++) {
			for(int y = 0; y < leftWindowImage.getHeight(); y++) {
				pixelColor = robot.getPixelColor(leftEdge + x, topEdge + y);
				leftWindowImage.setRGB(x, y, pixelColor.getRGB());
			}
		}
		
		System.out.println(pixelColor);
		
		activeWindow = rightWindow;
		currentText = rightWindow.content.get(rightWindow.selectedText);
	}
	
	public static void changeHighlightCursorLocation() {
		if(activeWindow.selectedIndex < activeWindow.content.get(activeWindow.selectedText).content.length()) {			
			activeWindow.highlightIndex = activeWindow.selectedIndex;
			activeWindow.highlightText = activeWindow.selectedText;
			tempCursor.visible = true;
		}
	}
	
	private void update() {
		SCREEN_WIDTH = frame.getWidth();
		SCREEN_HEIGHT = frame.getHeight();
		MAX_CHARACTERS_PER_LINE = (((SCREEN_WIDTH/2)-leftWindow.sideBarWidth - leftWindow.windowXOffset)/FONT_WIDTH) - 1;
		
		leftWindow.update();
		rightWindow.update();
		cursor.update();
		tempCursor.update();
		currentText = activeWindow.content.get(activeWindow.selectedText);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2D = (Graphics2D)g;
		graphics = g;
		g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
		
		g.setColor(new Color(0, 0, 0));
		g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
		
		g.setFont(font.font);
		
		if(leftWindow == activeWindow) {
			leftWindow.paint(g2D);
			//rightWindow.paint(g2D);
		}
		else {			
			rightWindow.paint(g2D);
			//leftWindow.paint(g2D);
			g.drawImage(leftWindowImage, 0, 0, Main.SCREEN_WIDTH/2 - (Main.activeWindow.sideBarWidth/7), Main.SCREEN_HEIGHT, null);
		}
		tempCursor.paint(g2D);
		cursor.paint(g2D);
	}
	
	public synchronized void stop() {
		isRunning = false;
	}
	
	public void run() {
		setFocusable(true);
		requestFocus(); 
		long lastTime = System.nanoTime();
		double amountOfTicks = 128.0;
		double nanoSecond = 1000000000 / amountOfTicks;
		double delta = 0;
		long timer = System.currentTimeMillis();

		while (isRunning) {
			long now = System.nanoTime();
			delta += (now - lastTime) / nanoSecond;
			lastTime = now;
			
			
			while (delta >= 1) {
				repaint();
				update();
				
				delta--;
			}

			FPS++;
			
			long frameEndTime = System.nanoTime();

			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				frame.setTitle("Text Rendering (" + FPS + ")");
				FPS = 0;
			}
			
			if((frameEndTime - now) / 1000000 < 1000/TARGET_FPS) {
				try {
					Thread.sleep((long)(1000/TARGET_FPS - (frameEndTime - now) / 1000000));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		stop();
	}
	
	private void createWindow() {
		frame = new JFrame("Text Rendering (" + FPS + ")");
		frame.setMinimumSize(new Dimension(800, 600));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		//frame.setUndecorated(true);
		frame.setLocationRelativeTo(null);
		frame.getContentPane().setBackground(Color.white);
		frame.setResizable(true);
		frame.add(this);
		frame.setVisible(true);
		isRunning = true;
	}
	
	public static void main(String[] args) {
		new Main();
	}
}

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
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
	public static int LINE_HEIGHT = 20;
	public static int MAX_CHARACTERS_PER_LINE;
	public static int MAX_LINES;
	public static int SCREEN_RESOLUTION_WIDTH = 0;
	public static int SCREEN_RESOLUTION_HEIGHT = 0;
	public static int TARGET_FPS = 144;
	public static int MAX_FILE_DISPLAY;
	
	public static Window leftWindow = new Window(true);
	public static Window rightWindow = new Window(false);
	public static Window activeWindow = leftWindow;
	
	public static FileInfo fileInfo = new FileInfo();
	
	private boolean isRunning = false;
	private JFrame frame;
	
	private Fonts editorFont = new Fonts(Settings.editorFontPath, 18f);
	private Fonts fileInfoFont = new Fonts(Settings.fileInfoFontPath, 16f);
	
	public static Cursor cursor = new Cursor(false);
	public static Cursor tempCursor = new Cursor(true);
	public static String newFileString = "";
	
	public Main() {
		new Settings();
		setFocusTraversalKeysEnabled(false);
		this.addKeyListener(new Inputs());
		
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		SCREEN_RESOLUTION_WIDTH = gd.getDisplayMode().getWidth();
		SCREEN_RESOLUTION_HEIGHT = gd.getDisplayMode().getHeight();
		
		createWindow();
		run();
	}
	
	public static void switchToLeftWindow() {
		leftWindow.active = true;
		rightWindow.active = false;
		activeWindow = leftWindow;
	}
	
	public static void switchToRightWindow() {
		leftWindow.active = false;
		rightWindow.active = true;
		activeWindow = rightWindow;
	}
	
	private void update() {
		SCREEN_WIDTH = frame.getWidth();
		SCREEN_HEIGHT = frame.getHeight();
		MAX_CHARACTERS_PER_LINE = (((SCREEN_WIDTH/2)-leftWindow.sideBarWidth - leftWindow.windowXOffset)/FONT_WIDTH) - 1;
		MAX_LINES = (SCREEN_HEIGHT - fileInfo.infoHeight)/LINE_HEIGHT-2;
		MAX_FILE_DISPLAY = (SCREEN_HEIGHT - fileInfo.infoHeight)/58;
		
		if(Main.activeWindow.state == ProgramState.Editor) {
			FONT_WIDTH = editorFont.getFontPixelWidth();
		}
		else {
			FONT_WIDTH = fileInfoFont.getFontPixelWidth();
		}
		
		leftWindow.update();
		rightWindow.update();
		tempCursor.update();
		cursor.update();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2D = (Graphics2D)g;
		g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
		
		g.setColor(new Color(0, 0, 0));
		g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
		
		g.setFont(editorFont.font);
		
		if(leftWindow == activeWindow) {
			leftWindow.paint(g2D);
			rightWindow.paint(g2D);
		}
		else {		
			if(rightWindow.state == ProgramState.Editor) {				
				rightWindow.paint(g2D);
				leftWindow.paint(g2D);
			}
			else {
				leftWindow.paint(g2D);
				rightWindow.paint(g2D);
			}
		}
		
		g.setFont(fileInfoFont.font);
		fileInfo.paint(g2D);
		if(activeWindow.state == ProgramState.Editor) tempCursor.paint(g2D);
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

		while (isRunning) {
			long now = System.nanoTime();
			delta += (now - lastTime) / nanoSecond;
			lastTime = now;
			
			
			while (delta >= 1) {
				repaint();
				update();
				
				delta--;
			}

			long frameEndTime = System.nanoTime();

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
		frame = new JFrame("Notepad");
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
	
	public static BufferedImage loadImage(String path) throws IOException{
        BufferedImage image = ImageIO.read(new File(path));
        return image;
    }
	
	public static void main(String[] args) {
		new Main();
	}
}

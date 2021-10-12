import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.EventListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Main extends JPanel implements EventListener {
	
	private static final long serialVersionUID = 1L;
	public static int SCREEN_WIDTH = 1920;
	public static int SCREEN_HEIGHT = 1080;
	
	Window leftWindow = new Window(true);
	Window rightWindow = new Window(false);
	
	int FPS;
	int TARGET_FPS = 144;
	boolean isRunning = false;
	JFrame frame;
	
	public Main() {
		leftWindow.active = true;

		createWindow();
		run();
	}
	
	private void update() {
		SCREEN_WIDTH = frame.getWidth();
		SCREEN_HEIGHT = frame.getHeight();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2D = (Graphics2D)g;
		g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
		
		g.setColor(new Color(0, 0, 0));
		g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
		
		leftWindow.paint(g2D);
		rightWindow.paint(g2D);
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
		frame.setMinimumSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setLocationRelativeTo(null);
		frame.setUndecorated(true);
		frame.getContentPane().setBackground(Color.white);
		frame.setResizable(false);
		frame.add(this);
		frame.setVisible(true);
		isRunning = true;
	}
	
	public static void main(String[] args) {
		new Main();
	}
}

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.EventListener;
import java.util.Random;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;
import javax.swing.JPanel;

enum HighlightType {
	Red,
	Blue,
	Yellow
}

class Highlight {
	public int start;
	public int end;
	
	public int r;
	public int g;
	public int b;
	
	HighlightType type;
	
	public Highlight(int start, int end, HighlightType type) {
		this.start = start;
		this.end = end;
		setColor(type);
	}
	
	public void setColor(HighlightType type) {
		this.type = type;
		if(type == HighlightType.Red) {
			setColor(new Color(232, 93, 93));
		}
		else if(type == HighlightType.Blue) {
			setColor(new Color(110, 179, 219));
		}
		else {
			setColor(new Color(232, 230, 93));
		}
	}
	
	public void setColor(Color color) {
		this.r = color.getRed();
		this.g = color.getGreen();
		this.b = color.getBlue();
	}
}

class Text {
	public String text;
	public int x;
	public int y;
	public ArrayList<Integer> lastSpaces = new ArrayList<Integer>();
	public ArrayList<Highlight> highlights = new ArrayList<Highlight>();
	
	public Text(int x, int y) {
		this.x = x;
		this.y = y;
		this.text = "";
		lastSpaces.add(0);
	}
	
	public Text(String text, int x, int y) {
		this.text = text;
		this.x = x;
		this.y = y;
		lastSpaces.add(0);
	}
}

public class MainApp extends JPanel implements EventListener{

	private static final long serialVersionUID = 1L;
	public static int SCREEN_WIDTH = 1980, SCREEN_HEIGHT = 1080, FPS = 0, CELL_SIZE = 80, TARGET_FPS = 144;
	private boolean isRunning = false;
	private JFrame frame;
	public static Random rand = new Random();
	
	public static ArrayList<Text> texts = new ArrayList<Text>();
	public static int selectedText = 0;
	public static int selectedIndex = 0;
	static File currentFile = null;
	
	Font font;
	BufferedImage cursor;
	public static String fileName = "filename...";
	public static int fileNameIndex = fileName.length();
	
	public static int topBarHeight = 35;
	public static int lineIncrement = 22;
	public static int textXOffset = 75;
	public static int textYOffset = topBarHeight + lineIncrement + 5;
	public static int cursorXOffset = textXOffset + 1;
	public static int cursorYOffset = textYOffset + 6;
	public static int lineCountXOffset = 15;
	
	public static int cursorXPos = cursorXOffset;
	public static int cursorYPos = cursorYOffset - lineIncrement;
	public static int cursorTargetX = cursorXPos;
	public static int cursorTargetY = cursorYPos;
	public static int textTargetY = textYOffset;
	public static int textWidth = 11;
	
	public static int cursorXPosOffsetNewFile = 124;
	public static int fileSelectionIndex = 0;
	
	public static float cursorXLerpSpeed = 0.5f;
	public static float cursorYLerpSpeed = 0.5f;
	public static float textLerpSpeed = 0.8f;
	
	public static int characters = 300 / textWidth;
	public static int lines = (SCREEN_HEIGHT - textYOffset) / lineIncrement;
	
	public static int startLine = 0;
	public static int endLine = lines;
	public static boolean changed = true;
	static String directory = "files";
	static File[] files = null;
	public static String openFileDir = "files/";
	
	enum programState {
		editor,
		newFile,
		openFile
	}
	
	public static programState state = programState.editor;
	
    public BufferedImage loadImage(String path) throws IOException{
        BufferedImage image = ImageIO.read(new File(path));
        return image;
    }
	
	public MainApp() {		
		try {
			cursor = loadImage("res/Cursor.png");
			font = Font.createFont(Font.TRUETYPE_FONT, new File("res/SpaceMono-Regular.ttf")).deriveFont(18f);
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		ge.registerFont(font);
		
		createWindow();
		texts.add(new Text(textXOffset, textYOffset));
		this.addKeyListener(new KeyInput());
		
		isRunning = true;
		run();
	}
	
	public float Lerp(float a, float b, float t) {
		return (float)((1.0 - t) * a + b * t);
	}
	
	private void update() {
		SCREEN_WIDTH = frame.getWidth();
		SCREEN_HEIGHT = frame.getHeight();
		
		characters = SCREEN_WIDTH / textWidth;
		lines = (SCREEN_HEIGHT - textYOffset) / lineIncrement;
		characters -= 10;
		
		
		if(selectedText >= endLine - 1) {
			endLine++;
			startLine++;
			
			int amountToMove = (textYOffset + ((endLine) * lineIncrement) + 2) - SCREEN_HEIGHT;
			
			textTargetY -= amountToMove;
			cursorYOffset -= amountToMove;
		}
		
		if(startLine != 0 && selectedText <= startLine - 1) {
			endLine--;
			startLine--;
			
			int amountToMove = SCREEN_HEIGHT - (textYOffset + ((endLine) * lineIncrement) + 2);
			
			textTargetY += amountToMove;
			cursorYOffset += amountToMove;
		}
		
		if(state == programState.newFile) {
			cursorTargetX = cursorXPosOffsetNewFile + (textWidth * fileName.length());
			cursorTargetY = 9;
		}
		else if(state == programState.editor) {			
			cursorTargetX = cursorXOffset + (selectedIndex * textWidth);
			cursorTargetY = cursorYOffset + (lineIncrement * (selectedText - 1));
		}
		
		cursorXPos = (int)Lerp(cursorXPos, cursorTargetX, cursorXLerpSpeed);
		cursorYPos = (int)Lerp(cursorYPos, cursorTargetY, cursorYLerpSpeed);
		textYOffset = (int)Lerp(textYOffset, textTargetY, textLerpSpeed);
	}
	
	public static int lastEmptyText() {
		int lowestIndex = -1;
		int lowestDistance = Integer.MAX_VALUE;
		
		for(int i = 0; i < texts.size(); i++) {
			if(MainApp.texts.get(i).text.length() == 0 &&
					Math.abs(i - MainApp.selectedText) < lowestDistance &&
					i < MainApp.selectedText) {
				lowestDistance = Math.abs(i - MainApp.selectedText);
				lowestIndex = i;
			}
		}

		return lowestIndex;
	}
	
	public static int nextEmptyText() {
		int lowestIndex = -1;
		int lowestDistance = Integer.MAX_VALUE;
		
		for(int i = 0; i < texts.size(); i++) {
			if(MainApp.texts.get(i).text.length() == 0 &&
					i - MainApp.selectedText < lowestDistance &&
					i > MainApp.selectedText) {
				lowestDistance = i - MainApp.selectedText;
				lowestIndex = i;
			}
		}

		return lowestIndex;
	}
	
	public static void createFile() {
		File file = new File("files/" + fileName + ".nt");
		
		try {
			file.createNewFile();
			currentFile = file;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void inStart(int start, int end, HighlightType type) {
		if(texts.get(selectedText).highlights.size() > 0) {		
			boolean didSomething = false;
			for(int i = 0; i < texts.get(selectedText).highlights.size(); i++) {
				int s = texts.get(selectedText).highlights.get(i).start;
				int e = texts.get(selectedText).highlights.get(i).end;
				
				if(s == start) {
					if(e <= end) {
						texts.get(selectedText).highlights.get(i).setColor(type);
						didSomething = true;
						break;
					}
					else if(e > end) {
						texts.get(selectedText).highlights.get(i).start = end;
						texts.get(selectedText).highlights.get(i).end = e;
						
						texts.get(selectedText).highlights.add(new Highlight(start, end, type));
						didSomething = true;
						break;
					}
				}
				else if(s < start) {
					if(e <= end) {
						texts.get(selectedText).highlights.get(i).end = start;
						texts.get(selectedText).highlights.add(new Highlight(start, end, type));
						didSomething = true;
						break;
					}
					else if(e > end) {
						texts.get(selectedText).highlights.get(i).end = start;
						
						texts.get(selectedText).highlights.add(new Highlight(start, end, type));
						texts.get(selectedText).highlights.add(new Highlight(end, e, texts.get(selectedText).highlights.get(i).type));
						didSomething = true;
						break;
					}
				}
				else if(s > start && end >= s) {
					if(e <= end) {
						texts.get(selectedText).highlights.get(i).start = start;
						texts.get(selectedText).highlights.get(i).end = end;
						texts.get(selectedText).highlights.get(i).setColor(type);
						didSomething = true;
						break;
					}
					else if(e > end) {
						texts.get(selectedText).highlights.get(i).start = end;
						texts.get(selectedText).highlights.add(new Highlight(start, end, type));
						didSomething = true;
						break;
					}
				}
			}
			
			if(!didSomething) {
				texts.get(selectedText).highlights.add(new Highlight(start, end, type));
			}
		}
		else {
			texts.get(selectedText).highlights.add(new Highlight(start, end, type));
		}
	}
	
	public static void save() {
		changed = false;
		
		try {
			FileWriter writer = new FileWriter(currentFile);

			for(int i = 0; i < texts.size(); i++) {
				writer.write(texts.get(i).text);
				
				if(i != texts.size() - 1) {
					writer.write("\n");
				}
			}
			
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void listFiles() {
		File dir = new File(directory);
		files = dir.listFiles();
		
		if(files != null) {
			for(File file : files) {
				System.out.println(file.getName());
			}
		}
	}
	
	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2D = (Graphics2D)g;
		g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
		
		g.setColor(new Color(20, 20, 20));
		g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
		
		g.setFont(font);
		
		//side-bar
		g.setColor(new Color(23, 23, 23));
		g.fillRect(0, 0, 61, SCREEN_HEIGHT);

		for(int i = 0; i < texts.size(); i++) {
			int Xoffset = lineCountXOffset;
			
			if(i >= 0 && i < 9) {
				Xoffset += 20;
			}
			else if(i >= 9 && i < 99) {
				Xoffset += 10;
			}
			
			int startIndex = 0;
			int offset = 0;
			
			if(texts.get(i).highlights.size() != 0) {				
				int[][] index = new int[texts.get(i).highlights.size()][2];
				
				for(int j = 0; j < texts.get(i).highlights.size(); j++) {
					index[j][0] = texts.get(i).highlights.get(j).start;
					index[j][1] = j;
				}
				
				Arrays.sort(index, (a, b) -> a[0] - b[0]);
				ArrayList<Highlight> highlights = new ArrayList<Highlight>();
				
				for(int j = 0; j < index.length; j++) {
					highlights.add(texts.get(i).highlights.get(index[j][1]));
				}
				
				for(int j = 0; j < highlights.size(); j++) {
					int start = highlights.get(j).start;
					int end = highlights.get(j).end;
					
					int r = highlights.get(j).r;
					int gr = highlights.get(j).g;
					int b = highlights.get(j).b;
					
					if(start > texts.get(i).text.length() - 1) {
						texts.get(i).highlights.remove(j);
					}
					else {						
						g.setColor(new Color(200, 200, 200));
						g.drawString(texts.get(i).text.substring(startIndex, Math.min(start, texts.get(i).text.length())), offset + textXOffset, textYOffset + (i * lineIncrement));
						
						g.setColor(new Color(r, gr, b));
						int width = texts.get(i).text.substring(startIndex, Math.min(start, texts.get(i).text.length())).length();
						g.drawString(texts.get(i).text.substring(Math.min(start, texts.get(i).text.length()), Math.min(end, texts.get(i).text.length())), offset + width * textWidth + textXOffset, textYOffset + (i * lineIncrement));
						
						startIndex = Math.min(end, texts.get(i).text.length());
						offset += textWidth * (width + texts.get(i).text.substring(Math.min(start, texts.get(i).text.length()), Math.min(end, texts.get(i).text.length())).length());
					}
				}
			}
			
			g.setColor(new Color(200, 200, 200));
			g.drawString(texts.get(i).text.substring(startIndex), offset + textXOffset, textYOffset + (i * lineIncrement));
			
			//side bar number rendering
			g.setColor(new Color(112, 112, 112));
			g.drawString(String.valueOf(i + 1), Xoffset, textYOffset + (i * lineIncrement));
		}
		
		//cursor
		if(state == programState.editor) {			
			g.setColor(new Color(126, 173, 230));
			g.drawImage(cursor, cursorXPos, cursorYPos, 10, 20, null, null);
		}
		
		//top-bar
		g.setColor(new Color(41, 41, 41));
		g.fillRect(0, 0, SCREEN_WIDTH, topBarHeight);
		
		if(state == programState.editor) {
			g.setColor(new Color(200, 200, 200));
			String text = currentFile == null ? "empty file" : currentFile.getName();
			g.drawString(text, 12, 24);
			
			if(changed) {				
				g.setColor(new Color(232, 93, 93));
				g.drawString("*", 20 + (text.length() * textWidth), 25);
			}
		}
		
		if(state == programState.newFile) {
			g.setColor(new Color(23, 23, 23));
			g.fillRect(0, topBarHeight, SCREEN_WIDTH, SCREEN_HEIGHT);
			g.setColor(new Color(110, 179, 219));
			g.drawString("New File: ", 12, 24);
			g.setColor(new Color(210, 210, 210));
			
			if(fileName.trim().equals("filename..."))
				g.setColor(new Color(150, 150, 150));
			
			g.drawString(fileName, 122, 24);
			g.setColor(new Color(126, 173, 230));
			g.drawImage(cursor, cursorXPos, cursorYPos, 10, 20, null, null);
			
			if(files != null) {
				int i = 0;
				
				for(File file : files) {
					int height = 65;
					int yPos = 48 + (i * 70);
					
					if(i == 0) height = 60;
					if(i >= 1) yPos -= 5;
					
					g.setColor(new Color(38, 38, 38));
					g.fillRect(12, yPos, SCREEN_WIDTH - 40, height);
					
					g.setColor(new Color(200, 200, 200));
					g.drawString(file.getName(), 35, yPos + 38);
					
					g.setColor(new Color(232, 93, 93, 150));
					g.drawString("Last Opened: " + new Date(file.lastModified()).toString(), 50 + (file.getName().length() * textWidth), yPos + 38);
					i++;
				}
			}
		}
		
		if(state == programState.openFile) {
			g.setColor(new Color(23, 23, 23));
			g.fillRect(0, topBarHeight, SCREEN_WIDTH, SCREEN_HEIGHT);
			g.setColor(new Color(110, 179, 219));
			g.drawString("Open File", 12, 24);
			
			if(files != null) {
				int i = 0;
				
				for(File file : files) {
					int height = 65;
					int yPos = 48 + (i * 70);
					
					if(i == 0) height = 60;
					if(i >= 1) yPos -= 5;
					
					g.setColor(new Color(38, 38, 38));
					if(fileSelectionIndex == i)
						g.setColor(new Color(25, 25, 25));
					g.fillRect(12, yPos, SCREEN_WIDTH - 40, height);
					
					g.setColor(new Color(200, 200, 200));
					g.drawString(file.getName(), 35, yPos + 38);
					
					g.setColor(new Color(232, 93, 93, 150));
					g.drawString("Last Opened: " + new Date(file.lastModified()).toString(), 50 + (file.getName().length() * textWidth), yPos + 38);
					i++;
				}
			}
		}
	}
	
	public static void openFile() {
		//(TODO): Figure out the lines which are joined with the next and ones which are intentionally line gaped
		//Save and load that so it works
		
		//(TODO): Figure out how to save the highlights... fuck that thing is going to screw with my brain
		//Save it within file and not display? Encoded files with html inside which the code can read to add the highlights?
		
		//(TODO): There's a highlighting problem, what the fuck was young tommy doing ffs... Anyway, to replicate just
		//highlight an early part of the text then highlight an area of the text after that
		
		File file = files[fileSelectionIndex];
		currentFile = file;
		openFileDir += file.getName();
		
		for(int i = 0; i < texts.size(); i++) {
			texts.remove(i);
			i--;
		}
		
		Scanner myReader;
		try {
			myReader = new Scanner(file);
			
			int i = 0;
			
			while (myReader.hasNextLine()) {
				String data = myReader.nextLine();
				texts.add(new Text(0, 0));
				texts.get(i).text = data;
				
				for(int j = 0; j < data.length(); j++) {
					if(data.charAt(j) == ' ') {
						texts.get(i).lastSpaces.add(0, j);
					}
				}
				
				i++;
			}
			
			if(i == 0) {
				texts.add(new Text(0, 0));
			}
			
			for(int j = 0; j < texts.size(); j++) {
				String text = texts.get(j).text.substring(Math.min(texts.get(j).text.length(), characters));
				texts.get(j).text = texts.get(j).text.substring(0, Math.min(characters, texts.get(j).text.length()));
				
				if(text.length() > 0) {
					for(int k = 0; k < texts.get(j).lastSpaces.size(); k++) {
						if(texts.get(j).lastSpaces.get(k) >= texts.get(j).text.length()) {
							texts.get(j).lastSpaces.remove(k);
							k--;
						}
					}
					
					texts.add(j + 1, new Text(0, 0));
					texts.get(j + 1).text = text;
				}
			}
			
			myReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	    
		System.out.println(openFileDir);
		openFileDir = openFileDir.substring(0, openFileDir.length() - file.getName().length());
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
		frame = new JFrame("Text Rendering Test Engine (" + FPS + ")");
		frame.setMinimumSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setLocationRelativeTo(null);
		frame.setUndecorated(true);
		frame.getContentPane().setBackground(Color.white);
		frame.setResizable(false);
		frame.add(this);
		frame.setVisible(true);
	}
	
	public static void main(String[] args) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
		new MainApp();
	}
}

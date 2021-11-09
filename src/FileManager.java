import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class FileManager {
	
	public static ArrayList<File> getFiles(String directory) {
		File dir = new File(directory);
		File[] files = dir.listFiles();
		ArrayList<File> f = new ArrayList<File>();
		
		if(files != null) {
			for(File file : files) {
				if(file.isDirectory()) {
					f.add(file);
				}
			}
			
			for(File file : files) {
				if(file.isFile()) {
					f.add(file);
				}
			}
		}
		
		return f;
	}
	
	public static ArrayList<File> getFiles(String directory, String input){
		File dir = new File(directory);
		File[] files = dir.listFiles();
		ArrayList<File> f = new ArrayList<File>();
		
		if(files != null) {
			for(File file : files) {
				if(file.isDirectory() && file.getName().length() >= input.length() && file.getName().toLowerCase().substring(0, input.length()).equals(input.toLowerCase())) {
					f.add(file);
				}
			}
			
			for(File file : files) {
				if(file.isFile() && file.getName().length() >= input.length() && file.getName().toLowerCase().substring(0, input.length()).equals(input.toLowerCase())) {
					f.add(file);
				}
			}
		}
		
		return f;
	}
	
	public static void saveFile() {
		File file = Main.activeWindow.selectedFile;
		
		if(file != null) {			
			if(Main.activeWindow.needsSave) {
				try {
					FileWriter writer = new FileWriter(file);
					
					for(Text t : Main.activeWindow.content) {
						writer.write(t.content + '\n');
					}
					
					Main.activeWindow.needsSave = false;
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		else {
			Main.activeWindow.state = ProgramState.NewFile;
		}
	}
	
	public static void saveFile(boolean b, File file) {
		try {
			FileWriter writer = new FileWriter(file);
			
			for(Text t : Main.activeWindow.content) {
				writer.write(t.content + "\n");
			}
			
			Main.activeWindow.needsSave = false;
			Main.activeWindow.state = ProgramState.Editor;
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void loadFile(File file) {
		boolean hadFile = Main.activeWindow.selectedFile != null;
		Main.activeWindow.selectedFile = file;
		Main.activeWindow.needsSave = false;
		
		try {		
			if(!hadFile) saveFile(true, file);
			Scanner scanner = new Scanner(file);
			Main.activeWindow.content.clear();
			Main.activeWindow.content.add(new Text());
			int i = 0;
				
			while(scanner.hasNextLine()) {
				if(Main.activeWindow.content.size() <= i) Main.activeWindow.content.add(new Text());
				String line = scanner.nextLine();
				Main.activeWindow.content.get(i).content = line;
			}

			Main.activeWindow.selectedIndex = 0;
			Main.activeWindow.selectedText = 0;
			Main.activeWindow.state = ProgramState.Editor;
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void loadFile(String path) {
		if(Main.activeWindow.fileSelectIndex < Main.activeWindow.files.size()) {			
			String p = Main.activeWindow.files.get(Main.activeWindow.fileSelectIndex).getName();
			
			for(int i = 0; i < Main.activeWindow.files.size(); i++) {
				if(Main.activeWindow.files.get(i).getName().equals(p)) {
					if(Main.activeWindow.files.get(i).isFile()) {
						loadFile(new File(path + p));
					}
					else {
						Main.fileInfo.path += Main.activeWindow.files.get(i).getName() + "/";
						Main.activeWindow.fileSelectIndex = 0;
					}
				}
			}
		}
	}
	
	//0 if false, 1 if true, 2 if needs confirmation
	public static int createFile(String filePath) {
		if(Main.activeWindow.files.size() > Main.activeWindow.fileSelectIndex && Main.activeWindow.files.get(Main.activeWindow.fileSelectIndex).isDirectory()) {
			Main.fileInfo.path += Main.activeWindow.files.get(Main.activeWindow.fileSelectIndex).getName();
			Main.activeWindow.fileSelectIndex = 0;
			return 0;
		}
		else {
			if(Main.fileInfo.newFileString.length() > 0) {				
				String path = Main.fileInfo.path + "/" + Main.fileInfo.newFileString + ".nt";
				boolean contains = false;
				
				for(int i = 0; i < Main.activeWindow.files.size(); i++) {
					if(Main.activeWindow.files.get(i).getName().equals(Main.fileInfo.newFileString + ".nt")) {
						contains = true;
					}
				}
				
				if(!contains) {
					//create the file
					try {
						File file = new File(path);
						if(file.createNewFile()) {
							loadFile(file);
							return 1;
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				else {
					//confirmation for file overwrite
					System.out.println("Overwrite File?");
					return 2;
				}
			}
			else {
				return 0;
			}
		}
	
		Main.fileInfo.newFileString = "";
		return 0;
	}
}

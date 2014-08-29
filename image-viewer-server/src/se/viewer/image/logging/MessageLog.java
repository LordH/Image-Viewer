package se.viewer.image.logging;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Observable;
import java.util.stream.Stream;

/**
 * Class for storing and retrieving clients' message logs
 * @author Harald Brege
 */
public class MessageLog extends Observable implements LogInterface {
	
	private ArrayList<String> log;
	private File logFile;
	
	public MessageLog() {
		log = new ArrayList<String>();
	}
	
	//=======================================
	//	MESSAGE LOGGING
	//---------------------------------------

	@Override
	public void userLoggedIn(String user) {
		setupLogFile(user);
	}

	@Override
	public void newLogMessage(String message) {
		Calendar time = Calendar.getInstance();
		Date date = time.getTime();
		
		String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
		String add = "[" + timestamp + "] " + message;
		log.add(add);
		
		setChanged();
		notifyObservers(add);
		clearChanged();
	}

	@Override
	public void shutdown() {
		if(logFile != null)
			try {
				logFile.setWritable(true);
				PrintWriter pw = new PrintWriter(logFile);
				for(String message : log) 
					pw.write(message + "\r\n");
				pw.close();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
	}

	//=======================================
	//	PRIVATE METHODS
	//---------------------------------------
	
	private void setupLogFile(String user) {
		File directory = new File("logs");
		if(!directory.exists())
			directory.mkdirs();
		
		String[] logs = directory.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".log");
			}
		});
		
		boolean found = false;
		for(String log : logs)
			if(log == "logs\\" + user + ".log") {
				logFile = new File(log);
				found = true;
				break;
			}
		
		log = new ArrayList<String>();
		if(found) {
			try {
				Stream<String> stream = Files.lines(logFile.toPath());
				Iterator<String> iter = stream.iterator();
				while(iter.hasNext())
					log.add(iter.next());
				stream.close();
				
			} catch (FileNotFoundException e) {
				System.err.println("Could not find file");
			} catch (IOException e) {
				System.err.println("Cold not read file");
			}
		}
		else {
			logFile = new File("logs\\" + user + ".log");
			try {
				logFile.createNewFile();
			} catch (IOException e) {
				System.err.println("Could not create file");
			}
		}
	}
}

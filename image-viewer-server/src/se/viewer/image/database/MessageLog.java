package se.viewer.image.database;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observable;
import java.util.stream.Stream;

import se.viewer.image.server.ClientConnection;

/**
 * Class for storing and retrieving clients' message logs
 * @author Harald Brege
 */
public class MessageLog extends Observable {
	
	private ArrayList<String> log;
	private File logFile;
	
	public MessageLog(ClientConnection client) {
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
			if(log == "logs\\" + client.getClientId()) {
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
			logFile = new File("logs\\" + client.getClientId() + ".log");
			try {
				logFile.createNewFile();
			} catch (IOException e) {
				System.err.println("Could not create file");
			}
		}
	}
	
	public void newLogMessage(String message) {
		log.add(message);
		setChanged();
		notifyObservers(message);
		clearChanged();
	}
	
	public void shutdown() {
		try {
			logFile.setWritable(true);
			PrintWriter pw = new PrintWriter(logFile);
			for(String message : log) 
				pw.write(message);
			pw.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}

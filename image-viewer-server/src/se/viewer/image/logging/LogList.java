package se.viewer.image.logging;

import java.util.ArrayList;

import se.viewer.image.server.ClientConnection;

/**
 * Class for handling a list of message logs linked to certain clients
 * @author Harald Brege
 */
public class LogList {

	private ArrayList<ClientConnection> userList;
	private ArrayList<LogInterface> logList;
	
	protected LogList() {
		userList = new ArrayList<ClientConnection>();
		logList = new ArrayList<LogInterface>();
	}
	
	public boolean add(ClientConnection user, LogInterface log) {
		if(!userList.contains(user)) {
			userList.add(user);
			logList.add(log);
			return true;
		}
		return false;
	}
	
	public boolean contains(ClientConnection user) {
		return userList.contains(user);
	}
	
	public boolean remove(ClientConnection user) {
		int index = userList.indexOf(user);
		if(index != -1) {
			userList.remove(index);
			logList.remove(index);
			return true;
		}
		return false;
	}
	
	public LogInterface get(ClientConnection user) {
		LogInterface db = null;
		int index = userList.indexOf(user);
		if(index != -1)
			db = logList.get(index);
		return db;
	}
}

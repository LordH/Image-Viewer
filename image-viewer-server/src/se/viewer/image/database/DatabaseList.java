package se.viewer.image.database;

import java.util.ArrayList;

import se.viewer.image.server.ClientConnection;

/**
 * Class for handling a list of databases linked to certain clients
 * @author Harald Brege
 */
public class DatabaseList {

	private ArrayList<ClientConnection> userList;
	private ArrayList<DatabaseInterface> dbList;
	
	protected DatabaseList() {
		userList = new ArrayList<ClientConnection>();
		dbList = new ArrayList<DatabaseInterface>();
	}
	
	public boolean add(ClientConnection user, DatabaseInterface db) {
		if(!userList.contains(user)) {
			userList.add(user);
			dbList.add(db);
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
			dbList.remove(index);
			return true;
		}
		return false;
	}
	
	public DatabaseInterface get(ClientConnection user) {
		DatabaseInterface db = null;
		int index = userList.indexOf(user);
		if(index != -1)
			db = dbList.get(index);
		return db;
	}
}

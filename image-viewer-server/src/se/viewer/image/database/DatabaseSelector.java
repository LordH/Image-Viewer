package se.viewer.image.database;

import se.viewer.image.server.ClientConnection;

/**
 * Class that selects the proper database to use
 * @author Harald Brege
 */
public class DatabaseSelector {

	private static DatabaseList list = new DatabaseList();
	
	/**
	 * Called to get a database for the specified client
	 * @param client The client whose databse is desired
	 * @return The database for that client
	 */
	public static DatabaseInterface getDatabase(ClientConnection client) {
		if(!list.contains(client)) 
			list.add(client, new DatabaseMySQL(client));
		return list.get(client);
	}
}

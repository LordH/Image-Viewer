package se.viewer.image.logging;

import se.viewer.image.server.ClientConnection;

public class LogFinder {

	private static LogList list = new LogList();
	
	/**
	 * Called to get the image gallery for a certain client
	 * @param client The client whose image gallery is desired
	 * @return The image gallery for that client, or null if the client isn't authenticated
	 */
	public static LogInterface getGallery(ClientConnection client) {
		if(client.isAuthenticated()) {
			if(!list.contains(client)) 
				list.add(client, new MessageLog());
			return list.get(client);
		}
		return null;
	}
}

package se.viewer.image.gallery;

import se.viewer.image.server.ClientConnection;

/**
 * Class for selecting the proper image gallery implementation for a client
 * @author Harald Brege
 */
public class GallerySelector {

	private static GalleryList list = new GalleryList();
	
	/**
	 * Called to get the image gallery for a certain client
	 * @param client The client whose image gallery is desired
	 * @return The image gallery for that client, or null if the client isn't authenticated
	 */
	public static GalleryInterface getGallery(ClientConnection client) {
		if(client.isAuthenticated()) {
			if(!list.contains(client)) 
				list.add(client, new ImageGallery(client));
			return list.get(client);
		}
		return null;
	}
}

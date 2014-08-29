package se.viewer.image.server.requests;

import java.io.ObjectOutputStream;

import se.viewer.image.database.DatabaseInterface;
import se.viewer.image.database.DatabaseSelector;
import se.viewer.image.gallery.GalleryInterface;
import se.viewer.image.gallery.GallerySelector;
import se.viewer.image.logging.LogInterface;
import se.viewer.image.server.ClientConnection;
import se.viewer.image.tokens.Token;

/**
 * Abstract class that describes a generic request handler
 * @author Harald Brege
 */
public abstract class RequestHandler{
	protected Token token;
	protected ClientConnection client;
	protected ObjectOutputStream stream;
	protected GalleryInterface gallery;
	protected LogInterface log;
	protected DatabaseInterface database;
	
	/**
	 * Creates a generic client request handler
	 * @param token The token to be processed
	 * @param client The client
	 */
	public RequestHandler(Token token, ClientConnection client) {
		this.token = token;
		this.client = client;
		
		stream = client.getStream();
		log = client.getMessageLog();
		
		gallery = GallerySelector.getGallery(client);
		database = DatabaseSelector.getDatabase(client);
	}

	/**
	 * Called to make the handler do its job
	 * @return Operation success or failure
	 */
	public abstract boolean dealWithIt();
	
}

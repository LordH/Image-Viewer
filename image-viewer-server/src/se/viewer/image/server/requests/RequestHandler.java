package se.viewer.image.server.requests;

import java.io.ObjectOutputStream;

import se.viewer.image.database.GalleryInterface;
import se.viewer.image.database.MessageLog;
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
	protected MessageLog log;
	
	/**
	 * Creates a generic client request handler
	 * @param token The token to be processed
	 * @param client The client
	 */
	public RequestHandler(Token token, ClientConnection client) {
		this.token = token;
		this.client = client;
		
		stream = client.getStream();
		gallery = client.getImageServer();
		log = client.getMessageLog();
	}

	/**
	 * Called to make the handler do its job
	 * @return Operation success or failure
	 */
	public abstract boolean dealWithIt();
	
}

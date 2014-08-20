package se.viewer.image.server.requests;

import java.io.ObjectOutputStream;

import se.viewer.image.server.ClientConnection;
import se.viewer.image.tokens.Token;

/**
 * Abstract class that describes a generic request handler
 * @author Harald Brege
 */
public abstract class RequestHandler{
	protected Token token;
	protected ObjectOutputStream oos;
	protected ClientConnection client;
	
	/**
	 * Creates a generic client request handler
	 * @param token The token to be processed
	 * @param oos The output stream to the client
	 * @param client The connection to the client
	 */
	public RequestHandler(Token token, ObjectOutputStream oos, ClientConnection client) {
		this.token = token;
		this.oos = oos;
		this.client = client;
	}

	/**
	 * Called to make the handler do its job
	 * @return Operation success or failure
	 */
	public abstract boolean dealWithIt();
	
}

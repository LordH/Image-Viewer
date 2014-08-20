package se.viewer.image.server.requests;

import se.viewer.image.server.ClientConnection;
import se.viewer.image.tokens.Token;

/**
 * Handler for client logout 
 * @author Harald Brege
 */
public class LogoutHandler extends RequestHandler {
	
	public LogoutHandler(Token token, ClientConnection client) {
		super(token, client);
	}
	
	@Override
	public boolean dealWithIt() {
		client.disconnect();
		return true;
	}

}

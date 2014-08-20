package se.viewer.image.server.requests;

import java.io.ObjectOutputStream;

import se.viewer.image.server.ClientConnection;
import se.viewer.image.tokens.Token;

public class LogoutHandler extends RequestHandler {
	
	public LogoutHandler(Token token, ObjectOutputStream oos, ClientConnection client) {
		super(token, oos, client);
	}
	
	@Override
	public boolean dealWithIt() {
		client.disconnect();
		return true;
	}

}

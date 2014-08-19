package se.viewer.image.server.requests;

import java.io.ObjectOutputStream;

import se.viewer.image.server.ClientConnection;
import se.viewer.image.tokens.Token;

public class LogoutHandler implements RequestHandlerInterface {

	private ClientConnection client;
	
	public LogoutHandler(ClientConnection client) {
		this.client = client;
	}
	
	@Override
	public boolean dealWithIt(Token token, ObjectOutputStream outStream) {
		client.disconnect();
		return true;
	}

}

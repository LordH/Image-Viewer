package se.viewer.image.server.requests;

import java.io.IOException;

import se.viewer.image.server.ClientConnection;
import se.viewer.image.tokens.Token;

/**
 * Handler for forbidden client requests
 * @author Harald Brege
 */
public class DeniedHandler extends RequestHandler {

	public DeniedHandler(Token token, ClientConnection client) {
		super(token, client);
	}

	@Override
	public boolean dealWithIt() {
		try {
			stream.writeObject(token);
			stream.flush();
			return true;
		} catch (IOException e) {
			System.err.println("Could not send image.");
			e.printStackTrace();
		}
		return false;
	}

}

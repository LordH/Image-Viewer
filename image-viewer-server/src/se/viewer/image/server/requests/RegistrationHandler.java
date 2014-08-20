package se.viewer.image.server.requests;

import java.io.IOException;

import se.viewer.image.database.DatabaseSelector;
import se.viewer.image.server.ClientConnection;
import se.viewer.image.tokens.RegisterUserToken;
import se.viewer.image.tokens.Token;

/**
 * Handler for registration requests
 * @author Harald Brege
 */
public class RegistrationHandler extends RequestHandler {

	public RegistrationHandler(Token token, ClientConnection client) {
		super(token, client);
	}

	@Override
	public boolean dealWithIt() {
		boolean success = DatabaseSelector.getDB().registerUser(
				((RegisterUserToken) token).getUsername(), 
				((RegisterUserToken) token).getPassword() );
		try {
			stream.writeObject(success);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return success;
	}


}

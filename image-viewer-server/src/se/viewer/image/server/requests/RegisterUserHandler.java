package se.viewer.image.server.requests;

import java.io.IOException;

import se.viewer.image.database.DatabaseSelector;
import se.viewer.image.server.ClientConnection;
import se.viewer.image.tokens.RegisterUserToken;
import se.viewer.image.tokens.Token;

public class RegisterUserHandler extends RequestHandler {

	public RegisterUserHandler(Token token, ClientConnection client) {
		super(token, client);
	}

	@Override
	public boolean dealWithIt() {
		boolean success = DatabaseSelector.getDB().registerUser(
				((RegisterUserToken) token).getUsername(), 
				((RegisterUserToken) token).getPassword() );
		try {
			oos.writeObject(success);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return success;
	}


}

package se.viewer.image.server.requests;

import java.io.IOException;
import java.io.ObjectOutputStream;

import se.viewer.image.database.DatabaseSelector;
import se.viewer.image.server.ClientConnection;
import se.viewer.image.tokens.RegisterUserToken;
import se.viewer.image.tokens.Token;

public class RegisterUserHandler extends RequestHandler {

	public RegisterUserHandler(Token token, ObjectOutputStream oos,ClientConnection client) {
		super(token, oos, client);
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

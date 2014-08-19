package se.viewer.image.server.requests;

import java.io.IOException;
import java.io.ObjectOutputStream;

import se.viewer.image.database.DatabaseSelector;
import se.viewer.image.tokens.RegisterUserToken;
import se.viewer.image.tokens.Token;

public class RegisterUserHandler implements RequestHandlerInterface {

	@Override
	public boolean dealWithIt(Token token, ObjectOutputStream outStream) {
		
		boolean success = DatabaseSelector.getDB().registerUser(((RegisterUserToken) token).getUsername(), 
				((RegisterUserToken) token).getPassword());
		try {
			outStream.writeObject(success);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return success;
	}


}

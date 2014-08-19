package se.viewer.image.server.requests;

import java.io.IOException;
import java.io.ObjectOutputStream;

import se.viewer.image.database.DatabaseSelector;
import se.viewer.image.tokens.LoginFailedToken;
import se.viewer.image.tokens.LoginSuccessToken;
import se.viewer.image.tokens.LoginToken;
import se.viewer.image.tokens.Token;

/**
 * Handler for login requests
 * @author Harald Brege
 */
public class LoginHandler implements RequestHandlerInterface {
	
	private int attemptsLeft;
	
	public LoginHandler(int attemptsLeft) {
		this.attemptsLeft = attemptsLeft;
	}
	
	@Override
	public boolean dealWithIt(Token token, ObjectOutputStream outStream) {
		LoginToken request = (LoginToken) token;
		
		String user = request.getUser();
		String password = request.getPassword();
		
		boolean success = DatabaseSelector.getDB().authenticateUser(user, password);
		
		if(success)
			try {
				outStream.writeObject(new LoginSuccessToken());
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		else
			try {
				outStream.writeObject(new LoginFailedToken("wrong credentials", attemptsLeft));
				System.out.println("User login by " + user + " failed, " + attemptsLeft + " attempts left.");
				return false;
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		return false;
	}

}

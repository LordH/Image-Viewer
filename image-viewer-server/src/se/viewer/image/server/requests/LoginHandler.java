package se.viewer.image.server.requests;

import java.io.IOException;

import se.viewer.image.database.DatabaseSelector;
import se.viewer.image.server.ClientConnection;
import se.viewer.image.tokens.LoginFailedToken;
import se.viewer.image.tokens.LoginSuccessToken;
import se.viewer.image.tokens.LoginToken;
import se.viewer.image.tokens.Token;

/**
 * Handler for client login requests
 * @author Harald Brege
 */
public class LoginHandler extends RequestHandler {
	
	private int left;
	
	/**
	 * Creates a client login request handler
	 * @param left Remaining login attempts
	 */
	public LoginHandler(Token token, ClientConnection client) {
		super(token, client);
		left = 2;
	}
	
	@Override
	public boolean dealWithIt() {
		LoginToken request = (LoginToken) token;
		
		String user = request.getUser();
		String password = request.getPassword();
		boolean success = DatabaseSelector.getDB().authenticateUser(user, password);
		
		if(success)
			try {
				client.authenticate(request.getUser());
				oos.writeObject(new LoginSuccessToken());
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		else
			try {
				oos.writeObject(new LoginFailedToken("wrong credentials", left));
				System.out.println("User login by " + user + " failed, " + left + " attempts left.");
				left--;
				if(left < 0)
					client.blacklist();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		return false;
	}

}

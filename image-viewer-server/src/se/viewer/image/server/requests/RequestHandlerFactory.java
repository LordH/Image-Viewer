package se.viewer.image.server.requests;

import se.viewer.image.server.ClientConnection;
import se.viewer.image.tokens.DeniedToken;
import se.viewer.image.tokens.Messages;
import se.viewer.image.tokens.Token;

/**
 * Class for handling different client requests
 * @author Harald Brege
 */
public class RequestHandlerFactory {

	private ClientConnection client;	
	private LoginHandler login;
	
	/**
	 * Creates a new factory for the supplied client
	 * @param client The client
	 */
	public RequestHandlerFactory(ClientConnection client) {
		this.client = client;
	}
	
	/**
	 * Handles the request token and sends results to the client.
	 * @param token The token to be handled, null results in a Denied answer being sent
	 * @return Operation success or failure
	 */
	public boolean handle(Token token) {
		RequestHandler handler;
		
		if(token == null) {
			handler = new DeniedHandler(new DeniedToken("Fuck you"), client);
		}
		else {
			int request = token.message();

			if(request == Messages.LOGIN) {
				if(login == null)
					login = new LoginHandler(token, client);
				handler = login;
			}
			else if(request == Messages.REGISTER_USER)
				handler = new RegistrationHandler(token, client);
			else if(request == Messages.LOGOUT) 
				handler = new LogoutHandler(token, client);
			else if(request == Messages.SEND_IMAGE) 
				handler = new SendImageHandler(token, client);
			else if(request == Messages.SEND_THUMBNAILS) 
				handler = new SendThumbnailsHandler(token, client);
			else if(request == Messages.UPDATE_TAGS) 
				handler = new UpdateTagsHandler(token, client);

			else 
				handler = new DeniedHandler(new DeniedToken("Fuck you"), client);
		}
		
		return handler.dealWithIt();
	}
	
}

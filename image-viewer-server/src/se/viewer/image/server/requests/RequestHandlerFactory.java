package se.viewer.image.server.requests;

import se.viewer.image.server.ClientConnection;
import se.viewer.image.tokens.Messages;
import se.viewer.image.tokens.Token;

public class RequestHandlerFactory {

	private ClientConnection client;
	private RequestHandler handler;
	
	private LoginHandler login;
	
	public RequestHandlerFactory(ClientConnection client) {
		this.client = client;
	}
	
	public boolean handle(Token token) {
		int request = token.message();
		
		if(request == Messages.LOGIN) {
			if(login == null)
				login = new LoginHandler(token, client);
			handler = login;
		}
		else if(request == Messages.LOGOUT) {
			handler = new LogoutHandler(token, client);
		}
		else if(request == Messages.SEND_IMAGE) {
			handler = new SendImageHandler(token, client);
		}
		else if(request == Messages.SEND_THUMBNAILS) {
			handler = new SendThumbnailsHandler(token, client);
		}
		else if(request == Messages.UPDATE_TAGS) {
			handler = new UpdateTagsHandler(token, client);
		}
		
		return handler.dealWithIt();
	}
	
}

package se.viewer.image.server.requests;

import java.io.ObjectOutputStream;

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
		ObjectOutputStream stream = client.getStream();
		
		if(request == Messages.LOGIN) {
			if(login == null)
				login = new LoginHandler(token, stream, client);
			handler = login;
		}
		else if(request == Messages.LOGOUT) {
			handler = new LogoutHandler(token, stream, client);
		}
		else if(request == Messages.SEND_IMAGE) {
			handler = new SendImageHandler(token, stream, client);
		}
		else if(request == Messages.SEND_THUMBNAILS) {
			handler = new SendThumbnailsHandler(token, stream, client);
		}
		else if(request == Messages.UPDATE_TAGS) {
			handler = new UpdateTagsHandler(token, stream, client);
		}
		
		return handler.dealWithIt();
	}
	
}

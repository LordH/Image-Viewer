package se.viewer.image.server.requests;

import se.viewer.image.database.GalleryInterface;
import se.viewer.image.server.ClientConnection;
import se.viewer.image.tokens.Messages;

public class RequestHandlerFactory {

	private static RequestHandlerInterface handler;
	private static GalleryInterface server;
	private static String user;
	
	public static RequestHandlerInterface getHandler(int request, ClientConnection client) {
		handler = null;
		
		if(request == Messages.LOGOUT) {
			handler = new LogoutHandler(client);
		}
		else if(request == Messages.SEND_IMAGE) {
			server = client.getImageServer();
			handler = new SendImageHandler(server);
		}
		else if(request == Messages.SEND_THUMBNAILS) {
			server = client.getImageServer();
			handler = new SendThumbnailsHandler(server);
		}
		else if(request == Messages.UPDATE_TAGS) {
			user = client.getClientName();
			handler = new UpdateTagsHandler(user);
		}
		
		return handler;
	}
	
}

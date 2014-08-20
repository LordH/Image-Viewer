package se.viewer.image.server.requests;

import java.io.IOException;

import se.viewer.image.containers.Image;
import se.viewer.image.database.GalleryInterface;
import se.viewer.image.server.ClientConnection;
import se.viewer.image.tokens.SendImageToken;
import se.viewer.image.tokens.Token;

public class SendImageHandler extends RequestHandler {
	
	private GalleryInterface imageServer;

	public SendImageHandler(Token token, ClientConnection client) {
		super(token, client);
		imageServer = client.getImageServer();
	}
	
	@Override
	public boolean dealWithIt() {
		int dir = ((SendImageToken) token).direction();
		Image image;
		if(dir != 0)
			image = imageServer.getImage(dir);
		else
			image = imageServer.getImage(((SendImageToken) token).getImage());
		
		try {
			oos.writeObject(image);
			oos.flush();
			return true;
		} catch (IOException e) {
			System.err.println("Could not send image.");
			e.printStackTrace();
		}
		return false;
	}
}

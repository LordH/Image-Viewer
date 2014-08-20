package se.viewer.image.server.requests;

import java.io.IOException;
import java.io.ObjectOutputStream;

import se.viewer.image.containers.Image;
import se.viewer.image.database.GalleryInterface;
import se.viewer.image.server.ClientConnection;
import se.viewer.image.tokens.SendImageToken;
import se.viewer.image.tokens.Token;

public class SendImageHandler extends RequestHandler {
	
	private GalleryInterface imageServer;

	public SendImageHandler(Token token, ObjectOutputStream oos, ClientConnection client) {
		super(token, oos, client);
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

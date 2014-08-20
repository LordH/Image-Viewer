package se.viewer.image.server.requests;

import java.io.IOException;

import se.viewer.image.containers.Image;
import se.viewer.image.server.ClientConnection;
import se.viewer.image.tokens.SendImageToken;
import se.viewer.image.tokens.Token;

/**
 * Handler for sending images
 * @author Harald Brege
 */
public class SendImageHandler extends RequestHandler {
	
	public SendImageHandler(Token token, ClientConnection client) {
		super(token, client);
	}
	
	@Override
	public boolean dealWithIt() {
		int dir = ((SendImageToken) token).direction();
		Image image;
		if(dir != 0)
			image = gallery.getImage(dir);
		else
			image = gallery.getImage(((SendImageToken) token).getImage());
		
		try {
			stream.writeObject(image);
			stream.flush();
			return true;
		} catch (IOException e) {
			System.err.println("Could not send image.");
			e.printStackTrace();
		}
		return false;
	}
}

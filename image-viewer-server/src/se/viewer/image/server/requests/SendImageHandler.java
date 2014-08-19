package se.viewer.image.server.requests;

import java.io.IOException;
import java.io.ObjectOutputStream;

import se.viewer.image.containers.Image;
import se.viewer.image.database.GalleryInterface;
import se.viewer.image.tokens.SendImageToken;
import se.viewer.image.tokens.Token;

public class SendImageHandler implements RequestHandlerInterface {

	private GalleryInterface imageServer;
	
	public SendImageHandler(GalleryInterface imageServer) {
		this.imageServer = imageServer;
	}
	
	@Override
	public boolean dealWithIt(Token token, ObjectOutputStream outStream) {
		int dir = ((SendImageToken) token).direction();
		Image image;
		if(dir != 0)
			image = imageServer.getImage(dir);
		else
			image = imageServer.getImage(((SendImageToken) token).getImage());
		
		try {
			outStream.writeObject(image);
			outStream.flush();
			return true;
		} catch (IOException e) {
			System.err.println("Could not send image.");
			e.printStackTrace();
		}
		return false;
	}
}

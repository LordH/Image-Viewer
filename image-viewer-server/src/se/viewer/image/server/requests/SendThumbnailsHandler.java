package se.viewer.image.server.requests;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import se.viewer.image.containers.Tag;
import se.viewer.image.containers.Thumbnail;
import se.viewer.image.database.GalleryInterface;
import se.viewer.image.tokens.SendThumbnailsToken;
import se.viewer.image.tokens.Token;

public class SendThumbnailsHandler implements RequestHandlerInterface {

	private GalleryInterface server;
	
	public SendThumbnailsHandler(GalleryInterface server) {
		this.server = server;
	}
	
	@Override
	public boolean dealWithIt(Token token, ObjectOutputStream outStream) {
		try {
			Tag tag = ((SendThumbnailsToken) token).getTag();
			ArrayList<Thumbnail> list = server.getThumbnails(tag);
			ArrayList<Tag> tags = server.getTags(tag);
			outStream.writeObject(list);
			outStream.writeObject(tags);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}

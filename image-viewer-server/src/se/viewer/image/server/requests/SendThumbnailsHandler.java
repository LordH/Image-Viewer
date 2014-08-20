package se.viewer.image.server.requests;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import se.viewer.image.containers.Tag;
import se.viewer.image.containers.Thumbnail;
import se.viewer.image.database.GalleryInterface;
import se.viewer.image.server.ClientConnection;
import se.viewer.image.tokens.SendThumbnailsToken;
import se.viewer.image.tokens.Token;

public class SendThumbnailsHandler extends RequestHandler {

	private GalleryInterface server;

	public SendThumbnailsHandler(Token token, ObjectOutputStream oos, ClientConnection client) {
		super(token, oos, client);
	}
	
	@Override
	public boolean dealWithIt() {
		try {
			Tag tag = ((SendThumbnailsToken) token).getTag();
			ArrayList<Thumbnail> list = server.getThumbnails(tag);
			ArrayList<Tag> tags = server.getTags(tag);
			
			oos.writeObject(list);
			oos.writeObject(tags);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}

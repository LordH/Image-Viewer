package se.viewer.image.server.requests;

import java.io.IOException;
import java.util.ArrayList;

import se.viewer.image.containers.Tag;
import se.viewer.image.containers.Thumbnail;
import se.viewer.image.server.ClientConnection;
import se.viewer.image.tokens.SendThumbnailsToken;
import se.viewer.image.tokens.Token;

/**
 * Handler for client thumbnail requests
 * @author Harald Brege
 */
public class SendThumbnailsHandler extends RequestHandler {

	public SendThumbnailsHandler(Token token, ClientConnection client) {
		super(token, client);
	}
	
	@Override
	public boolean dealWithIt() {
		try {
			Tag tag = ((SendThumbnailsToken) token).getTag();
			ArrayList<Thumbnail> list = gallery.getThumbnails(tag);
			ArrayList<Tag> tags = gallery.getTags(tag);
			
			stream.writeObject(list);
			stream.writeObject(tags);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}

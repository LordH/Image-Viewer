package se.viewer.image.server.requests;

import java.io.IOException;
import java.util.ArrayList;

import se.viewer.image.containers.Tag;
import se.viewer.image.database.DatabaseSelector;
import se.viewer.image.server.ClientConnection;
import se.viewer.image.tokens.Token;
import se.viewer.image.tokens.UpdateTagsToken;

/**
 * Handler for client update tags requests
 * @author Harald Brege
 */
public class UpdateTagsHandler extends RequestHandler {

	private String user;

	public UpdateTagsHandler(Token token, ClientConnection client) {
		super(token, client);
		user = client.getClientName();
	}
	
	@Override
	public boolean dealWithIt() {
		
		UpdateTagsToken update = (UpdateTagsToken) token;
		boolean success = DatabaseSelector.getDB().updateTags(update.getTags(), update.getImage(), user);
		ArrayList<Tag> tags = DatabaseSelector.getDB().getTags(update.getImage(), user);
		try {
			stream.writeObject(tags);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return success;
	}

}
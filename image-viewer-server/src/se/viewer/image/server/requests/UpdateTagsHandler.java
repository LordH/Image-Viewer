package se.viewer.image.server.requests;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import se.viewer.image.containers.Tag;
import se.viewer.image.database.DatabaseSelector;
import se.viewer.image.tokens.Token;
import se.viewer.image.tokens.UpdateTagsToken;

public class UpdateTagsHandler implements RequestHandlerInterface {

	private String user;
	
	public UpdateTagsHandler(String user) {
		this.user = user;
	}
	
	@Override
	public boolean dealWithIt(Token token, ObjectOutputStream outStream) {
		
		UpdateTagsToken update = (UpdateTagsToken) token;
		boolean success = DatabaseSelector.getDB().updateTags(update.getTags(), update.getImage(), user);
		ArrayList<Tag> tags = DatabaseSelector.getDB().getTags(update.getImage(), user);
		try {
			outStream.writeObject(tags);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return success;
	}

}
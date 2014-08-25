package se.viewer.image.tokens;

import java.util.ArrayList;

import se.viewer.image.containers.Tag;
import se.viewer.image.containers.Thumbnail;

/**
 * Token for informing of login success and delivering setup
 * @author Harald Brege
 */
public class LoginSuccessToken implements Token {
	
	private static final long serialVersionUID = 1123769669272424764L;

	private ArrayList<Tag> tags;
	private ArrayList<Thumbnail> thumbs;
	
	public LoginSuccessToken(ArrayList<Tag> tags, ArrayList<Thumbnail> thumbs) {
		this.tags = tags;
		this.thumbs = thumbs;
	}
	
	@Override
	public int message() {
		return Messages.LOGIN_SUCCESS;
	}

	public ArrayList<Tag> getTags() {
		return tags;
	}
	
	public DeliverThumbnailsToken getThumbs() {
		return new DeliverThumbnailsToken(new Tag("tagme", "special"), thumbs);
	}
}

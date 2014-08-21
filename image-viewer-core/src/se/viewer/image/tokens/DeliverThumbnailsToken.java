package se.viewer.image.tokens;

import java.util.ArrayList;

import se.viewer.image.containers.Tag;
import se.viewer.image.containers.Thumbnail;

/**
 * Token for delivering thumbnails to the client
 * @author Harald Brege
 */
public class DeliverThumbnailsToken implements Token {

	private static final long serialVersionUID = 3806205623143624029L;
	private Tag tag;
	private ArrayList<Thumbnail> thumbs;

	public DeliverThumbnailsToken(Tag tag, ArrayList<Thumbnail> thumbs) {
		this.tag = tag;
		this.thumbs = thumbs;
	}
	
	@Override
	public int message() {
		return Messages.DELIVER_THUMBNAILS;
	}

	public Tag getTag() {
		return tag;
	}
	
	public ArrayList<Thumbnail> getThumbnails() {
		return thumbs;
	}
	
}

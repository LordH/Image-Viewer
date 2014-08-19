package se.viewer.image.tokens;

import se.viewer.image.structure.Tag;

public class SendThumbnailsToken implements Token {
	
	private static final long serialVersionUID = -846603845359522256L;
	private Tag tag;
	
	public SendThumbnailsToken(Tag tag) {
		this.tag = tag;
	}
	
	@Override
	public int message() {
		return Messages.SEND_THUMBNAILS;
	}

	public Tag getTag() {
		return tag;
	}
}

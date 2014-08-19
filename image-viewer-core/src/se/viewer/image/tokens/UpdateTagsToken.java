package se.viewer.image.tokens;

import java.util.ArrayList;

import se.viewer.image.containers.Tag;

public class UpdateTagsToken implements Token {
	
	private static final long serialVersionUID = -7986448348827125957L;
	private String image;
	private ArrayList<Tag> tags;
	
	public UpdateTagsToken(String image, ArrayList<Tag> tags) {
		this.image = image;
		this.tags = tags;
	}
	
	@Override
	public int message() {
		return Messages.UPDATE_TAGS;
	}

	public String getImage() {
		return image;
	}
	
	public ArrayList<Tag> getTags() {
		return tags;
	}
}

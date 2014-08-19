package se.viewer.image.containers;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Thumbnail extends Image {
	
	private static final long serialVersionUID = -1560452351825703676L;

	public Thumbnail(String name, BufferedImage image, ArrayList<Tag> tags) {
		super(name, image, tags);
	}

	public Thumbnail(String name, byte[] image, ArrayList<Tag> tags) {
		super(name, image, tags);
	}
}

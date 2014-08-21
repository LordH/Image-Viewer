package se.viewer.image.tokens;

import se.viewer.image.containers.Image;

/**
 * Token for delivering an image from the server
 * @author Harald Brege
 */
public class DeliverImageToken implements Token {
	
	private static final long serialVersionUID = 6265215858899714811L;
	private Image image;

	public DeliverImageToken(Image image) {
		this.image = image;
	}
	
	@Override
	public int message() {
		return Messages.DELIVER_IMAGE;
	}

	public Image getImage() {
		return image;
	}
}

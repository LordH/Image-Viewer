package se.viewer.image.database;

public class GallerySelector {

	public static GalleryInterface getGallery(String user) {
		return new ImageGallery(user);
	}
}

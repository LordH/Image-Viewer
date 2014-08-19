package se.viewer.image.database;

import java.util.ArrayList;

import se.viewer.image.containers.Image;
import se.viewer.image.containers.Tag;
import se.viewer.image.containers.Thumbnail;

public interface GalleryInterface {

	public static final int MODE_SERVER = 0;

	public abstract void setMode(int mode);

	/**
	 * Called to get an image by name
	 * @param image The name of the desired image
	 * @return The image
	 */
	public abstract Image getImage(String image);

	/**
	 * Fetches the next image in the desired direction.
	 * @param dir Set to 1 for forwards and -1 for backwards
	 * @return The next image
	 */
	public abstract Image getImage(int dir);

	/**
	 * Called to get a list of thumbnails of images tagged with the provided tag
	 * @param tag The tag
	 * @return A list of up to 20 thumbnails of images with the tag
	 */
	public abstract ArrayList<Thumbnail> getThumbnails(Tag tag);

	/**
	 * Called to get all tags that are tagged along with the provided tag
	 * @param tag The tag to check for associated tags
	 * @return A list of 20 tags associated with the tag provided
	 */
	public abstract ArrayList<Tag> getTags(Tag tag);

}
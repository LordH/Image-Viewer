package se.viewer.image.gui.viewer;

import java.util.ArrayList;

import se.viewer.image.containers.Image;
import se.viewer.image.containers.Tag;
import se.viewer.image.tokens.DeliverThumbnailsToken;

public interface ViewerInterface {

	public static final String IMAGE = "image";
	public static final String THUMBNAILS = "thumbnails";
	public static final String LOADING = "loading";

	public abstract void displayImage();

	public abstract void setImage(Image image);

	public abstract void clearImage();

	public abstract void setTags(ArrayList<Tag> tags);

	public abstract void displayLoading();

	public abstract void setUpdateProgress(String message, int percent);

	public abstract void displayThumbnails();

	public abstract void setThumbnails(DeliverThumbnailsToken token);

}
package se.viewer.image.gui.components.viewer;

import java.util.ArrayList;

import se.viewer.image.containers.Image;
import se.viewer.image.containers.Tag;
import se.viewer.image.tokens.DeliverThumbnailsToken;

public interface ViewerInterface {

	public static final String IMAGE = "image";
	public static final String THUMBNAILS = "thumbnails";
	public static final String LOADING = "loading";

	public void displayImage();

	public void setImage(Image image);

	public void clearImage();

	public void setTags(ArrayList<Tag> tags);

	public void displayLoading();

	public void setUpdateProgress(String message, int percent);

	public void displayThumbnails();

	public void setThumbnails(DeliverThumbnailsToken token);

}
package se.viewer.image.database;

import java.util.ArrayList;

import se.viewer.image.containers.Image;
import se.viewer.image.containers.Tag;

/**
 * Interface that describes a database connection
 * @author Harald Brege
 */
public interface DatabaseInterface {
	
	/**
	 * Called to shut down the database connection
	 * @return Operation success or failure
	 */
	public abstract boolean shutDown();
	
	//=======================================
	//	USER HANDLING METHODS
	//---------------------------------------
	
	public abstract boolean authenticateUser(String user, String password);

	public abstract boolean registerUser(String name, String password);
	
	//=======================================
	//	IMAGE HANDLING METHODS
	//---------------------------------------

	public abstract ArrayList<String> getAllImages();

	public abstract boolean add(Image image);

	public abstract boolean add(ArrayList<Image> images);
	
	//=======================================
	//	TAG HANDLING METHODS
	//---------------------------------------

	public abstract ArrayList<Tag> getAllTags();

	public abstract ArrayList<Tag> getTags(String image);

	public abstract ArrayList<String> getTaggedList(String tag);
	
	public abstract boolean updateTags(Image image);

	public abstract boolean updateTags(ArrayList<Tag> tags, String image);
	

}
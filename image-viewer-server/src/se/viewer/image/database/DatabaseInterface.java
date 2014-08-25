package se.viewer.image.database;

import java.util.ArrayList;

import se.viewer.image.containers.Image;
import se.viewer.image.containers.Tag;

public interface DatabaseInterface {
	
	/**
	 * Called to shut down the database connection
	 * @return Operation success or failure
	 */
	public abstract boolean shutDown();
	
	//=======================================
	//	USER HANDLING METHODS
	//---------------------------------------
	
	/**
	 * Called to check user credentials with database records
	 * @param user The user's name
	 * @param password The user's password
	 * @return Authentication success or failure
	 */
	public abstract boolean authenticateUser(String user, String password);

	/**
	 * Called to create a new user with the provided credentials
	 * @param name The new user's user name
	 * @param password The new user's password
	 * @return If the user could be created or not
	 */
	public abstract boolean registerUser(String name, String password);

	/**
	 * Called to get the database user id for a given user
	 * @param user The user's name
	 * @return The user's id number, or 0 if no such user exists
	 */
	public abstract int getUserID(String user);
	
	//=======================================
	//	IMAGE HANDLING METHODS
	//---------------------------------------

	/**
	 * Called to get the database id number of a certain image
	 * @param image The image in question
	 * @param user The name of the user that owns the image
	 * @return The image id number, or 0 if no such image exists
	 */
	public abstract int getImageID(String image, String user);

	/**
	 * Called to check if a certain image exists or not
	 * @param image The name of the image
	 * @param user The name of the user that owns the image
	 * @return Whether the image exists in the database or not
	 */
	public abstract boolean existsImage(String image, String user);

	/**
	 * Returns a list of all images stored for a certain user
	 * @param user The name of the user
	 * @return A list of all the names of the user's images
	 */
	public abstract ArrayList<String> getAllImages(String user);

	/**
	 * Adds an image and its tags to the database
	 * @param image The image to be added
	 * @param user The name of the user that owns the image
	 * @return Operation success or failure
	 */
	public abstract boolean add(Image image, String user);
	
	/**
	 * Called to add many images at once
	 * @param images A list of all images to be added
	 * @param user The name of the user that owns the images
	 * @return Operation success or failure
	 */
	public abstract boolean add(ArrayList<Image> images, String user);

	/**
	 * Called to get all tags stored in the database for a certain image
	 * @param image The image in question
	 * @param user The name of the user that owns the image
	 * @return A list of all tags for the image
	 */
	public abstract ArrayList<Tag> getTags(String image, String user);
	
	/**
	 * Called to update the database record of the tags for an image
	 * @param image The image in question
	 * @param user The name of the user that owns the image
	 * @return Operation success or failure
	 */
	public abstract boolean updateTags(Image image, String user);

	/**
	 * Called to update the tags for an image
	 * @param tags The current tags for the image
	 * @param image The name of the image
	 * @param user The name of the user that owns the image
	 * @return Operation success or failure
	 */
	public abstract boolean updateTags(ArrayList<Tag> tags, String image,
			String user);
	
	//=======================================
	//	TAG HANDLING METHODS
	//---------------------------------------
	
	/**
	 * Called to get the database id number of a certain tag
	 * @param tag The name of the tag in question
	 * @param user The name of the user that owns the tag
	 * @return The tag id number, or 0 if no such tag exists
	 */
	public abstract int getTagID(String tag, String user);

	public abstract ArrayList<Tag> getAllTags(String user);
	
	/**
	 * Called to check whether a certain tag exists in the database or not
	 * @param tag The name of the tag
	 * @param user The name of the user that owns the tag
	 * @return Whether the tag exists or not
	 */
	public abstract boolean existsTag(String tag, String user);

	/**
	 * Called to add a new tag to the database
	 * @param tag The tag to be added
	 * @param user The name of the user that owns the tag
	 * @return Operation success or failure
	 */
	public abstract boolean add(Tag tag, String user);

//	/**
//	 * Called to get a list of the most common tags that come together with a certain tag
//	 * @param tag The tag whose common tags are required
//	 * @param user The user that owns the tags
//	 * @return A list of the up to 20 most common tag
//	 */
//	public abstract ArrayList<Tag> getCoTags(String tag, String user);
	
//	/**
//	 * Called to get the number of images tagged with a certain tag
//	 * @param tag The tag in question
//	 * @param user The name of the user that owns the tag
//	 * @return The number of tagged images
//	 */
//	public abstract int getTaggedCount(String tag, String user);

	/**
	 * Called to get a list of all images a user has that are tagged with a certain tag
	 * @param tag The name of the tag
	 * @param user The name of the user that owns the images and the tag
	 * @return A list of all images tagged with the tag
	 */
	public abstract ArrayList<String> getTaggedList(String tag, String user);
	
	//=======================================
	//	TAG TYPE HANDLING METHODS
	//---------------------------------------

	/**
	 * Called to get the tag type id for a certain tag type
	 * @param type The tag type in question
	 * @param user The name of the user that owns the tag type
	 * @return The tag type id, or 0 if no such tag exists
	 */
	public abstract int getTagTypeID(String type, String user);

	/**
	 * Checks whether or not a certain tag type exists
	 * @param type The name of the tag type
	 * @param user The name of the user that owns the tag type
	 * @return Whether the tag type exists or not
	 */
	public abstract boolean existsTagType(String type, String user);

	/**
	 * Called to add a new tag type to the database
	 * @param tagType The name of the tag type in question
	 * @param user The name of the user that owns the tag type
	 * @return Operation success or failure
	 */
	public abstract boolean add(String tagType, String user);

}
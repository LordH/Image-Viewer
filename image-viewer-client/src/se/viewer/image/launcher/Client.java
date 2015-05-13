package se.viewer.image.launcher;

import java.util.ArrayList;

import se.viewer.image.communication.ServerCommunicator;
import se.viewer.image.containers.Image;
import se.viewer.image.containers.Tag;
import se.viewer.image.gui.factory.GUIHandlerInterface;
import se.viewer.image.gui.factory.GUISelector;
import se.viewer.image.gui.viewer.ViewerInterface;
import se.viewer.image.tokens.DeliverThumbnailsToken;

/**
 * Main class that handles for the client application
 * @author Harald Brege
 */
public class Client {

	public static final int IMAGE_DOWNLOAD_STARTED = 0;
	public static final int IMAGE_BUFFER_PROGRESS = 1;
	public static final int IMAGE_BUFFER_COMPLETE = 2;
	
	private static Client instance;
	
	private GUIHandlerInterface factory;
	private ViewerInterface panel;
	
	private Client() {
		factory = GUISelector.getFactory();
	}
	
	/**
	 * Called to get the client instance
	 * @return The instance of Client
	 */
	public static Client instance() {
		if(instance == null)
			instance = new Client();
		return instance;
	}
	
	//=======================================
	//	CONNECTION REQUESTS
	//---------------------------------------
	
	/**
	 * Runs the main view of the application
	 */
	public void start() {
//		ServerCommunicator.instance().connect("192.168.1.3", 2106);
		loginMode();
	}
	
	/**
	 * Called to set the title of the frame
	 * @param title String to be set into the title
	 */
	public void setTitle(String title) {
		factory.setTitle("Image Viewer - " + title);
	}
	
	/**
	 * Called to activate the main viewer mode of the application
	 */
	public void viewerMode() {
		panel = factory.viewerMode();
	}
	
	/**
	 * Called to activate the login mode of the application
	 */
	public void loginMode() {
		factory.loginMode();
	}
	
	//=======================================
	//	IMAGE REQUESTS
	//---------------------------------------
	
	/**
	 * Called to get the next or previous image to display
	 * @param dir 1 for the next image or -1 for the previous
	 */
	public void getImage(int dir) {
		ServerCommunicator.instance().getImage(dir);
	}
	
	/**
	 * Called to get an image to display
	 * @param name The name of the image to be displayed
	 */
	public void getImage(String name) {
		ServerCommunicator.instance().getImage(name);
	}
	
	//=======================================
	//	INFORMATION DELIVERY
	//---------------------------------------
	
	/**
	 * Called to inform the user of server connection success or failure
	 * @param success If a connection to the server has been established or not
	 * @param host The address of the host
	 */
	public void connectionSuccess(boolean success, String host) {
		if(success)
			factory.loginMessage("Connected to server at " + host);
		else
			factory.loginMessage("Could not connect to server at " + host);
	}
	
	/**
	 * Called to indicate whether the user has successfully logged in to the server or not
	 * @param success Login success or failure
	 * @param attemptsLeft How many more attempts to log in can be made
	 */
	public void loginSuccess(boolean success, int attemptsLeft) {
		if(!success)
			factory.loginMessage("Login failed! " + attemptsLeft + " attempts left.");
		else {
			factory.loginSuccessful();
			viewerMode();
		}
	}
	
	/**
	 * Called to indicate whether an attempt to register a new user has been successful or not
	 * @param success Registration success or failure
	 */
	public void registrationSuccess(boolean success) {
		if(success)
			factory.loginMessage("Successfully created new user!");
		else
			factory.loginMessage("Failed to create user!");
	}

	/**
	 * Called to deliver a new image to be displayed in the viewer
	 * @param image The image to be displayed
	 */
	public void deliverImage(Image image) {
		panel.setImage(image);
	}
	
	/**
	 * Called to deliver a new list of tags to be displayed
	 * @param tags The tags to be displayed
	 */
	public void deliverTags(ArrayList<Tag> tags) {
		panel.setTags(tags);
	}

	/**
	 * Called to deliver a list of thumbnails to be displayed
	 * @param thumbs The thumbnails to be displayed
	 */
	public void deliverThumbnails(DeliverThumbnailsToken token) {
		panel.setThumbnails(token);
//		panel.setTags(new ArrayList<Tag>());
		panel.displayThumbnails();
	}
	
	/**
	 * Called to deliver information on the progress of a process
	 * @param percent
	 * @param removeImage
	 * @param source
	 */
	public void deliverUpdateProgress(int message, int percent) {
		switch (message) {
		case IMAGE_DOWNLOAD_STARTED :
			panel.clearImage();
			panel.setUpdateProgress("Downloading image:", percent);
			panel.displayLoading();
			break;
			
		case IMAGE_BUFFER_PROGRESS :
			panel.setUpdateProgress("Buffering image:", percent);
			break;
			
		case IMAGE_BUFFER_COMPLETE :
			panel.displayImage();
			break;
		}
	}
	

}

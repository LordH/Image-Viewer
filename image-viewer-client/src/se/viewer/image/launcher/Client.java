package se.viewer.image.launcher;

import java.util.ArrayList;

import javax.swing.JFrame;

import se.viewer.image.containers.Image;
import se.viewer.image.containers.Tag;
import se.viewer.image.containers.Thumbnail;
import se.viewer.image.gui.GUIFactoryInterface;
import se.viewer.image.gui.GUIFactorySelector;
import se.viewer.image.gui.ApplicationPanel;
import se.viewer.image.gui.LoginFrame;

/**
 * Main class that handles for the client application
 * @author Harald Brege
 */
public class Client {

	public static final int SOURCE_SERVER = 0;
	public static final int SOURCE_LOCAL = 1;
	
	public static final int IMAGE_DOWNLOAD_STARTED = 0;
	public static final int IMAGE_BUFFER_PROGRESS = 1;
	public static final int IMAGE_BUFFER_COMPLETE = 2;
	
	private static Client instance;
	
	private GUIFactoryInterface factory;
	
	private JFrame frame;
	private LoginFrame login;
	private ApplicationPanel panel;
	
	private int source;
	
	private Client() {
		setImageSource(SOURCE_SERVER);
		factory = GUIFactorySelector.getFactory();
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
		ServerCommunicator.instance().connect("192.168.1.3", 2106);
		loginMode();
	}
	
	/**
	 * Sets the image handler to either fetch local images or from the server
	 * @param source SOURCE_SERVER or SOURCE_LOCAL
	 */
	public void setImageSource(int source) {
		this.source = source;
	}
	
	/**
	 * Called to set the title of the frame
	 * @param title String to be set into the title
	 */
	public void setFrameTitle(String title) {
		frame.setTitle("Image Viewer - " + title);
	}
	
	/**
	 * Called to activate the main viewer mode of the application
	 */
	public void viewerMode() {
		frame = factory.getFrame();
		panel = factory.setViewerMode(frame);
	}
	
	/**
	 * Called to activate the login mode of the application
	 */
	public void loginMode() {
		login = factory.getLoginFrame();
	}
	
	//=======================================
	//	IMAGE REQUESTS
	//---------------------------------------
	
	/**
	 * Called to get the next or previous image to display
	 * @param dir 1 for the next image or -1 for the previous
	 */
	public void getImage(int dir) {
		if(source == SOURCE_SERVER) {
			ServerCommunicator.instance().getImage(dir);
		}
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
	 * Called to indicate whether the user has successfully logged in to the server or not
	 * @param success Login success or failure
	 * @param attemptsLeft How many more attempts to log in can be made
	 */
	public void loginSuccess(boolean success, int attemptsLeft) {
		if(!success)
			login.setMessage("Login failed! " + attemptsLeft + " attempts left.");
		else {
			login.success();
			viewerMode();
		}
	}
	
	/**
	 * Called to indicate whether an attempt to register a new user has been successful or not
	 * @param success Registration success or failure
	 */
	public void registrationSuccess(boolean success) {
		if(success)
			login.setMessage("Successfully created new user!");
		else
			login.setMessage("Failed to create user!");
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
	public void deliverThumbnails(ArrayList<Thumbnail> thumbs) {
		panel.setThumbnails(thumbs);
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

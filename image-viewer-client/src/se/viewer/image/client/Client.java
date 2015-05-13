package se.viewer.image.client;

import java.util.ArrayList;

import se.viewer.image.communication.ServerCommunicator;
import se.viewer.image.containers.Image;
import se.viewer.image.containers.Tag;
import se.viewer.image.gui.components.viewer.ViewerInterface;
import se.viewer.image.gui.handlers.GUIFactory;
import se.viewer.image.gui.handlers.LoginHandlerInterface;
import se.viewer.image.gui.handlers.ViewerHandlerInterface;
import se.viewer.image.tokens.DeliverThumbnailsToken;

/**
 * Main class that handles for the client application
 * @author Harald Brege
 */
public class Client {

	public static final int IMAGE_DOWNLOAD_STARTED = 0;
	public static final int IMAGE_BUFFER_PROGRESS = 1;
	public static final int IMAGE_BUFFER_COMPLETE = 2;
	
	public static final int MODE_LOGIN = 0;
	public static final int MODE_VIEWER = 1;
	private int mode;
	
	private static Client instance;
	
	private ViewerHandlerInterface vHandler;
	private LoginHandlerInterface lHandler;
	
	private ViewerInterface panel;
	
	private Client() {
		vHandler = GUIFactory.getViewerHandler();
		lHandler = GUIFactory.getLoginHandler();
		System.out.println("GUI initialized");
	}
	
	//=======================================
	//	LAUNCHER
	//---------------------------------------
	
	/**
	 * Launches the main client application
	 * @param args Not in use
	 */
	public static void main(String[] args) {
		instance().getLoginHandler().setupFrame();
		System.out.println("Client application fully initialized");
	}
	
	//=======================================
	//	SERVICE ACCESS
	//---------------------------------------
		
	/**
	 * Called to get the client instance
	 * @return The instance of Client
	 */
	public static Client instance() {
		if(instance == null) {
			instance = new Client();
			System.out.println("Client instance created");
		}
		return instance;
	}
	
	public ViewerHandlerInterface getViewerHandler() {
		if(mode == MODE_VIEWER)
			return vHandler;
		else
			return null;
	}
	
	public LoginHandlerInterface getLoginHandler() {
		if(mode == MODE_LOGIN)
			return lHandler;
		else
			return null;
	}
		
	//=======================================
	//	SET MODE
	//---------------------------------------
		
	/**
	 * Called to activate the viewer mode of the application
	 */
	public void viewerMode() {
		mode = MODE_VIEWER;
		lHandler.removeFrame();
	}
	
	/**
	 * Called to activate the login mode of the application
	 */
	public void loginMode() {
		mode = MODE_LOGIN;
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
	 * Called to indicate whether an attempt to register a new user has been successful or not
	 * @param success Registration success or failure
	 */
	public void registrationSuccess(boolean success) {
		if(success)
			lHandler.setMessage("Successfully created new user!");
		else
			lHandler.setMessage("Failed to create user!");
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

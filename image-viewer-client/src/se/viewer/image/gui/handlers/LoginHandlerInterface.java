package se.viewer.image.gui.handlers;

/**
 * Interface for classes that handle the GUI for login
 * @author Harald Brege
 */
public interface LoginHandlerInterface {

	/**
	 * Creates the login GUI frame
	 * @return Success or failure of operation
	 */
	public boolean setupFrame();

	/**
	 * Removes the login GUI frame
	 * @return Success or failure of operation
	 */
	public boolean removeFrame();
	
	/**
	 * Delivers a message, e.g. from the server, to the user
	 * @param message The message to be delivered
	 * @return Delivery success or failure
	 */
	public boolean setMessage(String message);
		
}
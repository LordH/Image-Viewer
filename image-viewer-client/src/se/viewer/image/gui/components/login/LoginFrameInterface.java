package se.viewer.image.gui.components.login;


/**
 * Interface for login frames
 * @author Harald Brege
 */
public interface LoginFrameInterface {
	
	/**
	 * Sets up the components and the login frame
	 * @return Operation success or failure
	 */
	public boolean setup();
	
	/**
	 * Removes up the components and the login frame
	 * @return Operation success or failure
	 */
	public boolean remove();
	
	/**
	 * Displays a message to the user
	 * @param message The message to be shown
	 * @return Operation success or failure
	 */
	public boolean setMessage(String message);
	
}
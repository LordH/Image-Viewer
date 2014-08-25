package se.viewer.image.gui;

public interface LoginInterface {

	/**
	 * Called to set the message displayed to the user
	 * @param message The message to be shown
	 */
	public abstract void setMessage(String message);

	/**
	 * Called when the server confirms successful login do dispose the login frame
	 */
	public abstract void success();

}
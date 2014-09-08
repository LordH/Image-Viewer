package se.viewer.image.gui.login;

public interface LoginInterface {

	/**
	 * Called to set the message displayed to the user
	 * @param message The message to be shown
	 */
	public abstract void setMessage(String message);
	
	public abstract void setVisible(boolean b);

}
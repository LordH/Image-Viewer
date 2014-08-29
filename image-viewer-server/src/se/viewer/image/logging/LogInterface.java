package se.viewer.image.logging;

public interface LogInterface {

	/**
	 * Called to inform the log which user is on the client
	 * @param name Name of the user for the log
	 */
	public abstract void userLoggedIn(String user);

	public abstract void newLogMessage(String message);

	public abstract void shutdown();

}
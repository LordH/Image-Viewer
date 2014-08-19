package se.viewer.image.launcher;

/**
 * Class representing the main client for the application
 * @author Harald Brege
 */
public class ClientLauncher {
	
	/**
	 * Starts the client
	 * @param args No arguments are used
	 */
	public static void main (String[] args) {
		Client.instance().start();
	}
}
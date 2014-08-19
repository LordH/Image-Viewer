package se.viewer.image.gui;

import se.viewer.image.server.ClientConnection;

public interface GUIManagerInterface {

	/**
	 * Called to initialise the GUI
	 */
	public abstract void initialise();
	
	
	/**
	 * Called to add a certain client to the information display
	 * @param client The client to be added
	 */
	public abstract void add(ClientConnection client);
	
	/**
	 * Called to update the info for a client
	 * @param client The client to be updated
	 */
	public abstract void update(ClientConnection client);

	/**
	 * Called to remove a certain client from the information display
	 * @param client The client to be removed
	 */
	public abstract void remove(ClientConnection client);

}

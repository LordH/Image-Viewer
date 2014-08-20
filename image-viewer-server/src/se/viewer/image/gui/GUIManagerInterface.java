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
	 * Called to remove a certain client from the information display
	 * @param client The client to be removed
	 */
	public abstract void remove(ClientConnection client);

	/**
	 * Called to indicate which client's information should currently be displayed
	 * @param client The client whose information should be displayed
	 */
	public abstract void setFocused(ClientConnection client);
}

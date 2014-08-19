package se.viewer.image.gui;

import java.util.ArrayList;

import se.viewer.image.server.ClientConnection;

/**
 * Class for listing clients and their related information for the server gui
 * @author Harald Brege
 */
public class ClientList {

	private ArrayList<ClientConnection> clients;
	private ArrayList<InformationPanel> panels;
	private ArrayList<ClientLabel> labels;
	
	/**
	 * Creates a new empty client list
	 */
	public ClientList() {
		clients = new ArrayList<ClientConnection>();
		panels = new ArrayList<InformationPanel>();
		labels = new ArrayList<ClientLabel>();
	}
	
	//=======================================
	//	ELEMENT HANDLING METHODS
	//---------------------------------------	
	
	/**
	 * Adds a new client to the list and sets up a new information panel 
	 * and client label for it
	 * @param client THe client to be added
	 */
	public void add(ClientConnection client) {
		clients.add(client);
		panels.add(new InformationPanel(client));
		labels.add(new ClientLabel(client));
	}
	
	/**
	 * Removes a client and its information panel and label from the list
	 * @param client
	 */
	public void remove(ClientConnection client) {
		int index = clients.indexOf(client);
		clients.remove(index);
		panels.remove(index);
		labels.remove(index);
	}
	
	//=======================================
	//	INFORMATION RETRIEVAL METHODS 
	//---------------------------------------	

	/**
	 * Called to check whether the list contains a certain client or not
	 * @param client The client
	 * @return Whether it is contained in the list or not
	 */
	public boolean contains(ClientConnection client) {
		return clients.contains(client);
	}
	
	/**
	 * Called to get the information panel for a certain client
	 * @param client The client whose panel is desired
	 * @return The panel for that client
	 */
	public InformationPanel getPanel(ClientConnection client) {
		int index = clients.indexOf(client);
		return panels.get(index);
	}
	
	/**
	 * Called to get the client label for a certain client
	 * @param client The client whose label is desired
	 * @return The label for that client
	 */
	public ClientLabel getLabel(ClientLabel client) {
		int index = clients.indexOf(client);
		return labels.get(index);
	}
	
	/**
	 * Called to get a list of all client labels
	 * @return An array list containing all client labels
	 */
	public ArrayList<ClientLabel> getLabels() {
		return labels;
	}
	
	/**
	 * Called to get an iterable list of all clients in the list
	 * @return An array list of all client connections in the list
	 */
	public ArrayList<ClientConnection> getClients() {
		return clients;
	}
	
	/**
	 * Called to get the size of the list
	 * @return The number of clients in the list
	 */
	public int size() {
		return clients.size();
	}
}

package se.viewer.image.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import se.viewer.image.gui.GUISelector;

/**
 * Class for running the part of the server that accepts incoming client connections
 * @author Harald Brege
 */
public class Server implements Runnable {

	private ArrayList<ClientConnection> connections;
	private ArrayList<InetAddress> blacklist;
	
	private ServerSocket server;
	private boolean run;
	
	private static Server instance;
	
	private Server() {
		connections = new ArrayList<ClientConnection>();
		blacklist = new ArrayList<InetAddress>();	
		run = true;
		
		GUISelector.getGUI();
	}
	
	/**
	 * Called to get the singleton Server instance
	 * @return The instance of Server
	 */
	public static Server instance() {
		if(instance == null) 
			instance = new Server();
		return instance;
	}
	
	@Override
	public void run() {
		//Setting up the server socket
				
		try {
			server = new ServerSocket(2106, 10, InetAddress.getLocalHost());
			server.setSoTimeout(1000);
			
			System.out.println("+++ SERVER OPENED AT " + server.getInetAddress()
					+ ":" + server.getLocalPort() + " +++");
		} catch (IOException e) {
			System.err.println("+++ CONNECTION ERROR: " + e.toString().toUpperCase() + " +++");
			e.printStackTrace();
		}
		
		//Accepting new connections and creating handler threads for them
		while (run) {
			try {
				Socket connection = server.accept();

				if(blacklist.contains(connection.getInetAddress())) {
					System.out.println("+++ BLACKLISTED CLIENT AT  " + connection.getInetAddress() 
							+ " ATTEMPTED CONNECTION +++");
					connection.close();
				}
				else {
					ClientConnection handler = new ClientConnection(connection);
					(new Thread(handler)).start();

					connections.add(handler);
					GUISelector.getGUI().add(handler);
				}
			} catch (SocketTimeoutException e) {
				//Catches time-outs from ServerSocket.accept()
			} catch (IOException e) {
				System.err.println("+++ CONNECTION ERROR: " + e.toString().toUpperCase() + " +++");
				e.printStackTrace();
			}
		}

		//Shutting down the server orderly
		try {
			for(ClientConnection handler : connections)
				handler.disconnect();

			server.close();
			System.out.println("+++ SERVER SOCKET CLOSED +++");
			
		} catch (IOException e) {
			System.err.println("+++ CONNECTION ERROR: " + e.toString().toUpperCase() + " +++");
			e.printStackTrace();
		}
			
		System.out.println("+++ SERVER SHUTTING DOWN +++");
	}
	
	//=======================================
	//	INTERNAL SERVER REQUESTS
	//---------------------------------------
	
	/**
	 * Removes a client connection from the internal list
	 * @param client The connection to be removed
	 */
	public void disconnect(ClientConnection client) {
		GUISelector.getGUI().remove(client);
		connections.remove(client);
	}
	
	/**
	 * Adds a IP address to the blacklist
	 * @param address The IP address to be blacklisted
	 */
	public void blacklist(InetAddress address) {
		blacklist.add(address);
		System.out.println("+++ " + address.toString() + " BLACKLISTED +++");
	}
	
	/**
	 * Shuts down the server application in an orderly manner
	 */
	public void serverShutdown() {
		run = false;
	}
}
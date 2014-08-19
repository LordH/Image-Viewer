package se.viewer.image.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Observable;

import se.viewer.image.database.GalleryInterface;
import se.viewer.image.database.GallerySelector;
import se.viewer.image.server.requests.DeniedHandler;
import se.viewer.image.server.requests.LoginHandler;
import se.viewer.image.server.requests.RegisterUserHandler;
import se.viewer.image.server.requests.RequestHandlerFactory;
import se.viewer.image.server.requests.RequestHandlerInterface;
import se.viewer.image.tokens.LoginToken;
import se.viewer.image.tokens.Messages;
import se.viewer.image.tokens.Token;

/**
 * Class for handling a client connection to the server
 * @author Harald Brege
 */
public class ClientConnection extends Observable implements Runnable {
	
	private Socket client;
	private ObjectInputStream inStream;
	private ObjectOutputStream outStream;
	
	private String clientName = "UNKNOWN";
	private String clientIp;
	
	private boolean run = true;
	private boolean authenticated = false;
	private int attemptsLeft = 3;
	
	private GalleryInterface imageServer;
	private RequestHandlerInterface handler;
	private Token request;
	
	/**
	 * Opens a new connection handler that caters to the client on the specified socket
	 * @param socket A socket with a client connected to it
	 */
	public ClientConnection(Socket socket) {
		try {
			client = socket;
			inStream = new ObjectInputStream(client.getInputStream());
			outStream = new ObjectOutputStream(client.getOutputStream());
			clientIp = client.getInetAddress().toString();
			
			System.out.println("+++ CONNECTION FROM " + socket.getLocalAddress().toString() + " +++");
			
		} catch (IOException e) {
			System.err.println("+++ COULD NOT CREATE STREAMS TO CLIENT +++");
			e.printStackTrace();
		}
	}

	/**
	 * Call to get the ID on the client for this connection
	 * @return The IP address for the client
	 */
	public String getClientId() {
		return clientName + clientIp;
	}

	/**
	 * Called to get the name for the user connected
	 * @return The name of the user currently connected
	 */
	public String getClientName() {
		return clientName;
	}
	
	/**
	 * Called to get the image server used by this connection
	 * @return The image server
	 */
	public GalleryInterface getImageServer() {
		return imageServer;
	}
	
	/**
	 * Called to disconnect the client connection
	 */
	public void disconnect() {
		try {
			inStream.close();
			outStream.close();
			client.close();
			authenticated = false;
			run = false;
			System.out.println("+++ CLIENT " + getClientId().toUpperCase() + " DISCONNECTED +++\n");
			Server.instance().disconnect(this);
			
		} catch (IOException e) {
			System.err.println("+++ CLIENT " + getClientId().toUpperCase() + " WAS UNEXCPECTEDLY DISCONNECTED +++\n");
			Server.instance().disconnect(this);
		}
	}

	//=======================================
	//	RUN METHOD
	//---------------------------------------
	
	/**
	 * Called to make the handler listen for requests from the client.
	 * Handles all client requests by creating an appropriate handler object
	 */
	public void run() {
		while (run) {
			//Receiving a token from the client
			try {
				request = (Token) inStream.readObject();
				System.out.println("+++ REQUEST RECEIVED FROM " + getClientId().toUpperCase() +
						": " + Messages.getMessage(request.message()).toUpperCase() + " +++");
			} catch (ClassNotFoundException e) {
				System.err.println("+++ UNKNOWN TOKEN RECEIVED +++");
			} catch (IOException e) {
				System.err.println("+++ CONNECTION TO " + getClientId().toUpperCase() + " FAILED +++");
				disconnect();
				break;
			}
			
			if(!authenticated) {
				//Code for handling login requests
				if(request.message() == Messages.LOGIN) {
					clientName = ((LoginToken) request).getUser();
					handler = new LoginHandler(attemptsLeft);
					
					if(handler.dealWithIt(request, outStream)) {
						authenticated = true;
						notifyObservers();
						imageServer = GallerySelector.getGallery(clientName);
						System.out.println("+++ USER " + ((LoginToken) request).getUser()
								.toUpperCase() + " LOGGED IN FROM " + clientIp + " +++");
					}
					else
						attemptsLeft--;

					if(attemptsLeft == 0) {
						Server.instance().blacklist(client.getInetAddress());
						disconnect();
						break;
					}
				}
				else if(request.message() == Messages.CREATE_USER) {
					handler = new RegisterUserHandler();
					handler.dealWithIt(request, outStream);
				}
				else {
					handler = new DeniedHandler();
					handler.dealWithIt(request, outStream);
				}
			}
			else {
				//Code for handling all other requests
				handler = RequestHandlerFactory.getHandler(request.message(), this);
				handler.dealWithIt(request, outStream);
			}
		}
	}
}

package se.viewer.image.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Observable;

import se.viewer.image.database.GalleryInterface;
import se.viewer.image.database.GallerySelector;
import se.viewer.image.database.MessageLog;
import se.viewer.image.server.requests.RequestHandlerFactory;
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
	
	private String clientName;
	private String clientIp;
	
	private boolean run = true;
	private boolean authenticated = false;
	
	private GalleryInterface imageServer;
	private MessageLog log;
	private RequestHandlerFactory handler;
	
	/**
	 * Opens a new connection handler that caters to the client on the specified socket
	 * @param socket A socket with a client connected to it
	 */
	public ClientConnection(Socket socket) {
		try {
			client = socket;
			inStream = new ObjectInputStream(client.getInputStream());
			outStream = new ObjectOutputStream(client.getOutputStream());
			
			clientName = "UNKNOWN";
			clientIp = client.getInetAddress().toString();
			
			handler = new RequestHandlerFactory(this);
			log = new MessageLog();
			
			log.newLogMessage("+++ CONNECTION FROM " + socket.getLocalAddress().toString() + " +++");
			
		} catch (IOException e) {
			System.err.println("+++ COULD NOT CREATE STREAMS TO CLIENT +++");
			e.printStackTrace();
		}
	}
	
	//=======================================
	//	GETTERS
	//---------------------------------------
	
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
	 * Called to get the object output stream associated with this client
	 * @return The stream to the client
	 */
	public ObjectOutputStream getStream() {
		return outStream;
	}
	
	/**
	 * Called to get the image server used by this connection
	 * @return The image server
	 */
	public GalleryInterface getImageServer() {
		return imageServer;
	}
	
	/**
	 * Called to get the log of server messages for this connection
	 * @return The message log
	 */
	public MessageLog getMessageLog() {
		return log;
	}

	public void authenticate(String user) {
		authenticated = true;
		clientName = user;
		imageServer = GallerySelector.getGallery(clientName);
		log.userLoggedIn(clientName);
		
		setChanged();
		notifyObservers(Messages.LOGIN_SUCCESS);
		clearChanged();
		
		log.newLogMessage("+++ USER " + clientName + " LOGGED IN FROM " + clientIp + " +++");
	}
	
	public void blacklist() {
		Server.instance().blacklist(client.getInetAddress());
		disconnect();
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
			Token request;
			//Receiving a token from the client
			try {
				request = (Token) inStream.readObject();
				log.newLogMessage("Request received from " + getClientId().toUpperCase() +
						": +++ " + Messages.getMessage(request.message()).toUpperCase() + " +++");
			} catch (ClassNotFoundException e) {
				System.err.println("+++ UNKNOWN TOKEN RECEIVED +++");
				break;
			} catch (IOException e) {
				System.err.println("+++ CONNECTION TO " + getClientId().toUpperCase() + " FAILED +++");
				disconnect();
				break;
			}
			
			//Handling the request token
			if(!authenticated) {
				if(request.message() == Messages.LOGIN) {
					clientName = ((LoginToken) request).getUser();
					handler.handle(request);
				}
				else if(request.message() == Messages.REGISTER_USER) {
					handler.handle(request);
				}
				else {
					handler.handle(null);
				}
			}
			else if(authenticated){
				handler.handle(request);
			}
		}
	}
	
	/**
	 * Called to disconnect the client connection
	 */
	public void disconnect() {
		try {
			setChanged();
			notifyObservers(Messages.DISCONNECTED);
			clearChanged();
			
			inStream.close();
			outStream.close();
			client.close();
			
			if(log != null)
				log.shutdown();

			run = false;
			Server.instance().disconnect(this);
			System.out.println("+++ CLIENT " + getClientId().toUpperCase() + " DISCONNECTED +++\n");
			
		} catch (IOException e) {
			System.err.println("+++ CLIENT " + getClientId().toUpperCase() 
					+ " WAS UNEXCPECTEDLY DISCONNECTED +++\n");
			Server.instance().disconnect(this);
		}
	}
}

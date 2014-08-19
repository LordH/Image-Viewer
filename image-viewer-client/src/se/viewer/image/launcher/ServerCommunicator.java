package se.viewer.image.launcher;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;

import se.viewer.image.structure.Image;
import se.viewer.image.structure.Tag;
import se.viewer.image.structure.Thumbnail;
import se.viewer.image.tokens.LoginFailedToken;
import se.viewer.image.tokens.LoginToken;
import se.viewer.image.tokens.LogoutToken;
import se.viewer.image.tokens.Messages;
import se.viewer.image.tokens.RegisterUserToken;
import se.viewer.image.tokens.SendImageToken;
import se.viewer.image.tokens.SendThumbnailsToken;
import se.viewer.image.tokens.Token;
import se.viewer.image.tokens.UpdateTagsToken;

/**
 * Class for handling communications with the server.
 * @author Harald Brege
 */
public class ServerCommunicator implements Runnable{

	private static final int WAIT = 0;
	private static final int SHUTDOWN = 1;
	private static final int CONNECT = 2;
	private static final int DISCONNECT = 5;
	private static final int LOGIN = 4;
	private static final int REGISTER_USER = 3;
	
	private static final int GET_IMAGE = 6;
	private static final int GET_THUMBNAILS = 7;
	private static final int UPDATE_TAGS = 8;
	
	private static ServerCommunicator instance;
	private static Thread thread;
	
	private String host;
	private int port;
	private Socket socket; 
	private ObjectInputStream inStream;
	private ObjectOutputStream outStream;
	
	private LinkedList<Integer> tasks = new LinkedList<Integer>();
	private LinkedList<Token> tokens = new LinkedList<Token>();
	private boolean run = true;
	
	private ServerCommunicator() {}
	
	/**
	 * Called to get the singleton instance of the server handler
	 * @return The instance
	 */
	public static ServerCommunicator instance() {
		if(instance == null) {
			instance = new ServerCommunicator();
			thread = new Thread(instance);
			thread.start();
		}
		return instance;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		while (run) {
			switch (getTask()) {
			//Just wait for some time if there are no requests
			case WAIT :
				try {
					Thread.sleep(0);
				} catch (InterruptedException e) {
					
				}
				break;
				
			//Shut down the server communicator
			case SHUTDOWN :
				run = false;
				break;
			
			//Attempt to connect to the server at the provided address
			case CONNECT :
				try {
					if(socket != null)
						socket.close();

					socket = new Socket(host, port);
					outStream = new ObjectOutputStream(socket.getOutputStream());
					inStream = new ObjectInputStream(socket.getInputStream());

				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
				
			//Attempt to disconnect from the currently connected server
			case DISCONNECT :
				try {			
					if(socket.isClosed()) 
						break;
					
					outStream.writeObject(new LogoutToken());
					System.out.println("Logged out");
					outStream.close();
					inStream.close();
					socket.close();
					
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
				
			//Attempt to login to the currently connected server
			case LOGIN :
				if(socket.isClosed())
					break;

				try {			
					outStream.writeObject(getToken());
					Token answer = (Token) inStream.readObject();
					
					if(answer.message() == Messages.LOGIN_SUCCESS) {
						System.out.println("Login successful.");
						Client.instance().loginSuccess(true, 0);
					}
					else if(answer.message() == Messages.LOGIN_FAILURE) {
						System.out.println("Login failed because of: " + ((LoginFailedToken) answer).reason());
						System.out.println("Attempts left: " + ((LoginFailedToken) answer).attemptsLeft());
						Client.instance().loginSuccess(false, ((LoginFailedToken) answer).attemptsLeft());
					}
					
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				break;
			
			//Attempt to create a new user
			case REGISTER_USER :
				try {
					outStream.writeObject(getToken());
					boolean success = (boolean) inStream.readObject();
					Client.instance().registrationSuccess(success);
					
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				break;

			//Attempt to get an image from the server	
			case GET_IMAGE :
				if(socket.isClosed())
					break;
				
				Client.instance().deliverUpdateProgress(Client.IMAGE_DOWNLOAD_STARTED, 0);
				try {
					long time1 = Calendar.getInstance().getTimeInMillis();
					outStream.writeObject(getToken());
					Image image = (Image) inStream.readObject();
					long time2 = Calendar.getInstance().getTimeInMillis();
					System.out.println("Time to download image: " + (time2-time1) + " ms");
					Client.instance().deliverImage(image);
					
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				break;
				
			//Attempt to get thumbnails from the server
			case GET_THUMBNAILS :
				if(socket.isClosed())
					break;
				try {
					outStream.writeObject(getToken());
					ArrayList<Thumbnail> thumbs = (ArrayList<Thumbnail>) inStream.readObject();
					ArrayList<Tag> tags = (ArrayList<Tag>) inStream.readObject();
					Client.instance().deliverThumbnails(thumbs);
					Client.instance().deliverTags(tags);
					
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				break;
				
			//Attempt to update the tags for an image
			case UPDATE_TAGS :
				if(socket.isClosed())
					break;
				
				try {
					outStream.writeObject(getToken());
					ArrayList<Tag> tags = (ArrayList<Tag>) inStream.readObject();
					Client.instance().deliverTags(tags);
					
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				break;
			}
		}
	}
	
	//=======================================
	//	PUBLIC REQUESTS
	//---------------------------------------
	
	public void shutdown() {
		tasks.addLast(SHUTDOWN);
		thread.interrupt();
	}
	
	public void connect(String host, int port) {
		this.host = host;
		this.port = port;
		tasks.addLast(CONNECT);
		thread.interrupt();
	}

	public void disconnect() {
		tasks.addLast(DISCONNECT);
		thread.interrupt();
	}

	public void login(String username, String password) {
		tokens.addLast(new LoginToken(username, password));
		tasks.addLast(LOGIN);
		thread.interrupt();
	}
	
	public void registerUser(String username, String password) {
		tokens.add(new RegisterUserToken(username, password));
		tasks.addLast(REGISTER_USER);
		thread.interrupt();
	}
	
	public void updateTags(String image, ArrayList<Tag> tags) {
		tokens.addLast(new UpdateTagsToken(image, tags));
		tasks.addLast(UPDATE_TAGS);
		thread.interrupt();
	}

	public void getImage(int dir) {
		tokens.addLast(new SendImageToken(dir));
		tasks.addLast(GET_IMAGE);
		thread.interrupt();
	}
	
	public void getImage(String image) {
		tokens.addLast(new SendImageToken(image));
		tasks.addLast(GET_IMAGE);
		thread.interrupt();
	}
	
	public void getThumbnails(Tag tag) {
		tokens.addLast(new SendThumbnailsToken(tag));
		tasks.addLast(GET_THUMBNAILS);
		thread.interrupt();
	}
	
	//=======================================
	//	PRIVATE METHODS
	//---------------------------------------
	
	private int getTask() {
		if(!tasks.isEmpty())
			return tasks.removeFirst();
		else
			return WAIT;
	}
	
	private Token getToken() {
		return tokens.removeFirst();
	}
}

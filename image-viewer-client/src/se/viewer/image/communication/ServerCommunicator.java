package se.viewer.image.communication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Observable;
import java.util.concurrent.LinkedBlockingQueue;

import se.viewer.image.client.Client;
import se.viewer.image.containers.Image;
import se.viewer.image.containers.Tag;
import se.viewer.image.tokens.DeliverImageToken;
import se.viewer.image.tokens.DeliverThumbnailsToken;
import se.viewer.image.tokens.LoginFailedToken;
import se.viewer.image.tokens.LoginSuccessToken;
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
public class ServerCommunicator extends Observable implements Runnable {

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

	private LinkedBlockingQueue<Integer> tasks = new LinkedBlockingQueue<Integer>();
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
			int next;
			try {
				next = tasks.take();
				System.out.println("going to " + next);
				
				switch (next) {

				//Shut down the server communicator
				case SHUTDOWN :
					run = false;
					break;

				//Attempt to connect to the server at the provided address
				case CONNECT :
					try {
						socket = new Socket();
						socket.connect(new InetSocketAddress(host, port), 2000);
						outStream = new ObjectOutputStream(socket.getOutputStream());
						inStream = new ObjectInputStream(socket.getInputStream());
						
						setChanged();
						notifyObservers(true);
						clearChanged();
						//					Client.instance().connectionSuccess(true, host);
					} catch (IOException e) {
						//					Client.instance().connectionSuccess(false, host);
						e.printStackTrace();
						resetConnection();

						setChanged();
						notifyObservers(false);
						clearChanged();
					}
					break;

				//Attempt to disconnect from the currently connected server
				case DISCONNECT :
					if(socket == null)
						break;
					else if(socket.isClosed()) {
						resetConnection();
						break;
					}

					try {
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
					if(socket == null)
						break;
					else if(socket.isClosed()) {
						resetConnection();
						break;
					}

					try {			
						outStream.writeObject(getToken());
						Token answer = (Token) inStream.readObject();

						if(answer.message() == Messages.LOGIN_SUCCESS) {
							LoginSuccessToken t = (LoginSuccessToken) answer;
							System.out.println("Login successful.");
							Client.instance().deliverThumbnails(t.getThumbs());
							Client.instance().deliverTags(t.getTags());
						}
						else if(answer.message() == Messages.LOGIN_FAILURE) {
							
						}

					} catch (IOException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					break;

				//Attempt to create a new user
				case REGISTER_USER :
					if(socket == null)
						break;
					else if(socket.isClosed()) {
						resetConnection();
						break;
					}

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
					if(socket == null)
						break;
					else if(socket.isClosed()) {
						resetConnection();
						break;
					}

					Client.instance().deliverUpdateProgress(Client.IMAGE_DOWNLOAD_STARTED, 0);
					try {
						Image image = null;					
						outStream.writeObject(getToken());
						Token token = (Token) inStream.readObject();

						if(token.message() == Messages.DELIVER_IMAGE)
							image = ((DeliverImageToken) token).getImage();
						else {
							System.err.println("Image download failed");
						}
						Client.instance().deliverImage(image);

					} catch (IOException e) {
						e.printStackTrace();
						resetConnection();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					break;

				//Attempt to get thumbnails from the server
				case GET_THUMBNAILS :
					if(socket == null)
						break;
					else if(socket.isClosed()) {
						resetConnection();
						break;
					}

					try {
						outStream.writeObject(getToken());
						Token answer = (Token) inStream.readObject();

						if(answer.message() != Messages.DELIVER_THUMBNAILS) {
							System.err.println("Thumbnail download failed");
							break;
						}
						Client.instance().deliverThumbnails(((DeliverThumbnailsToken)answer));

					} catch (IOException e) {
						e.printStackTrace();
						resetConnection();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					break;

				//Attempt to update the tags for an image
				case UPDATE_TAGS :
					if(socket == null)
						break;
					else if(socket.isClosed()) {
						resetConnection();
						break;
					}

					try {
						outStream.writeObject(getToken());
						ArrayList<Tag> tags = (ArrayList<Tag>) inStream.readObject();
						Client.instance().deliverTags(tags);

					} catch (IOException e) {
						e.printStackTrace();
						resetConnection();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					break;
				}
				
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}

	//=======================================
	//	SERVER REQUESTS
	//---------------------------------------

	public void shutdown() {
		try {
			tasks.put(SHUTDOWN);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void connect(String host, int port) {
		this.host = host;
		this.port = port;
		try {
			tasks.put(CONNECT);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void disconnect() {
		try {
			tasks.put(DISCONNECT);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void login(String username, String password) {
		tokens.addLast(new LoginToken(username, password));
		try {
			tasks.put(LOGIN);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void registerUser(String username, String password) {
		tokens.add(new RegisterUserToken(username, password));
		try {
			tasks.put(REGISTER_USER);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void updateTags(String image, ArrayList<Tag> tags) {
		tokens.addLast(new UpdateTagsToken(image, tags));
		try {
			tasks.put(UPDATE_TAGS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void getImage(int dir) {
		tokens.addLast(new SendImageToken(dir));
		try {
			tasks.put(GET_IMAGE);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void getImage(String image) {
		tokens.addLast(new SendImageToken(image));
		try {
			tasks.put(GET_IMAGE);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void getThumbnails(Tag tag) {
		tokens.addLast(new SendThumbnailsToken(tag));
		try {
			tasks.put(GET_THUMBNAILS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	//=======================================
	//	INFORMATION METHODS
	//---------------------------------------

	public boolean isConnected() {
		return socket != null;
	}

	//=======================================
	//	PRIVATE METHODS
	//---------------------------------------
	
	private Token getToken() {
		return tokens.removeFirst();
	}

	private void resetConnection() {
		socket = null;
		outStream = null;
		inStream = null;

		Client.instance().loginMode();
	}
}

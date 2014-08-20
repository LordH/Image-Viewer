package se.viewer.image.tokens;

/**
 * Class defining a number of constants for server-client communication
 * @author Harald Brege
 */
public class Messages {
	
	public static final int REGISTER_USER = -1;
	public static final int LOGIN = 0;
	public static final int LOGOUT = 1;
	public static final int LOGIN_SUCCESS = 2;
	public static final int LOGIN_FAILURE = 3;
	public static final int DISCONNECTED = -2;
	
	public static final int REQUEST_DENIED = 4;
	
	public static final int SEND_IMAGE = 5;
	public static final int SEND_THUMBNAILS = 6;
	public static final int UPDATE_TAGS = 7;
	
	/**
	 * Called to get the message as a string
	 * @param id ID of the message
	 * @return the message as a string
	 */
	public static String getMessage(int id) {
		switch (id) {
		case REGISTER_USER :
			return "register user";
		case LOGIN : 
			return "login";
		case LOGOUT :
			return "logout";
		case LOGIN_SUCCESS :
			return "login success";
		case LOGIN_FAILURE :
			return "login failure";
		case DISCONNECTED :
			return "disconnected";
		case REQUEST_DENIED :
			return "request denied";
		case SEND_IMAGE :
			return "send image";
		case SEND_THUMBNAILS :
			return "send thumbnails";
		case UPDATE_TAGS :
			return "update tags";
		default:
			return "invalid message id";
		}
	}
}

package se.viewer.image.tokens;

/**
 * Token to deliver a logout request to the server
 * 
 * @author Harald Brege
 */
public class LogoutToken implements Token {

	private static final long serialVersionUID = -2714476222590604920L;

	@Override
	public int message() {
		return Messages.LOGOUT;
	}
}

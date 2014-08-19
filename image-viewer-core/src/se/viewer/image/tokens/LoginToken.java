package se.viewer.image.tokens;

/**
 * Token to deliver a login request and credentials to the server
 * @author Harald Brege
 */
public class LoginToken implements Token {
	
	private static final long serialVersionUID = 4912312212412845032L;
	private String user;
	private String password;
	
	public LoginToken(String login, String password) {
		this.user = login;
		this.password = password;
	}
	
	@Override
	public int message() {
		return Messages.LOGIN;
	}

	public String getUser() {
		return user;
	}
	
	public String getPassword() {
		return password;
	}
}

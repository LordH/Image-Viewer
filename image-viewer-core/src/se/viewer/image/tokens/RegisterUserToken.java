package se.viewer.image.tokens;

public class RegisterUserToken implements Token {

	private static final long serialVersionUID = 5831946416219618039L;
	private String username;
	private String password;
	
	public RegisterUserToken(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	@Override
	public int message() {
		return Messages.REGISTER_USER;
	}
	
	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
}

package se.viewer.image.tokens;

public class LoginFailedToken implements Token {

	private static final long serialVersionUID = -5022028779694111267L;
	private String reason;
	private int attemptsLeft;

	public LoginFailedToken(String reason, int attemptsLeft) {
		this.reason = reason;
		this.attemptsLeft = attemptsLeft;
	}
	
	@Override
	public int message() {
		return Messages.LOGIN_FAILURE;
	}
	
	public String reason() {
		return reason;
	}

	public int attemptsLeft() {
		return attemptsLeft;
	}
}

package se.viewer.image.tokens;

public class LoginSuccessToken implements Token {
	
	private static final long serialVersionUID = 1123769669272424764L;

	@Override
	public int message() {
		return Messages.LOGIN_SUCCESS;
	}

}

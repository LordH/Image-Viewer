package se.viewer.image.tokens;

public class DeniedToken implements Token {
	
	private static final long serialVersionUID = 7627156887959973506L;
	private String reason;
	
	public DeniedToken(String reason) {
		this.reason = reason;
	}
	
	@Override
	public int message() {
		return Messages.REQUEST_DENIED;
	}

	public String reason() {
		return reason;
	}
}

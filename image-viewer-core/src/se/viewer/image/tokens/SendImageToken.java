package se.viewer.image.tokens;

public class SendImageToken implements Token {
	
	private static final long serialVersionUID = -5570173558852633557L;
	private int direction;
	private String image;
	
	public SendImageToken(int direction) {
		this.direction = direction;
	}
	
	public SendImageToken(String image) {
		this.direction = 0;
		this.image = image;
	}
	
	@Override
	public int message() {
		return Messages.SEND_IMAGE;
	}

	public int direction() {
		return direction;
	}
	
	public String getImage() {
		return image;
	}
}

package se.viewer.image.server.requests;

import java.io.IOException;
import java.io.ObjectOutputStream;

import se.viewer.image.tokens.DeniedToken;
import se.viewer.image.tokens.Token;

public class DeniedHandler implements RequestHandlerInterface {

	@Override
	public boolean dealWithIt(Token token, ObjectOutputStream outStream) {
		
		try {
			outStream.writeObject(new DeniedToken("Not authenticated!"));
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

}

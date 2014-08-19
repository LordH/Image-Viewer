package se.viewer.image.server.requests;

import java.io.ObjectOutputStream;

import se.viewer.image.tokens.Token;

/**
 * Interface that describes a generic request handler
 * 
 * @author Harald Brege
 */
public interface RequestHandlerInterface {
	
	/**
	 * This method should handle the client request embedded in the token
	 * @param token The embedded client request
	 * @param outStream The stream to return any results to the client
	 * @return Success or failure of operation
	 */
	public boolean dealWithIt(Token token, ObjectOutputStream outStream);
	
}

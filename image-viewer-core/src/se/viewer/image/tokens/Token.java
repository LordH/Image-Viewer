package se.viewer.image.tokens;

import java.io.Serializable;

/**
 * This interface describes a generic request token for a client to send to the server
 * 
 * @author Harald Brege
 */
public interface Token extends Serializable{
	
	/**
	 * This method is called to get the nature of the request
	 * @return A short string describing the request
	 */
	public int message();
	
}

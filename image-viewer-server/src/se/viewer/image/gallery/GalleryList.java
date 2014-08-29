package se.viewer.image.gallery;

import java.util.ArrayList;

import se.viewer.image.server.ClientConnection;

/**
 * Class for handling a list of galleries linked to certain clients
 * @author Harald Brege
 */
public class GalleryList {

	private ArrayList<ClientConnection> userList;
	private ArrayList<GalleryInterface> galleryList;
	
	protected GalleryList() {
		userList = new ArrayList<ClientConnection>();
		galleryList = new ArrayList<GalleryInterface>();
	}
	
	public boolean add(ClientConnection user, GalleryInterface gallery) {
		if(!userList.contains(user)) {
			userList.add(user);
			galleryList.add(gallery);
			return true;
		}
		return false;
	}
	
	public boolean contains(ClientConnection user) {
		return userList.contains(user);
	}
	
	public boolean remove(ClientConnection user) {
		int index = userList.indexOf(user);
		if(index != -1) {
			userList.remove(index);
			galleryList.remove(index);
			return true;
		}
		return false;
	}
	
	public GalleryInterface get(ClientConnection user) {
		GalleryInterface db = null;
		int index = userList.indexOf(user);
		if(index != -1)
			db = galleryList.get(index);
		return db;
	}
}

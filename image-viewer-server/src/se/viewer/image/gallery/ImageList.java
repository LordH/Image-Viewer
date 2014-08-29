package se.viewer.image.gallery;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Class for handling a list of images and corresponding locations
 * @author Harald Brege
 */
public class ImageList implements Iterable<String>{

	private ArrayList<String> locations;
	private ArrayList<String> names;
	private int index;
	
	public ImageList() {
		names = new ArrayList<String>();
		locations = new ArrayList<String>();
		index = 0;
	}
	
	public ImageList(ImageList list) {
		names = new ArrayList<String>();
		locations = new ArrayList<String>();
		index = 0;
		
		for(int i=0; i<list.size(); i++)
			add(list.getLocation(i), list.getName(i));
	}
	
	//=======================================
	//	ELEMENT HANDLING METHODS
	//---------------------------------------	
	
	public boolean add(String location, String name) {
		if(!names.contains(name)) {
			locations.add(location);
			names.add(name);
			return true;
		}
		return false;
	}
	
	public void add(String location, String[] names) {
		for(String name : names)
			if(!this.names.contains(name)) {
				locations.add(location);
				this.names.add(name);
			}
	}
	
	public boolean remove(String name) {
		int index = names.indexOf(name);
		if(index != -1) {
			names.remove(index);
			locations.remove(index);
			return true;
		}
		return false;
	}
	
	public void clear() {
		locations.clear();
		names.clear();
		index = 0;
	}
	
	public String get(int dir) {
		if(size() == 0)
			return null;
		
		index = (index + size() + dir) % size();
		return getName(index);
	}
	
	public String get(String name) {
		if(size() == 0)
			return null;
		
		index = names.indexOf(name);
		if(index == -1)
			return "no such image";
		return getPath(index);
	}

	@Override
	public Iterator<String> iterator() {
		return names.iterator();
	}
	
	//=======================================
	//	INFORMATION RETRIEVAL METHODS 
	//---------------------------------------	

	public ArrayList<String> getList() {
		ArrayList<String> compound = new ArrayList<String>();
		for(int i=0; i<size(); i++)
			compound.add(getPath(i));
		return compound;
	}
	
	public String getLocation(int index) {
		if(index > -1 && index < size())
			return locations.get(index);
		return null;
	}
	
	public String getLocation(String name) {
		int index = names.indexOf(name);
		if(index != -1)
			return locations.get(index);
		return null;
	}
	
	public String getName(int index) {
		if(index > -1 && index < size())
			return names.get(index);
		return null;
	}
	
	public String getPath(int index) {
		if(index > -1 && index < size())
			return getLocation(index) + "\\" + getName(index);
		return null;
	}	
	
	public boolean contains(String name) {
		if(names.contains(name))
			return true;
		return false;
	}
	
	public int size() {
		return names.size();
	}
	
	public boolean isEmpty() {
		return size() == 0;
	}
}

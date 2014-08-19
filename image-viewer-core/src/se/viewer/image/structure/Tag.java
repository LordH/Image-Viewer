package se.viewer.image.structure;

import java.io.Serializable;

public class Tag implements Comparable<Tag>, Serializable{
	
	private static final long serialVersionUID = -2333012473704398641L;
	private String name;
	private String type;
	private int count;
	
	public Tag(String name, String type) {
		this.name = name;
		this.type = type;
		this.count = 0;
	}

	public Tag(String name, String type, int count) {
		this.name = name;
		this.type = type;
		this.count = count;
	}
	
	//=======================================
	//	GETTERS AND SETTERS
	//---------------------------------------

	public String getName() {
		return name;
	}
	
	public String getType() {
		return type;
	}
	
	public int getCount() {
		return count;
	}
	
	public void setCount(int count) {
		this.count = count;
	}
	
	@Override
	public int compareTo(Tag tag) {
		
		int typeComparison = type.compareTo(tag.getType());
		if(typeComparison != 0)
			return typeComparison;
		
		int nameComparison = name.compareTo(tag.getName());
		return nameComparison;
	}
}

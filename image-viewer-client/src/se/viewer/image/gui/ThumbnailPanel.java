package se.viewer.image.gui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;

import se.viewer.image.structure.Thumbnail;

public class ThumbnailPanel extends JPanel {

	private static final long serialVersionUID = -8104118838714686809L;
	private ArrayList<Component> components;
	private ArrayList<Thumbnail> images;
	private int imagesDisplayed;
	
	private JLabel end;
	
	public ThumbnailPanel(ArrayList<Thumbnail> images) {
		setLayout(new GridBagLayout());
		
		this.images = new ArrayList<Thumbnail>();
		components = new ArrayList<Component>();
		imagesDisplayed = 0;
		JLabel label = new JLabel("Tag");
		end = new JLabel();
		end.setName("end");
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.anchor = GridBagConstraints.NORTH;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1;
		add(label, c);
		
		addThumbnails(images);
	}
	
	public void addThumbnails(ArrayList<Thumbnail> images) {
		if(images != null) {
			this.images.addAll(images);
			setupList();
		}
	}
	
	public void redraw() {
		clear();
		setupList();
	}
	
	public void clear() {
		for(Component comp : components)
			remove(comp);
		while(components.remove(end))
			remove(end);
		imagesDisplayed = 0;
	}
	
	private void setupList() {
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 0.2;
		c.weighty = 0;

		while(components.remove(end))
			remove(end);
		
		SmallImagePanel image;
		
		int rows = (int)(getParent().getParent().getParent().getWidth() / 235.0);
		int start = imagesDisplayed;
		
		c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5, 5, 5, 5);
		c.weightx = 1;
		
		for(int i=start; i<images.size(); i++) {
			c.gridx = i % rows;
			c.gridy = (int) Math.floor(i/(double)rows) + 1;
			image = new SmallImagePanel(images.get(i).getName(), images.get(i).getImage(null));
			add(image, c);
			components.add(image);
			imagesDisplayed++;
		}
		
		end = new JLabel();
		end.setName("end");
		
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = (int)(images.size()/(double)rows + 2);
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridheight = GridBagConstraints.REMAINDER;
		c.weightx = 1;
		c.weighty = 1;
		add(end, c);
		components.add(end);

		updateUI();
	}
}

package se.viewer.image.gui.viewer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import se.viewer.image.communication.ServerCommunicator;
import se.viewer.image.containers.Image;
import se.viewer.image.containers.Tag;

public class SidePanel extends JPanel implements ActionListener{

	private static final long serialVersionUID = -5520409393056099549L;
	
	private String image;
	private ArrayList<Tag> tags;
	private ArrayList<Component> components;
	private JTextField searchField;
	private JTextArea tagField;
	
	public SidePanel(Image image) {
		setLayout(new GridBagLayout());
		components = new ArrayList<Component>();
				
		//Setting up all components
		searchField = new JTextField();
		searchField.setText(" Search...");
		searchField.setFont(new Font("Arial", Font.PLAIN, 14));
		searchField.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Color.GRAY), 
				BorderFactory.createEmptyBorder(2, 5, 2, 5)));

		JLabel tagLabel = new JLabel("Tags");
		Font font = new Font("Arial", Font.BOLD, 18);
		tagLabel.setFont(font);
		
		tagField = new JTextArea();
		tagField.setText("");
		tagField.setLineWrap(true);
		tagField.setWrapStyleWord(true);
		tagField.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Color.GRAY), 
				BorderFactory.createEmptyBorder(2, 5, 2, 5)));
		
		//Adding all components in their right place
		GridBagConstraints c = new GridBagConstraints();

		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 0;
		c.weightx = 1;
		c.insets = new Insets(10, 10, 0, 10);
		add(searchField, c);
		
		c.anchor = GridBagConstraints.CENTER;
		c.gridy = 1;
		c.ipadx = 298;
		add(new JSeparator(SwingConstants.HORIZONTAL), c);
		
		c.anchor = GridBagConstraints.WEST;
		c.gridy = 2;
		c.ipadx = 0;
		add(tagLabel, c);
		
		if(image != null) {
			setImage(image);
		}
	}
	
	public void setImage(Image image) {
		if(image != null) {
			this.image = image.getName();
			tags = image.getTags();
		}
		else {
			this.image = "";
			tags = new ArrayList<Tag>();
		}
		setupList();
	}
	
	public void setTags(ArrayList<Tag> tags) {
		this.tags = tags;
		setupList();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand() == "tags") {
			String string = tagField.getText().toLowerCase() + " ";
			ArrayList<Tag> list = new ArrayList<Tag>();
			Tag toAdd;
			boolean exists = false;
			
			String temp = "";
			for(int i=0; i<string.length(); i++) {
				
				if(string.charAt(i) != ' ') 
					temp = temp + string.charAt(i);
				else {
					if(temp != "") {
						for(Tag tag : list) {
							if(tag.getName().equals(temp)) {
								exists = true;
								break;
							}
						}
						if(!exists) {
							toAdd = new Tag(temp, "normal");
							list.add(toAdd);
						}
						temp = "";
						exists = false;
					}
				}
			}
			Collections.sort(list);
			ServerCommunicator.instance().updateTags(image, list);
			updateTags(list);
		}
	}
	
	//=======================================
	//	INTERNAL METHODS
	//---------------------------------------
	
	private void updateTags(ArrayList<Tag> newTags) {
		tags = newTags;
		setupList();
	}
	
	private void setupList() {
		for(Component comp : components)
			remove(comp);
		
		tagField.setText("");
		components.add(tagField);
		
		JButton button = new JButton("Set tags");
		button.addActionListener(this);
		button.setActionCommand("tags");
		components.add(button);
		
		JLabel label;
		GridBagConstraints c = new GridBagConstraints();
		
		c.anchor = GridBagConstraints.NORTHWEST;
		c.gridx = 0;
		c.weighty = 0;
		c.insets = new Insets(5, 10, 0, 0);
		
		int i = 3;
		for(Tag tag : tags) {
			label = new JLabel();
			label.setText(tag.getName() + " (" + tag.getCount() +")");
			label.setFont(new Font("Arial", Font.BOLD, 13));
			label.setForeground(new Color(255, 127, 0));
			label.addMouseListener(new tagMouse(label, tag));

			tagField.setText(tagField.getText() + tag.getName() + " ");
			components.add(label);
			
			c.gridy = i++;
			add(label, c);
		}
		
		c.anchor = GridBagConstraints.SOUTH;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridy = i;
		c.gridheight = GridBagConstraints.RELATIVE;
		c.weighty = 1;
		c.ipady = 100;
		c.insets = new Insets(0, 10, 10, 10);
		add(tagField, c);
		
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.NONE;
		c.gridy = i+1;
		c.gridheight = 1;
		c.weighty = 0;
		c.ipady = 0;
		add(button, c);
		
		validate();
		repaint();
	}
	
	//=======================================
	//	LISTENER CLASSES
	//---------------------------------------
	
	/**
	 * Mouse listener for the tag labels
	 * @author Harald Brege
	 */
	private class tagMouse implements MouseListener {
		private JLabel label;
		private Tag tag;
		private Color color;
		
		public tagMouse(JLabel label, Tag tag) {
			this.label = label;
			this.tag = tag;
			color = label.getForeground();
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {
			ServerCommunicator.instance().getThumbnails(tag);
		}
		
		@Override
		public void mouseEntered(MouseEvent e) {
			label.setForeground(color.brighter());
			setCursor(new Cursor(Cursor.HAND_CURSOR));
		}
		
		@Override
		public void mouseExited(MouseEvent e) {
			label.setForeground(color);
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}

		@Override
		public void mousePressed(MouseEvent e) {}

		@Override
		public void mouseReleased(MouseEvent e) {}
		
	}
}

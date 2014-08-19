package se.viewer.image.gui;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;

import se.viewer.image.containers.Image;
import se.viewer.image.containers.Tag;
import se.viewer.image.containers.Thumbnail;
import se.viewer.image.launcher.Client;

/**
 * Class that represents a panel with a picture on it.
 * @author Harald Brege
 */
public class ApplicationPanel extends JPanel{

	private static final long serialVersionUID = -5742181472489084300L;
	
	public static final String IMAGE = "image";
	public static final String THUMBNAILS = "thumbnails";
	public static final String LOADING = "loading";

	private Image source;
	
	private LargeImagePanel imagePanel;
	private SidePanel sidePanel;
	private ThumbnailPanel thumbnailPanel;
	private JPanel rightPanel;
	private CardLayout card;
	
	private JProgressBar progress;
	private JLabel message;
	private JPanel loadingPanel;
	
	/**
	 * Creates a new image view panel
	 */
	public ApplicationPanel() {
		//Setting up all components
		setLayout(new GridBagLayout());
		
		sidePanel = new SidePanel(null);
		sidePanel.setMinimumSize(new Dimension(600, 1));

		imagePanel = new LargeImagePanel();
		imagePanel.setVisible(true);
		
		thumbnailPanel = new ThumbnailPanel(null);
		thumbnailPanel.setVisible(true);
		
		progress = new JProgressBar(0, 100);
		message = new JLabel();
		loadingPanel = new JPanel();
		loadingPanel.setLayout(new GridBagLayout());
		loadingPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		loadingPanel.setPreferredSize(new Dimension(200, 70));
		JPanel container = new JPanel(new GridBagLayout());
		
		card = new CardLayout();
		rightPanel = new JPanel(card);
		rightPanel.add(imagePanel, IMAGE);
		rightPanel.add(thumbnailPanel, THUMBNAILS);
		rightPanel.add(container, LOADING);
		
		JScrollPane scrollLeft = new JScrollPane(sidePanel);
		scrollLeft.getVerticalScrollBar().setUnitIncrement(10);
		scrollLeft.getHorizontalScrollBar().setUnitIncrement(10);
		scrollLeft.setAutoscrolls(true);
//		scrollLeft.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollLeft.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		scrollLeft.setMinimumSize(new Dimension(320, 1));
		
		JScrollPane scrollRight = new JScrollPane(rightPanel);
		scrollRight.getVerticalScrollBar().setUnitIncrement(10);
		scrollRight.getHorizontalScrollBar().setUnitIncrement(10);
		scrollRight.setAutoscrolls(true);
		scrollRight.setName("scroll right");
		scrollRight.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		scrollRight.addComponentListener(new ComponentListener() {
			
			@Override
			public void componentResized(ComponentEvent e) {
				if(source != null)
					imagePanel.setSizeToFit();
				
				thumbnailPanel.redraw();
				updateUI();
			}

			@Override
			public void componentShown(ComponentEvent e) {}
			
			@Override
			public void componentMoved(ComponentEvent e) {}
			
			@Override
			public void componentHidden(ComponentEvent e) {}
		});
		
		//Adding components to the panel
		GridBagConstraints c = new GridBagConstraints();
		
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.VERTICAL;
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 1;
		c.insets = new Insets(40, 40, 40, 20);
		add(scrollLeft, c);

		c.fill = GridBagConstraints.BOTH;
		c.gridx = 1;
		c.weightx = 1;
		c.insets = new Insets(40, 20, 40, 40);
		add(scrollRight, c);
		
		//Adding components to the loading panel
				
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.insets = new Insets(10, 10, 0, 10);
		loadingPanel.add(message, c);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridy = 1;
		c.weighty = 0;
		c.insets = new Insets(10, 10, 10, 10);
		loadingPanel.add(progress, c);
		
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = 0;
		container.add(loadingPanel, c);
	}
	
	//=======================================
	//	IMAGE HANDLING METHODS
	//---------------------------------------
	
	public void setImage(Image image) {
		source = image;
		imagePanel.setImage(source);
		sidePanel.setImage(source);
	}
	
	public void clearImage() {
		setImage(null);
	}

	public void displayImage() {
		thumbnailPanel.clear();
		card.show(rightPanel, IMAGE);
	}
	
	//=======================================
	//	TAG HANDLING METHODS
	//---------------------------------------
	
	public void setTags(ArrayList<Tag> tags) {
		sidePanel.setTags(tags);
	}
	
	//=======================================
	//	LOADING PANEL HANDLING METHODS
	//---------------------------------------
		
	public void displayLoading() {
		thumbnailPanel.clear();
		card.show(rightPanel, LOADING);
	}
	
	public void setUpdateProgress(String message, int percent) {
		this.message.setText(message + " " + percent + "%");
		progress.setValue(percent);
	}
	
	//=======================================
	//	THUMBNAIL HANDLING METHODS
	//---------------------------------------
	
	public void displayThumbnails() {
		card.show(rightPanel, THUMBNAILS);
		Client.instance().setFrameTitle("tag");
	}
	
	public void setThumbnails(ArrayList<Thumbnail> thumbs) {
		thumbnailPanel.addThumbnails(thumbs);
	}
}
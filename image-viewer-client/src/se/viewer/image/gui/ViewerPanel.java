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
import se.viewer.image.launcher.Client;
import se.viewer.image.tokens.DeliverThumbnailsToken;

/**
 * Class that represents a panel with a picture on it.
 * @author Harald Brege
 */
public class ViewerPanel extends JPanel implements ViewerInterface{

	private static final long serialVersionUID = -5742181472489084300L;
	
	private String displaying;
	private Tag currentTag;
	private Image source;
	
	private ImagePanelLarge imagePanel;
	private SidePanel sidePanel;
	private ThumbnailsPanel thumbnailPanel;
	private JPanel rightPanel;
	private CardLayout card;
	
	private JProgressBar progress;
	private JLabel message;
	private JPanel loadingPanel;
	
	/**
	 * Creates a new image view panel
	 */
	public ViewerPanel() {
		//Setting up all components
		setLayout(new GridBagLayout());
		
		sidePanel = new SidePanel(null);
		sidePanel.setMinimumSize(new Dimension(600, 1));

		imagePanel = new ImagePanelLarge();
		imagePanel.setVisible(true);
		
		thumbnailPanel = new ThumbnailsPanel(null);
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
		scrollLeft.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		scrollLeft.setMinimumSize(new Dimension(320, 1));
		
		JScrollPane scrollRight = new JScrollPane(rightPanel);
		scrollRight.getVerticalScrollBar().setUnitIncrement(10);
		scrollRight.getHorizontalScrollBar().setUnitIncrement(10);
		scrollRight.setAutoscrolls(true);
//		scrollRight.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
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
		
		revalidate();
		repaint();
	}
	
	//=======================================
	//	IMAGE HANDLING METHODS
	//---------------------------------------

	/* (non-Javadoc)
	 * @see se.viewer.image.gui.ViewerInterface#displayImage()
	 */
	@Override
	public void displayImage() {
		thumbnailPanel.clearDisplay();
		displaying = IMAGE;
		currentTag = null;
		card.show(rightPanel, IMAGE);
	}
	
	/* (non-Javadoc)
	 * @see se.viewer.image.gui.ViewerInterface#setImage(se.viewer.image.containers.Image)
	 */
	@Override
	public void setImage(Image image) {
		source = image;
		imagePanel.setImage(source);
		sidePanel.setImage(source);
	}
	
	/* (non-Javadoc)
	 * @see se.viewer.image.gui.ViewerInterface#clearImage()
	 */
	@Override
	public void clearImage() {
		setImage(null);
	}
	
	//=======================================
	//	TAG HANDLING METHODS
	//---------------------------------------
	
	/* (non-Javadoc)
	 * @see se.viewer.image.gui.ViewerInterface#setTags(java.util.ArrayList)
	 */
	@Override
	public void setTags(ArrayList<Tag> tags) {
		sidePanel.setTags(tags);
	}
	
	//=======================================
	//	LOADING PANEL HANDLING METHODS
	//---------------------------------------
		
	/* (non-Javadoc)
	 * @see se.viewer.image.gui.ViewerInterface#displayLoading()
	 */
	@Override
	public void displayLoading() {
		thumbnailPanel.clearDisplay();
		displaying = LOADING;
		card.show(rightPanel, LOADING);
	}
	
	/* (non-Javadoc)
	 * @see se.viewer.image.gui.ViewerInterface#setUpdateProgress(java.lang.String, int)
	 */
	@Override
	public void setUpdateProgress(String message, int percent) {
		this.message.setText(message + " " + percent + "%");
		progress.setValue(percent);
	}
	
	//=======================================
	//	THUMBNAIL HANDLING METHODS
	//---------------------------------------
	
	/* (non-Javadoc)
	 * @see se.viewer.image.gui.ViewerInterface#displayThumbnails()
	 */
	@Override
	public void displayThumbnails() {
		imagePanel.setImage(null);
		card.show(rightPanel, THUMBNAILS);
		displaying = THUMBNAILS;
		Client.instance().setTitle(currentTag.getName());
	}
	
	/* (non-Javadoc)
	 * @see se.viewer.image.gui.ViewerInterface#setThumbnails(se.viewer.image.tokens.DeliverThumbnailsToken)
	 */
	@Override
	public void setThumbnails(DeliverThumbnailsToken token) {		
		if(!token.getTag().equals(currentTag)) {
			if(displaying == THUMBNAILS)
				thumbnailPanel.clearDisplay();
			
			thumbnailPanel.clearList();
			currentTag = token.getTag();
		}
		thumbnailPanel.addThumbnails(token.getThumbnails());
		validate();
		thumbnailPanel.repaint();
	}
}
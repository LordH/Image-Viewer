package se.viewer.image.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;

import javax.imageio.ImageReader;
import javax.imageio.event.IIOReadProgressListener;
import javax.swing.JPanel;

import se.viewer.image.containers.Image;
import se.viewer.image.launcher.Client;

public class ImagePanelLarge extends JPanel {
	
	private static final long serialVersionUID = 4901797446281335927L;
	
	private BufferedImage image;
	private BufferedImage scaled;
	private BufferedImage display;
	private String name;

	private Dimension imageSize;
	private Rectangle anchor;
	private double scaleToFit = 1.0;
	private double scaleToSpec = 1.0;
		
	private boolean justZoomed = false;
	
	public ImagePanelLarge() {
		TotalMouseListener listener = new TotalMouseListener();
		addMouseMotionListener(listener);
		addMouseListener(listener);
		addMouseWheelListener(listener);
	}
	
	public void setZoomed() {
		justZoomed = true;
	}

	public void setAnchor(Rectangle anchor) {
		this.anchor = anchor;	
	}
	
	/**
	 * Used to put a new image in the Picture component
	 * @param image The image to be displayed
	 */
	public void setImage(Image toSet) {
		if(toSet != null) {
			name = toSet.getName();
			image = toSet.getImage(new ProgressListener());
			scaled = image;
			imageSize = new Dimension(image.getWidth(), image.getHeight());
			
			scaleToFit = 1.0;
			scaleToSpec = 1.0;
			setSizeToFit();
		}
		else {
			display = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
			scaleToFit = 0;
			scaleToSpec = 0;
			setPreferredSize(new Dimension(0, 0));
		}
	}
	
	/**
	 * Called to adjust the image size to the view port size
	 */
	public void setSizeToFit() {
		double scaleH = 1.0;
		double scaleW = 1.0;
		
		if(image.getWidth() > getParent().getParent().getParent().getWidth()-2){
			scaleW = (getParent().getParent().getParent().getWidth()-2) / (double)image.getWidth();
		}
		if(image.getHeight() > getParent().getParent().getHeight()-2){
			scaleH = (getParent().getParent().getParent().getHeight()-2) / (double)image.getHeight();
		}
		scaleToFit = Math.min(scaleH, scaleW);
		if(scaleToFit != 0.0){
			scaleToSpec = scaleToFit;
			resizeImage(scaleToFit);
			setAnchor(new Rectangle(0, 0, getParent().getParent().getParent().getWidth(), 
					getParent().getParent().getParent().getHeight()));
		}
	}
	
	/**
	 * Called to alter the scaling of the currently displayed image
	 * @param scale The percent to scale the picture, as a decimal
	 */
	private void resizeImage(double scale) {
		
		int w = (int) (image.getWidth() * scale);
		int h = (int) (image.getHeight() * scale);

		scaled = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		
		//Create graphics object and redraw image at desired scale with high quality
		Graphics2D g = scaled.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g.drawImage(image, 0, 0, w, h, null);
		g.dispose();
		
		setZoomed();
		imageSize = new Dimension(scaled.getWidth(), scaled.getHeight());
		display = scaled;
		setPreferredSize(imageSize);
		updateUI();
		Client.instance().setTitle(name + " - " + (int)(scaleToSpec*100) + "%");
	}
	
	/**
	 * Called to adjust the view port's location when resizing the image or moving the view port
	 * @param point The current mouse position in the component
	 * @param factor How much the image has changed in size
	 */
	public void setViewPort(Point point, double factor){
		
		Rectangle r = getVisibleRect();
		
		//Calculate the correct scaling factor depending on zoom mode
		double fx = factor * display.getWidth();
		double fy = factor * display.getHeight();

		//Calculates the position of the pointer as a fraction of the distance of the full window
		double x = (double) (point.x - r.x) / (double)(r.width * 2);
		double y = (double) (point.y - r.y) / (double)(r.height * 2);
		
		//Keeping the view port in the right place
		double newX = ((double) (r.x + x * fx) / (double)display.getWidth()) * 
				((double)display.getWidth()  + fx);
		double newY = ((double) (r.y + y * fy) / (double)display.getHeight()) * 
				((double)display.getHeight() + fy);

		anchor = new Rectangle((int)newX, (int)newY, (int)r.getWidth(), (int)r.getHeight());
		updateUI();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if(display != null) {
			//Make sure image is properly centered in view port
			int x = (int) ((getSize().getWidth()  - display.getWidth()) / 2);
			int y = (int) ((getSize().getHeight() - display.getHeight()) / 2);

			g.drawImage(display, x, y, null); 	
			
			// Centers view port on anchor point when zooming
			if(justZoomed){
				scrollRectToVisible(anchor);
				justZoomed = false;
			}
		}
	}
	
	//=======================================
	//	INTERNAL CLASSES
	//---------------------------------------

	/**
	 * Private class for handling mouse movements 
	 * @author Harald
	 */
	private class TotalMouseListener implements MouseMotionListener, MouseListener, MouseWheelListener {
		
		private Point mouseAnchor = new Point();
		private int button;
		/**
		 * Executed when a mouse button is held pressed and the mouse is moved.
		 * Handles dragging the picture around and scrolling properly.
		 */
		@Override
		public void mouseDragged(MouseEvent e) {
			Rectangle view = getVisibleRect();
						
			int tempX = e.getXOnScreen();
			int tempY = e.getYOnScreen();
			
			//Calculate angle of movement; 0º is straight up; clock-wise is positive
			double angle = -Math.atan2(-tempX+mouseAnchor.x, -tempY+mouseAnchor.y);
			
			//Add 180º to angle to reverse movement for button 1
			if(button == 1){
				if(angle < 0.0)
					angle = angle + Math.PI;
				else 
					angle = angle - Math.PI;
			}
			
			//Calculate appropriate movement of picture and apply it
			int newX = (int) (view.x + (Math.abs(tempX-mouseAnchor.x)) *   Math.sin(angle));
			int newY = (int) (view.y + (Math.abs(tempY-mouseAnchor.y)) * (-Math.cos(angle)));
			
			if(angle < -Math.PI/2 && angle >= -Math.PI){
				newY = newY + view.height;
			}
			else if(angle < Math.PI && angle >= Math.PI/2){
				newX = newX + view.width;
				newY = newY + view.height;
			}
			else if(angle < Math.PI/2 && angle >= 0.0){
				newX = newX + view.width;
			}
			
			if(button == 1) {
				mouseAnchor = MouseInfo.getPointerInfo().getLocation();
			}
			
			Rectangle r = new Rectangle(newX, newY, 1, 1);
			scrollRectToVisible(r);
			anchor = getVisibleRect();
			setAnchor(anchor);
		}
		
		/**
		 * Used to zoom the image with the scroll wheel on the mouse
		 */
		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			double minZoom = 0.7 * scaleToFit;
			double maxZoom = 2.5;
			
			if(e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL){
				int direction = (int)e.getPreciseWheelRotation();
				
				if(scaleToSpec == minZoom && direction == 1)
					return;
				else if(scaleToSpec == maxZoom && direction == -1)
					return;
				
				//Choose how much to zoom the image
				double scaleFactor;
				if(scaleToSpec > 1.0)
					scaleFactor = (-direction*0.2)*scaleToSpec;
				else
					scaleFactor = -direction*0.1;
				scaleToSpec = scaleToSpec + scaleFactor;
				
				if(scaleToSpec < minZoom)
					scaleToSpec = minZoom;
				else if(scaleToSpec > maxZoom)
					scaleToSpec = maxZoom;
				
				setViewPort(e.getPoint(), scaleFactor);
				resizeImage(scaleToSpec);
			}
		}
		
		/**
		 * Used to switch between scaled to fit and 100% size image
		 */
		@Override
		public void mouseClicked(MouseEvent e) {			
			if(e.getButton() == 1 && image != null){
				double factor;
				if(scaleToSpec == scaleToFit) {
					factor = scaleToFit;
					scaleToSpec = 1.0;
				}
				else {
					factor = 1;
					scaleToSpec = scaleToFit;
				}
				setViewPort(e.getPoint(), factor);
				resizeImage(scaleToSpec);
			}
		}
		
		@Override
		public void mousePressed(MouseEvent e) {
			button = e.getButton();
//			setCursor(new Cursor(Cursor.MOVE_CURSOR));
			
			if(e.getButton() == 4 || e.getButton() == 5){
				int dir = 2*e.getButton() - 9;
				Client.instance().getImage(dir);
			}
		}
		
		/**
		 * Used to continually update mouse location info
		 */
		@Override
		public void mouseMoved(MouseEvent e) {
			mouseAnchor = MouseInfo.getPointerInfo().getLocation();
		}

		@Override
		public void mouseReleased(MouseEvent e) {
//			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
		
		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}
	}
	
	/**
	 * Class for determining how far the image loading has progressed
	 * @author Harald Brege
	 */
	private class ProgressListener implements IIOReadProgressListener {
		
		@Override
		public void imageProgress(ImageReader arg0, float arg1) {
			Client.instance().deliverUpdateProgress(Client.IMAGE_BUFFER_PROGRESS,(int) arg1);
		}

		@Override
		public void imageComplete(ImageReader arg0) {
			Client.instance().deliverUpdateProgress(Client.IMAGE_BUFFER_COMPLETE, 100);
		}
		
		@Override
		public void thumbnailStarted(ImageReader arg0, int arg1, int arg2) {}
		
		@Override
		public void thumbnailProgress(ImageReader arg0, float arg1) {}
		
		@Override
		public void thumbnailComplete(ImageReader arg0) {}
		
		@Override
		public void sequenceStarted(ImageReader arg0, int arg1) {}
		
		@Override
		public void sequenceComplete(ImageReader arg0) {}
		
		@Override
		public void readAborted(ImageReader arg0) {}
		
		@Override
		public void imageStarted(ImageReader arg0, int arg1) {}
		
	}
}
package se.viewer.image.containers;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.event.IIOReadProgressListener;
import javax.imageio.stream.ImageInputStream;

/**
 * Class used to represent an image in the program.
 * @author Harald Brege
 */
public class Image implements Serializable {
	
	private static final long serialVersionUID = 9096868853916959296L;
	
	private String name;
	private byte[] imageData;
	private ArrayList<Tag> tags = new ArrayList<Tag>();
	
	/**
	 * Creates an image container
	 * @param name The name of the image
	 * @param location The location of the image
	 * @param image The image itself
	 * @param tags A list of all tags the image has
	 */
	public Image(String name, BufferedImage image, ArrayList<Tag> tags) {
		this.name = name;
		this.tags = tags;
		
		try {
			long time1 = Calendar.getInstance().getTimeInMillis();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(image, "png", baos );
			baos.flush();
			imageData = baos.toByteArray();
			baos.close();
			long time2 = Calendar.getInstance().getTimeInMillis();
			System.out.println("Time to create image: " + (time2-time1) + " ms");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Image(String name, byte[] imageData, ArrayList<Tag> tags) {
		this.name = name;
		this.imageData = imageData;
		this.tags = tags;
	}
	
	//=======================================
	//	GETTERS
	//---------------------------------------
	
	/**
	 * Called to get the name of the image
	 * @return The image's name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Called to get the actual image for displaying
	 * @return The image as a BufferedImage
	 */
	public BufferedImage getImage(IIOReadProgressListener listener) {
		try {
			InputStream in = new ByteArrayInputStream(imageData);
			ImageInputStream iis = ImageIO.createImageInputStream(in);
			Iterator <ImageReader> iter = ImageIO.getImageReaders(iis);
			ImageReader reader = iter.next();
			reader.setInput(iis);
			reader.addIIOReadProgressListener(listener);
			BufferedImage image = reader.read(0);
			return image;
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Called to get the image's tags
	 * @return The image's tags as an array list
	 */
	public ArrayList<Tag> getTags() {
		return tags;
	}
	
	public void setTags(ArrayList<Tag> tags) {
		this.tags = tags;
	}
}

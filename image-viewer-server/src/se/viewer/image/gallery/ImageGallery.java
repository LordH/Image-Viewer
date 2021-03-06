package se.viewer.image.gallery;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import se.viewer.image.containers.Image;
import se.viewer.image.containers.Tag;
import se.viewer.image.containers.Thumbnail;
import se.viewer.image.database.DatabaseSelector;
import se.viewer.image.server.ClientConnection;

public class ImageGallery implements GalleryInterface {
	
	private int mode;
	
	private ClientConnection client;
	private String imageDir;
	private String thumbnailDir;
	private String user;

	private ImageList images;

	private ImageList list;
	private String tag;
	private int delivered;
	
	protected ImageGallery(ClientConnection client) {
		this.client = client;
		user = client.getClientName();
		images = new ImageList();
		thumbnailDir = "C:\\Users\\Harald\\Pictures\\thumbnails\\" + client.getClientName();
		
		setMode(GalleryInterface.MODE_SERVER);
	}
	
	//=======================================
	//	PUBLIC METHODS
	//---------------------------------------

	@Override
	public void setMode(int mode) {
		if(this.mode == GalleryInterface.MODE_SERVER) {
			this.mode = mode;
			imageDir = "C:\\Users\\Harald\\Pictures\\The sweeter things in life";

			System.out.println("Setting up image list for " + user + " from server-side images");
			setupList(imageDir);
			list = new ImageList(images);
			tag = "";
			delivered = 0;
			checkList();
			
			System.out.println(images.size() + " images in the list");
		}
	}
	
	@Override
	public Image getImage(String image) {
		if(!list.contains(image))
			return null;
		
		File file = new File(list.get(image));
		byte[] data = getFileData(file);
		ArrayList<Tag> tags = DatabaseSelector.getDatabase(client).getTags(image);
		delivered = 0;
		
		return new Image(image, data, tags);
	}

	@Override
	public Image getImage(int dir) {
		String image = list.get(dir);		
		return getImage(image);
	}

	@Override
	public ArrayList<Thumbnail> getThumbnails(Tag tag) {
		ArrayList<Thumbnail> thumbnails = new ArrayList<Thumbnail>();
		
		//Establish new image list if it is a new tag
		if(!tag.getName().equals( this.tag )) {
			ArrayList<String> temp = DatabaseSelector.getDatabase(client).
					getTaggedList(tag.getName());
			list.clear();
			for(String image : temp)
				list.add(images.getLocation(image), image);
			this.tag = tag.getName();
			delivered = 0;
			
			System.out.println("List of size " + list.size() + " set up for tag '" + this.tag + "'");
		}
		
		//Set up array of thumbnails
		int stop = delivered + 20;
		for(; delivered<Math.min(stop, list.size()); delivered++) {
			
			String name = list.getName(delivered);
			File file = new File(thumbnailDir, name + ".png");
			byte[] img = getFileData(file);
			ArrayList<Tag> tags = DatabaseSelector.getDatabase(client).
					getTags(name);
			
			Thumbnail temp = new Thumbnail(name, img, tags);
			thumbnails.add(temp);
		}
		return thumbnails;
	}

//	@Override
//	public ArrayList<Tag> getTags(Tag tag) {
//		ArrayList<Tag> temp = DatabaseSelector.getDB().getCoTags(tag.getName(), user);
//		return temp;
//	}
	
	//=======================================
	//	PRIVATE METHODS
	//---------------------------------------
	
	/**
	 * Called to get a byte array with the data from an image file
	 * @param file The file to be converted
	 * @return The file data as a byte array
	 */
	private byte[] getFileData(File file) {
		try {
			byte[] data = Files.readAllBytes(file.toPath());
			return data;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Fills the image list with all image file names in the directory and sub-directories
	 * @param directory The root directory for all image files
	 * @param paths The list to be filled with image locations
	 */
	private void setupList(String directory) {		
		File file = new File(directory);
		ArrayList<String> folders = new ArrayList<String>();
		
		//Find all image files in the directory and add to the image list
		String[] files = file.list(new FileFinder());
		images.add(directory, files);
		
		//Find all sub-directories in the directory
	    DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {
	        @Override
	        public boolean accept(Path file) throws IOException {
	            return (Files.isDirectory(file));
	        }
	    };
	    Path dir = FileSystems.getDefault().getPath(directory);
	    try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, filter)) {
	        for (Path path : stream)
	            folders.add(directory + "\\" + path.getFileName().toString());
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    
		//Run again for each sub-directory
		if(folders != null)
			for(String folder : folders)
				setupList(folder);
	}
	
	/**
	 * Called to check if the images in the image list are stored in the database
	 */
	private void checkList() {
		
		//Check if images exist in the database and add them if they don't
		ArrayList<String> old = DatabaseSelector.getDatabase(client).getAllImages();		
		
		ArrayList<Image> toAdd = new ArrayList<Image>();
		ArrayList<Tag> tags = new ArrayList<Tag>();
		tags.add(new Tag("tagme", "special"));
		
		for(String name : images) 
			if(!old.contains(name)) 
				toAdd.add(new Image(name, (byte[]) null, tags));
		
		if(!toAdd.isEmpty())
			DatabaseSelector.getDatabase(client).add(toAdd);
		
		//Check thumbnail directory and create new thumbnails as necessary
		File file = new File(thumbnailDir);
		if(!file.exists())
			file.mkdirs();
		String[] found = file.list(new FileFinder());
		
		ArrayList<String> toThumb = images.getList();
		for(String thumb : found) 
			toThumb.remove(images.get(thumb.substring(0, thumb.lastIndexOf("."))));
		
		int i = 1;
		for(String image : toThumb) {
			try {
				File source = new File(image);
				BufferedImage buffimg = ImageIO.read(source);
				
				double scale = 0.0;
				if(buffimg.getWidth() >= buffimg.getHeight())
					scale = 200.0/(double)buffimg.getWidth();
				else
					scale = 200.0/(double)buffimg.getHeight();

				int w = (int) (buffimg.getWidth() * scale);
				int h = (int) (buffimg.getHeight() * scale);
					
				//Create graphics object and redraw image at desired scale with high quality
				BufferedImage scaled = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g = scaled.createGraphics();
				g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, 
						RenderingHints.VALUE_INTERPOLATION_BICUBIC);
				g.drawImage(buffimg, 0, 0, w, h, null);
				
				String name = image.substring( image.lastIndexOf("\\") );
				File output = new File(thumbnailDir, name + ".png");
				output.createNewFile();
				ImageIO.write(scaled, "png", output);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("Created thumbnail " + i++ + "/" + toThumb.size());
		}
	}
	
	private class FileFinder implements FilenameFilter {
		@Override
		public boolean accept(File file, String name) {
			return name.endsWith(".jpg") || name.endsWith(".png");
		}
	}
}

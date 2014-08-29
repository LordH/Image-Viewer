package se.viewer.image.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import se.viewer.image.containers.Image;
import se.viewer.image.containers.Tag;
import se.viewer.image.server.ClientConnection;

/**
 * Class that implements a MySQL database connection
 * @author Harald Brege
 */
public class DatabaseMySQL implements DatabaseInterface {

	private Connection con;
	
	private String dbURL;
	private String dbClass;
	private String dbUser;
	private String dbLogin;
	
	private ArrayList<Tag> tags;
	private ArrayList<String> types;
	
	private int userid;
	
	protected DatabaseMySQL(ClientConnection client)  {
		dbURL = "jdbc:mysql://localhost/imagedb";
		dbClass = "com.mysql.jdbc.Driver";
		dbUser = "LordH";
		dbLogin = "1123581321aA!";
		
		tags = new ArrayList<Tag>();
		
		try {
			Class.forName(dbClass);
			con = DriverManager.getConnection(dbURL, dbUser, dbLogin);
			con.setAutoCommit(false);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean shutDown() {
		try {
			if(!con.isClosed())
				con.close();
			return true;
		} catch (SQLException | NullPointerException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	//=======================================
	//	USER HANDLING METHODS
	//---------------------------------------
	
	@Override
	public boolean authenticateUser(String user, String password) {
		boolean authenticated = false;
		try {
			con.createStatement().execute("LOCK TABLES User READ;");
			
			String query = "SELECT salt, password, userid "
					+ "FROM User "
					+ "WHERE name = ? ;";
			PreparedStatement prep = con.prepareStatement(query);
			prep.setString(1, user);
			ResultSet answer = prep.executeQuery();
			if(answer.next()) {
				String hashedPass = password + answer.getString(1);
				if(hashedPass.equals(answer.getString(2))) {
					authenticated = true;
					userid = answer.getInt(3);
				}
			}
			answer.close();
			
			con.createStatement().execute("UNLOCK TABLES;");
			con.commit();
			
			if(authenticated) {
				tags = getAllTags();
				types = getAllTypes();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return authenticated;
	}
	
	@Override
	public boolean registerUser(String user, String password) {
		String salt = "salt";
		String hashedPass = password + salt;
		
		try {
			con.createStatement().execute("LOCK TABLES User WRITE;");
			
			String query = "SELECT name "
					+ "FROM User "
					+ "WHERE name = ? ;";
			PreparedStatement prep = con.prepareStatement(query);
			prep.setString(1, user);
			ResultSet answer = prep.executeQuery();
			if(answer.next()) {
				con.createStatement().execute("UNLOCK TABLES;");
				con.commit();
				return false;
			}
			
			query = "INSERT INTO User(name, password, salt) "
					+ "VALUES ( ? , ? , ? );";
			prep = con.prepareStatement(query);
			prep.setString(1, user);
			prep.setString(2, hashedPass);
			prep.setString(3, salt);
			prep.executeUpdate();

			con.createStatement().execute("UNLOCK TABLES;");
			con.commit();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	//=======================================
	//	IMAGE HANDLING METHODS
	//---------------------------------------
	
	@Override
	public ArrayList<String> getAllImages() {
		ArrayList<String> images = new ArrayList<String>();
		try {			
			String query = "LOCK TABLES Image READ;";
			con.createStatement().execute(query);
			
			query = "SELECT name "
					+ "FROM Image "
					+ "WHERE user = ? ;";
			PreparedStatement prep = con.prepareStatement(query);
			prep.setInt(1, userid);
			ResultSet answer = prep.executeQuery();
			while(answer.next()) {
				images.add(answer.getString(1));
			}
			answer.close();
			
			query = "UNLOCK TABLES;";
			con.createStatement().execute(query);
			con.commit();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return images;
	}
	
	@Override
	public boolean add(Image image) {
		ArrayList<Image> images = new ArrayList<Image>();
		images.add(image);
		return add(images);
	}

	@Override
	public boolean add(ArrayList<Image> images) {
		try {
			con.createStatement().execute("LOCK TABLES Image WRITE;");
			
			String query = "INSERT INTO Image (name, user) VALUES ";
			for(Image image : images) {
				query += "( ? , ? )";
				if(images.indexOf(image) != (images.size()-1))
					query += ", ";
				else
					query += " ON DUPLICATE KEY UPDATE name=name;";
			}
			PreparedStatement prep = con.prepareStatement(query);
			int i = 1;
			for(Image image : images) {
				prep.setString(i++, image.getName());
				prep.setInt(i++, userid);
			}
			prep.executeUpdate();

			con.createStatement().execute("UNLOCK TABLES;");
			con.commit();
			
			for(Image image : images) 
				updateTags(image);
			return true;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	//=======================================
	//	TAG HANDLING METHODS
	//---------------------------------------
	
	@Override
	public ArrayList<Tag> getAllTags() {
		ArrayList<Tag> tags = new ArrayList<Tag>();
		try {
			con.createStatement().execute("LOCK TABLES Tag T READ, TagType TT READ;");
			
			String query = "SELECT T.name, TT.name "
					+ "FROM Tag T, TagType TT "
					+ "WHERE T.user = ? "
						+ "AND T.type = TT.typeid ;";
			PreparedStatement prep = con.prepareStatement(query);
			prep.setInt(1, userid);
			ResultSet answer = prep.executeQuery();
			while(answer.next()){
				tags.add(new Tag(answer.getString(1), answer.getString(2)));
			}
			answer.close();

			con.createStatement().execute("UNLOCK TABLES;");
			con.commit();
			
			if(tags.size() > 0)
				setTaggedCounts(tags);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tags;
	}
	
	@Override
	public ArrayList<Tag> getTags(String image) {
		ArrayList<Tag> tags = new ArrayList<Tag>();
		try {
			con.createStatement().execute(
					"LOCK TABLES Image I READ, Tag T READ, TagType TT READ, Tagged G READ;");
			
			String query = "SELECT T.name, TT.name "
					+ "FROM Tag T, TagType TT, Tagged G, Image I "
					+ "WHERE I.name = ? "
						+ "AND I.imageid = G.image "
						+ "AND G.tag = T.tagid "
						+ "AND T.type = TT.typeid "
						+ "AND I.user = ? ;";
			PreparedStatement prep = con.prepareStatement(query);
			prep.setString(1, image);
			prep.setInt(2, userid);
			ResultSet answer = prep.executeQuery();
			while(answer.next()) 
				tags.add(new Tag(answer.getString(1), answer.getString(2)));
			answer.close();
			
			con.createStatement().execute("UNLOCK TABLES;");
			con.commit();
			
			if(tags.size() > 0)
				setTaggedCounts(tags);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tags;
	}
	
	@Override
	public ArrayList<String> getTaggedList(String tag) {
		ArrayList<String> list = new ArrayList<String>();
		try {
			con.createStatement().execute(
					"LOCK TABLES Image I READ, Tag T READ, Tagged TGD READ;");
			
			String query = "SELECT I.name "
					+ "FROM Image I, Tagged TGD, Tag T "
					+ "WHERE T.name = ? "
						+ "AND I.imageid = TGD.image "
						+ "AND TGD.tag = T.tagid "
						+ "AND T.user = ? ;";
			PreparedStatement prep = con.prepareStatement(query);
			prep.setString(1, tag);
			prep.setInt(2, userid);
			ResultSet answer = prep.executeQuery();
			
			while (answer.next())
				list.add(answer.getString(1));
			answer.close();
			
			con.createStatement().execute("UNLOCK TABLES;");
			con.commit();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public boolean updateTags(Image image) {
		return updateTags(image.getTags(), image.getName());
	}
	
	@Override
	public boolean updateTags(ArrayList<Tag> tags, String image) {
		ArrayList<Tag> newTags = tags;
		ArrayList<Tag> oldTags = getTags(image);
		ArrayList<Tag> toAdd = new ArrayList<Tag>();
		int imageid = getImageID(image);
		boolean found = false;
		
		for(Tag nTag : newTags) {
			String nName = nTag.getName();
			for(Tag oTag : oldTags)
				if(oTag.getName().equals(nName)) {
					found = true;
					oldTags.remove(oTag);
					break;
				}
			if(!found) 
				toAdd.add(nTag);
			found = false;
		}
		for(Tag tag : toAdd)
			if(!existsTag(tag.getName()))
				add(tag);
		setTagIDs(toAdd);
		
		try {
			//Adding all new tags
			con.createStatement().execute("LOCK TABLES Tagged WRITE;");
			
			String query = "INSERT INTO Tagged (image, tag) VALUES";
			for(int i=0; i<toAdd.size(); i++) {
				query += "( ? , ? )";
				if(i != toAdd.size()-1)
					query += ", ";
				else
					query += " ON DUPLICATE KEY UPDATE image=image;";
			}
			PreparedStatement prep = con.prepareStatement(query);
			int i=1;
			for(Tag tag : toAdd) {
				prep.setInt(i++, imageid);
				prep.setInt(i++, tag.getID());
			}
			if(!toAdd.isEmpty())
				prep.executeUpdate();

			con.createStatement().execute("UNLOCK TABLES;");
			con.createStatement().execute("LOCK TABLES Tagged TGD WRITE;");
			
			//Removing tags that has been removed from the image
			for(Tag tag : oldTags) {
				query = "DELETE FROM Tagged TGD, Tag T "
						+ "WHERE TGD.tag = T.tagid "
							+ "AND T.name = ? "
							+ "AND TGD.image = ? ;";
				prep = con.prepareStatement(query);
				prep.setString(1, tag.getName());
				prep.setInt(2, imageid);
				prep.executeUpdate();
			}
			
			con.createStatement().execute("UNLOCK TABLES;");
			con.commit();
			
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	//=======================================
	//	PRIVATE HELPER METHODS
	//---------------------------------------

	private boolean existsTag(String tag) {
		for(Tag t : tags) 
			if(t.getName().equals(tag)) 
				return true;
		return false;
	}

	private boolean add(Tag tag) {
		try {
			if(!types.contains(tag.getType())) 
				add(tag.getType());			
			
			con.createStatement().execute("LOCK TABLES Tag WRITE, TagType READ;");
			
			int typeid = 0;
			String query = "SELECT typeid "
					+ "FROM TagType "
					+ "WHERE name = ? ;";
			PreparedStatement prep = con.prepareStatement(query);
			prep.setString(1, tag.getType());
			ResultSet answer = prep.executeQuery();
			if(answer.next())
				typeid = answer.getInt(1);
			
			query = "INSERT INTO Tag (name, type, user) VALUES ( ? , ? , ? );";
			prep = con.prepareStatement(query);
			prep.setString(1, tag.getName());
			prep.setInt(2, typeid);
			prep.setInt(3, userid);
			prep.executeUpdate();
			
			con.createStatement().execute("UNLOCK TABLES;");
			con.commit();
			
			tags.add(tag);
			return true;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Adds a new tag type to the database
	 * @param tagType The type to add
	 * @return Success or failure
	 */
	private boolean add(String tagType) {
		try {			
			con.createStatement().execute("LOCK TABLES TagType WRITE;");
			
			String query = "INSERT INTO TagType (name, user) VALUES ( ? , ? );";
			PreparedStatement prep = con.prepareStatement(query);
			prep.setString(1, tagType);
			prep.setInt(2, userid);
			prep.executeUpdate();
			
			con.createStatement().execute("UNLOCK TABLES;");
			con.commit();
			
			types.add(tagType);
			return true;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Sets the numbers of images tagged for a list of tags
	 * @param tags The list
	 * @return Success or failure
	 */
	private boolean setTaggedCounts(ArrayList<Tag> tags) {
		try {
			con.createStatement().execute("LOCK TABLES Tag T READ, Tagged TGD READ;");
			
			String query = "SELECT T.name, Count(TGD.image) "
					+ "FROM Tag T, Tagged TGD "
					+ "WHERE TGD.tag = T.tagid "
						+ "AND T.user = ? "
						+ "AND T.name IN (";
			for(int i=0; i<tags.size(); i++) 
				if(i == tags.size() -1)
					query = query + " ? ) ";
				else
					query = query + " ? ,";	
			query = query + "GROUP BY T.name;";
			PreparedStatement prep = con.prepareStatement(query);
			prep.setInt(1, userid);
			int n = 2;
			for(Tag tag : tags) 
				prep.setString(n++, tag.getName());
			ResultSet answer = prep.executeQuery();
			
			while(answer.next()) 
				for(Tag tag : tags) 
					if(tag.getName().equals(answer.getString(1))) {
						tag.setCount(answer.getInt(2));
						break;
					}
			
			con.createStatement().execute("UNLOCK TABLES;");
			con.commit();
			return true;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Called to get the database id number of a certain image
	 * @param image The image in question
	 * @param user The name of the user that owns the image
	 * @return The image id number, or 0 if no such image exists
	 */
	private int getImageID(String image) {
		int imageid = 0;
		try {
			con.createStatement().execute("LOCK TABLES Image READ;");
			
			String query = "SELECT imageid FROM Image "
					+ "WHERE name = ? AND user = ? ;";
			PreparedStatement prep = con.prepareStatement(query);
			prep.setString(1, image);
			prep.setInt(2, userid);
			ResultSet answer = prep.executeQuery();
			if(answer.next())
				imageid = answer.getInt(1);
			answer.close();
			
			con.createStatement().execute("UNLOCK TABLES;");
			con.commit();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		return imageid;
	}
	
	private void setTagIDs(ArrayList<Tag> tags) {
		try {
			con.createStatement().execute("LOCK TABLES Tag READ;");
			
			String query = "SELECT name, tagid "
					+ "FROM Tag "
					+ "WHERE user = ? "
						+ "AND name IN (";
			for(int i=0; i<tags.size(); i++) 
				if(i == tags.size() -1)
					query = query + " ? ) ";
				else
					query = query + " ? ,";	
			
			PreparedStatement prep = con.prepareStatement(query);
			prep.setInt(1, userid);
			int n = 2;
			for(Tag tag : tags) 
				prep.setString(n++, tag.getName());
			ResultSet answer = prep.executeQuery();
			
			while(answer.next()) 
				for(Tag tag : tags) 
					if(answer.getString(1).equals(tag.getName())) {
						tag.setID(answer.getInt(2));
						break;
					}
			answer.close();

			con.createStatement().execute("UNLOCK TABLES;");
			con.commit();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private ArrayList<String> getAllTypes() {
		ArrayList<String> types = new ArrayList<String>();
		try {
			con.createStatement().execute("LOCK TABLES TagType READ;");
			
			String query = "SELECT name FROM TagType "
					+ "WHERE user = ? ;";
			PreparedStatement prep = con.prepareStatement(query);
			prep.setInt(1, userid);
			ResultSet answer = prep.executeQuery();
			while(answer.next())
				types.add(answer.getString(1));
			answer.close();
			
			con.createStatement().execute("UNLOCK TABLES;");
			con.commit();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		return types;
	}
}
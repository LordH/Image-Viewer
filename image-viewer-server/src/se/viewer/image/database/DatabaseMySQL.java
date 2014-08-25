package se.viewer.image.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import se.viewer.image.containers.Image;
import se.viewer.image.containers.Tag;

/**
 * Class that implements a MySQL database connection
 * @author Harald Brege
 */
public class DatabaseMySQL implements DatabaseInterface {
	
	private String dbURL;
	private String dbClass;
	private String dbUser;
	private String dbLogin;
	
	private Connection con;
	
	private static DatabaseInterface instance;
	
	private DatabaseMySQL()  {
		dbURL = "jdbc:mysql://localhost/imagedb";
		dbClass = "com.mysql.jdbc.Driver";
		dbUser = "LordH";
		dbLogin = "1123581321aA!";
		
		try {
			Class.forName(dbClass);
			con = DriverManager.getConnection(dbURL, dbUser, dbLogin);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Called to get the singleton database instance
	 * @return The instance of MySQL
	 */
	protected static DatabaseInterface instance() {
		if(instance == null) {
			instance = new DatabaseMySQL();
		}
		return instance;
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
		try {
			boolean authenticated = false;

			String query = "SELECT password FROM User WHERE name = ? ;";
			PreparedStatement prep = con.prepareStatement(query);
			prep.setString(1, user);
			ResultSet answer = prep.executeQuery();
			
			if(answer.isBeforeFirst()) {
				answer.next();
				String dbPass = answer.getString(1);
				if(password.equals(dbPass))
					authenticated = true;
			}
			return authenticated;
			
		} catch (SQLException e) {
			System.err.println("\n--- COULD NOT AUTHENTICATE USER " + user.toUpperCase() + " ---");
			System.err.println("/// REASON: " + e.getMessage() + " \\\\\\");
			return false;
		}
	}
	
	@Override
	public boolean registerUser(String name, String password) {
		try {
			String query = "INSERT INTO User (name, password) VALUES ( ? , ? );";
			PreparedStatement prep = con.prepareStatement(query);
			prep.setString(1, name);
			prep.setString(2, password);
			prep.executeUpdate();
			
			System.out.println("--- USER " + name.toUpperCase() + " REGISTERED ---");
			return true;
		} catch (SQLException e) {
			System.err.println("\n--- COULD NOT REGISTER USER " + name.toUpperCase() + " ---");
			System.err.println("/// REASON: " + e.getMessage() + " \\\\\\");
			return false;
		}
	}
	
	@Override
	public int getUserID(String user) {
		int userid = 0;
		try {
			String query = "SELECT userid FROM User WHERE name = ? ;";
			PreparedStatement prep = con.prepareStatement(query);
			prep.setString(1, user);
			ResultSet answer = prep.executeQuery();
			
			if(answer.isBeforeFirst()) {
				answer.next();
				userid = answer.getInt(1);
			}
			return userid;
			
		} catch (SQLException e) {
			System.err.println("\n--- COULD NOT LOOKUP USER " + user.toUpperCase() + " ---");
			System.err.println("/// REASON: " + e.getMessage() + " \\\\\\");
			return 0;	
		}	
	}
	
	//=======================================
	//	IMAGE HANDLING METHODS
	//---------------------------------------
	
	@Override
	public int getImageID(String image, String user) {
		int imageid = 0;
		
		try {
			String query = "SELECT imageid FROM Image I, User U WHERE I.name = ? "
					+ "AND I.user = U.userid AND U.name = ? ;";
			PreparedStatement prep = con.prepareStatement(query);
			prep.setString(1, image);
			prep.setString(2, user);
			ResultSet answer = prep.executeQuery();
			
			if(answer.isBeforeFirst()) {
				answer.next();
				imageid = answer.getInt(1);
			}
		} catch (SQLException e) {
			System.err.println("\n--- COULD NOT FETCH IMAGE ID FOR " + image + " ---");
			System.err.println("/// REASON: " + e.getMessage() + " \\\\\\");
		}	
		return imageid;
	}
	
	@Override
	public boolean existsImage(String image, String user) {
		int exists = getImageID(image, user);
		if(exists == 0)
			return false;
		else
			return true;
	}
	
	@Override
	public ArrayList<String> getAllImages(String user) {
		ArrayList<String> images = new ArrayList<String>();
		try {
			String query = "SELECT I.name FROM Image I, User U "
					+ "WHERE I.user = U.userid AND U.name = ? ;";
			PreparedStatement prep = con.prepareStatement(query);
			prep.setString(1, user);
			ResultSet answer = prep.executeQuery();
			
			while(answer.next()) {
				images.add(answer.getString(1));
			}
			
		} catch (SQLException e) {
			System.err.println("\n--- COULD NOT LOOKUP IMAGES ---");
			System.err.println("/// REASON: " + e.getMessage() + " \\\\\\");
		}
		return images;
	}
	
	@Override
	public boolean add(Image image, String user) {
		int userid = getUserID(user);
		try {
			String query = "INSERT INTO Image (name, user) VALUES "
					+ "( ? , ?) ON DUPLICATE KEY UPDATE imageid = imageid;";
			PreparedStatement prep = con.prepareStatement(query);
			prep.setString(1, image.getName());
			prep.setInt(2, userid);
			prep.executeUpdate();

			return updateTags(image, user);
		} catch (SQLException e) {
			System.err.println("\n--- COULD NOT ADD IMAGE " + image + " ---");
			System.err.println("/// REASON: " + e.getMessage() + " \\\\\\");
			return false;
		}
	}

	@Override
	public boolean add(ArrayList<Image> images, String user) {
		int userid = getUserID(user);

		try {
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

			for(Image image : images) 
				updateTags(image, user);
			
			System.out.println(user.toUpperCase() + " added " + images.size() 
					+ " images to their database");
			return true;
			
		} catch (SQLException e) {
			System.err.println("\n--- COULD NOT ADD IMAGES ---");
			System.err.println("/// REASON: " + e.getMessage() + " \\\\\\");
			return false;
		}
	}
	
	@Override
	public ArrayList<Tag> getTags(String image, String user) {
		ArrayList<Tag> tags = new ArrayList<Tag>();
		
		try {
			String query = "SELECT T.name, Y.name FROM Tag T, TagType Y, Tagged G WHERE " 
					+ "G.image = ? AND G.tag = T.tagid AND T.type = Y.typeid;";
			PreparedStatement prep = con.prepareStatement(query);
			prep.setInt(1, getImageID(image, user));
			ResultSet answer = prep.executeQuery();
			
			while(answer.next()) {
				Tag temp = new Tag(answer.getString(1), answer.getString(2));
//				temp.setCount(getTaggedCount(temp.getName(), user));
				tags.add(temp);
			}
			setTagCounts(tags, user);
			
			
		} catch (SQLException e) {
			System.err.println("\n--- COULD NOT RETRIEVE TAGS FOR "+ image + " ---");
			System.err.println("/// REASON: " + e.getMessage() + " \\\\\\");
		}
		return tags;
	}
	
	@Override
	public boolean updateTags(Image image, String user) {
		return updateTags(image.getTags(), image.getName(), user);
	}
	
	@Override
	public boolean updateTags(ArrayList<Tag> tags, String image, String user) {
		ArrayList<Tag> newTags = tags;
		ArrayList<Tag> oldTags = getTags(image, user);
		ArrayList<Tag> toAdd = new ArrayList<Tag>();
		int imageid = getImageID(image, user);
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
				
		try {
			//Adding all new tags
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
				if(!existsTag(tag.getName(), user)) 
					add(tag, user);
				
				prep.setInt(i++, imageid);
				prep.setInt(i++, getTagID(tag.getName(), user));
			}
			if(!toAdd.isEmpty())
				prep.executeUpdate();

			//Removing tags that has been removed from the image
			for(Tag tag : oldTags) {
				query = "DELETE FROM Tagged WHERE tag = ? AND image = ? ;";
				prep = con.prepareStatement(query);
				prep.setInt(1, getTagID(tag.getName(), user));
				prep.setInt(2, imageid);
				prep.executeUpdate();
			}		
			
			return true;
		} catch (SQLException e) {
			System.err.println("\n--- COULD NOT UPDATE TAGS FOR " + image + " ---");
			System.err.println("/// REASON: " + e.getMessage() + " \\\\\\");
			return false;
		}
	}
	
	//=======================================
	//	TAG HANDLING METHODS
	//---------------------------------------
	
	@Override
	public int getTagID(String tag, String user) {
		int tagid = 0;
		try {
			String query = "SELECT tagid FROM Tag T, User U WHERE T.name = ? "
					+ "AND T.user = U.userid AND U.name = ? ;";
			PreparedStatement prep = con.prepareStatement(query);
			prep.setString(1, tag);
			prep.setString(2, user);
			ResultSet answer = prep.executeQuery();
			
			if(answer.isBeforeFirst()) {
				answer.next();
				tagid = answer.getInt(1);
			}
			
		} catch (SQLException e) {
			System.err.println("\n--- COULD NOT RETRIEVE TAG ID ---");
			System.err.println("/// REASON: " + e.getMessage() + " \\\\\\");
		}	
		return tagid;	
	}
	
	@Override
	public ArrayList<Tag> getAllTags(String user) {
		ArrayList<Tag> tags = new ArrayList<Tag>();
		try {
			String query = "SELECT T.name, T.type FROM Tag T, User U "
					+ "WHERE T.user = U.userid AND U.name = ? ;";
			PreparedStatement prep = con.prepareStatement(query);
			prep.setString(1, user);
			ResultSet answer = prep.executeQuery();
			
			while(answer.next()){
				tags.add(new Tag(answer.getString(1), answer.getString(2)));
			}
			setTagCounts(tags, user);
			
		} catch (SQLException e) {
			System.err.println("\n--- COULD GET TAGS FOR USER " + user + " ---");
			System.err.println("/// REASON: " + e.getMessage() + " \\\\\\");
		}
		
		return tags;
	}
	
	@Override
	public boolean existsTag(String tag, String user) {
		int exists = getTagID(tag, user);
		if(exists == 0)
			return false;
		else
			return true;
	}

//	@Override
//	public ArrayList<Tag> getCoTags(String tag, String user) {
//		ArrayList<Tag> tags = new ArrayList<Tag>();
//		try {
//			String query = 
//					"SELECT COUNT(*) 'Images', T.name 'Name', TT.name 'Type' "
//					+ "FROM Tag T, Tagged TGD, TagType TT "
//					+ "WHERE TGD.tag = T.tagid	"
//						+ "AND T.type = TT.typeid "
//						+ "AND TGD.image IN "
//							+ "(SELECT I.imageid FROM Tagged TGD, Image I "
//							+ "WHERE TGD.image = I.imageid AND TGD.tag = ? AND I.user = ?) "
//					+ "GROUP BY T.Name "
//					+ "ORDER BY 'Images';";
//			
//			PreparedStatement prep = con.prepareStatement(query);
//			prep.setInt(1, getTagID(tag, user));
//			prep.setInt(2, getUserID(user));
//			ResultSet answer = prep.executeQuery();
//			
//			for(int i=0; i<20 || !answer.isAfterLast(); i++) {
//				answer.next();
//				Tag temp = new Tag(answer.getString(2), answer.getString(3));
//				temp.setCount(answer.getInt(1));
//				tags.add(temp);
//			}
//		} catch (SQLException e) {
//			System.err.println("\n--- COULD NOT GET TAG INFO FOR TAG " + tag + " ---");
//			System.err.println("/// REASON: " + e.getMessage() + " \\\\\\");
//			e.printStackTrace();
//		}
//		return tags;
//	}

	@Override
	public boolean add(Tag tag, String user) {
		try {
			if(!existsTagType(tag.getType(), user)) 
				add(tag.getType(), user);			

			String query = "INSERT INTO Tag (name, type, user) VALUES ( ? , ? , ? );";
			PreparedStatement prep = con.prepareStatement(query);
			prep.setString(1, tag.getName());
			prep.setInt(2, getTagTypeID(tag.getType(), user));
			prep.setInt(3, getUserID(user));
			prep.executeUpdate();
			return true;
			
		} catch (SQLException e) {
			System.err.println("\n--- COULD NOT ADD TAG " + tag.getName() + " ---");
			System.err.println("/// REASON: " + e.getMessage() + " \\\\\\");
			return false;
		}
	}
	
//	@Override
//	public int getTaggedCount(String tag, String user) {
//		int count = 0;
//		
//		try {
//			String query = "SELECT Count(image) FROM Tagged WHERE tag = ? ;";
//			PreparedStatement prep = con.prepareStatement(query);
//			prep.setInt(1, getTagID(tag, user));
//			ResultSet answer = prep.executeQuery();
//
//			if(answer.isBeforeFirst()) {
//				answer.next();
//				count = answer.getInt(1);
//			}
//		} catch (SQLException e) {
//			System.err.println("\n--- COULD NOT RETRIEVE TAG COUNT ---");
//			System.err.println("/// REASON: " + e.getMessage() + " \\\\\\");
//		}
//		return count;
//	}
	
	public void setTagCounts(ArrayList<Tag> tags, String user) {
		try {
			String query = "SELECT T.name, Count(TGD.image) FROM Tag T, Tagged TGD, User U "
			+ "WHERE TGD.tag = T.tagid "
				+ "AND T.user = U.userid "
				+ "AND U.name = ? "
				+ "AND T.name IN (";
			for(int i=0; i<tags.size(); i++) 
				if(i == tags.size() -1)
					query = query + " ? ) ";
				else
					query = query + " ? ,";	
			query = query + "GROUP BY T.name;";
			
			PreparedStatement prep = con.prepareStatement(query);
			prep.setString(1, user);
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
		} catch (SQLException e) {
			System.err.println("\n--- COULD NOT RETRIEVE TAG COUNT ---");
			System.err.println("/// REASON: " + e.getMessage() + " \\\\\\");
		}
	}
	
	@Override
	public ArrayList<String> getTaggedList(String tag, String user) {
		ArrayList<String> list = new ArrayList<String>();
		try {
			String query = "SELECT I.name FROM Image I, Tagged T "
					+ "WHERE I.imageid = T.image AND T.tag = ? ;";
			PreparedStatement prep = con.prepareStatement(query);
			prep.setInt(1, getTagID(tag, user));
			ResultSet answer = prep.executeQuery();
			
			while (answer.next()) {
				list.add(answer.getString(1));
			}
		} catch (SQLException e) {
			System.err.println("\n--- COULD NOT RETRIEVE TAGGED LIST ---");
			System.err.println("/// REASON: " + e.getMessage() + " \\\\\\");
		}
		return list;
	}
	
	//=======================================
	//	TAG TYPE HANDLING METHODS
	//---------------------------------------
	
	@Override
	public int getTagTypeID(String type, String user) {
		int typeid = 0;
		try {
			String query = "SELECT typeid FROM TagType T, User U WHERE T.name = ? "
					+ "AND T.user = U.userid AND U.name = ? ;";
			PreparedStatement prep = con.prepareStatement(query);
			prep.setString(1, type);
			prep.setString(2, user);
			ResultSet answer = prep.executeQuery();
			
			if(answer.isBeforeFirst()) {
				answer.next();
				typeid = answer.getInt(1);
			}
		} catch (SQLException e) {
			System.err.println("\n--- COULD NOT RETRIEVE TAG TYPE ID ---");
			System.err.println("/// REASON: " + e.getMessage() + " \\\\\\");
		}	
		return typeid;	
	}
	
	@Override
	public boolean existsTagType(String type, String user) {
		int exists = getTagTypeID(type, user);
		if(exists == 0)
			return false;
		else
			return true;
	}
	
	@Override
	public boolean add(String tagType, String user) {
		try {
			int userid = getUserID(user);
			
			String query = "INSERT INTO TagType (name, user) VALUES ( ? , ? );";
			PreparedStatement prep = con.prepareStatement(query);
			prep.setString(1, tagType);
			prep.setInt(2, userid);
			prep.executeUpdate();
			return true;

		} catch (SQLException e) {
			System.err.println("\n--- COULD NOT ADD TAG TYPE ---");
			System.err.println("/// REASON: " + e.getMessage() + " \\\\\\");
		}
		return false;
	}
}
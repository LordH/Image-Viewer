package se.viewer.image.database;

public class DatabaseSelector {

	public static DatabaseInterface getDB() {
		return DatabaseMySQL.instance();
	}
}

package se.viewer.image.server;

public class ServerLauncher {

	public static void main(String[] args) {
		(new Thread(Server.instance())).start();
	}
}

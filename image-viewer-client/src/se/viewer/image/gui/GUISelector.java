package se.viewer.image.gui;


public class GUISelector {

	public static GUIHandlerInterface getFactory() {
		return new GUIHandler();
	}
}

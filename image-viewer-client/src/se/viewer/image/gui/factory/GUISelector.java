package se.viewer.image.gui.factory;


public class GUISelector {

	public static GUIHandlerInterface getFactory() {
		return new GUIHandler();
	}
}

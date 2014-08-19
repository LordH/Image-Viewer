package se.viewer.image.gui;


public class GUIFactorySelector {

	public static GUIFactoryInterface getFactory() {
		return new GUIFactory();
	}
}

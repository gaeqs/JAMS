package net.jamsimulator.jams.gui.theme;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.paint.Paint;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.theme.event.SelectedThemeChangeEvent;

public class ThemedScene extends Scene {

	public ThemedScene(Parent root) {
		super(root);
		initializeJamsListeners();
	}

	public ThemedScene(Parent root, double width, double height) {
		super(root, width, height);
		initializeJamsListeners();
	}

	public ThemedScene(Parent root, Paint fill) {
		super(root, fill);
		initializeJamsListeners();
	}

	public ThemedScene(Parent root, double width, double height, Paint fill) {
		super(root, width, height, fill);
		initializeJamsListeners();
	}

	public ThemedScene(Parent root, double width, double height, boolean depthBuffer) {
		super(root, width, height, depthBuffer);
		initializeJamsListeners();
	}

	public ThemedScene(Parent root, double width, double height, boolean depthBuffer, SceneAntialiasing antiAliasing) {
		super(root, width, height, depthBuffer, antiAliasing);
		initializeJamsListeners();
	}

	private void initializeJamsListeners() {
		JamsApplication.getThemeManager().registerListeners(this);
		JamsApplication.getThemeManager().getSelected().apply(this);
	}

	public void unregisterJamsListeners() {
		JamsApplication.getThemeManager().unregisterListeners(this);
	}

	@Listener
	public void onThemeChange(SelectedThemeChangeEvent.After event) {
		event.getNewTheme().apply(this);
	}

}

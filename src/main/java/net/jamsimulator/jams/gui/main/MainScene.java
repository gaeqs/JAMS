package net.jamsimulator.jams.gui.main;

import javafx.scene.Parent;
import javafx.scene.SceneAntialiasing;
import javafx.scene.paint.Paint;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.action.event.ActionBindEvent;
import net.jamsimulator.jams.gui.action.event.ActionUnbindEvent;
import net.jamsimulator.jams.gui.theme.ThemedScene;
import net.jamsimulator.jams.gui.theme.event.SelectedThemeChangeEvent;

/**
 * Represents the main scene. This class listens both the {@link net.jamsimulator.jams.manager.ThemeManager} and
 * the {@link net.jamsimulator.jams.manager.ActionManager}.
 */
public class MainScene extends ThemedScene {

	public MainScene(Parent root) {
		super(root);
	}

	public MainScene(Parent root, double width, double height) {
		super(root, width, height);
	}

	public MainScene(Parent root, Paint fill) {
		super(root, fill);
	}

	public MainScene(Parent root, double width, double height, Paint fill) {
		super(root, width, height, fill);
	}

	public MainScene(Parent root, double width, double height, boolean depthBuffer) {
		super(root, width, height, depthBuffer);
	}

	public MainScene(Parent root, double width, double height, boolean depthBuffer, SceneAntialiasing antiAliasing) {
		super(root, width, height, depthBuffer, antiAliasing);
	}

	@Override
	protected void initializeJamsListeners() {
		super.initializeJamsListeners();
		JamsApplication.getActionManager().registerListeners(this);
	}

	public void unregisterJamsListeners() {
		JamsApplication.getThemeManager().unregisterListeners(this);
		JamsApplication.getActionManager().unregisterListeners(this);
	}

	@Listener
	public void onThemeChange(SelectedThemeChangeEvent.After event) {
		event.getNewTheme().apply(this);
	}

	@Listener
	public void onActionBind(ActionBindEvent.After event) {
		JamsApplication.getActionManager().addAcceleratorsToScene(this, true);
	}

	@Listener
	public void onActionUnbind(ActionUnbindEvent.After event) {
		JamsApplication.getActionManager().addAcceleratorsToScene(this, true);
	}

}

package net.jamsimulator.jams.gui.main;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import net.jamsimulator.jams.gui.JamsApplication;

/**
 * The main {@link MenuBar}.
 */
public class MainMenuBar extends MenuBar {

	public MainMenuBar() {
		loadDefaults();
	}


	private void loadDefaults() {
		Menu file = new Menu("File");
		getMenus().add(file);

		MenuItem exit = new MenuItem("Exit");
		exit.setOnAction(event -> JamsApplication.getStage().close());
		file.getItems().add(exit);
	}
}

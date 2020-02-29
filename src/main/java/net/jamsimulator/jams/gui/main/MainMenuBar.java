package net.jamsimulator.jams.gui.main;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.language.wrapper.LanguageMenu;
import net.jamsimulator.jams.language.wrapper.LanguageMenuItem;
import net.jamsimulator.jams.language.Messages;

/**
 * The main {@link MenuBar}.
 */
public class MainMenuBar extends MenuBar {

	public MainMenuBar() {
		loadDefaults();
	}


	private void loadDefaults() {
		Menu file = new LanguageMenu(Messages.MAIN_MENU_FILE);
		getMenus().add(file);

		MenuItem exit = new LanguageMenuItem(Messages.MAIN_MENU_FILE_EXIT);
		exit.setOnAction(event -> JamsApplication.getStage().close());
		file.getItems().add(exit);
	}
}

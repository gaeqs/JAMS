package net.jamsimulator.jams.gui.main;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.configuration.Configuration;
import net.jamsimulator.jams.configuration.RootConfiguration;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.settings.ConfigurationWindow;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageMenu;
import net.jamsimulator.jams.language.wrapper.LanguageMenuItem;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStreamReader;

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

		ConfigurationWindow window;
		try {
			Configuration types = new RootConfiguration(new InputStreamReader(Jams.class.getResourceAsStream(
					"/configuration/main_config_meta.jconfig")));
			window = new ConfigurationWindow(Jams.getMainConfiguration(), types);

		} catch (IOException | ParseException e) {
			throw new RuntimeException(e);
		}
		MenuItem settings = new LanguageMenuItem(Messages.MAIN_MENU_FILE_SETTINGS);
		settings.setOnAction(event -> window.open());
		file.getItems().add(settings);
	}
}

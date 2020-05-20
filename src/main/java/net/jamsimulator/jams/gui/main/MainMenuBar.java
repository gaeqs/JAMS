/*
 * MIT License
 *
 * Copyright (c) 2020 Gael Rial Costas
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.jamsimulator.jams.gui.main;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.DirectoryChooser;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.configuration.Configuration;
import net.jamsimulator.jams.configuration.RootConfiguration;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.configuration.ConfigurationWindow;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageMenu;
import net.jamsimulator.jams.language.wrapper.LanguageMenuItem;
import net.jamsimulator.jams.project.mips.MipsProject;

import java.io.File;
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

		MenuItem openProject = new LanguageMenuItem(Messages.MAIN_MENU_FILE_OPEN_PROJECT);
		openProject.setOnAction(event -> {
			DirectoryChooser chooser = new DirectoryChooser();
			File folder = chooser.showDialog(JamsApplication.getStage());
			if(folder == null || JamsApplication.getProjectsTabPane().isProjectOpen(folder)) return;
			JamsApplication.getProjectsTabPane().openProject(new MipsProject("JAMSProject", folder));
		});
		file.getItems().add(openProject);

		ConfigurationWindow window;
		try {
			Configuration types = new RootConfiguration(new InputStreamReader(Jams.class.getResourceAsStream(
					"/configuration/main_config_meta.jconfig")));
			window = new ConfigurationWindow(Jams.getMainConfiguration(), types);

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		MenuItem settings = new LanguageMenuItem(Messages.MAIN_MENU_FILE_SETTINGS);
		settings.setOnAction(event -> window.open());
		file.getItems().add(settings);
	}
}

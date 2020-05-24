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

package net.jamsimulator.jams.gui.configuration;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.configuration.Configuration;
import net.jamsimulator.jams.configuration.RootConfiguration;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.configuration.explorer.ConfigurationWindowExplorer;
import net.jamsimulator.jams.gui.configuration.explorer.ConfigurationWindowSection;
import net.jamsimulator.jams.gui.configuration.explorer.node.ConfigurationWindowNode;
import net.jamsimulator.jams.gui.image.icon.Icons;
import net.jamsimulator.jams.gui.theme.ThemedScene;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.event.SelectedLanguageChangeEvent;

import java.io.IOException;
import java.util.List;

public class ConfigurationWindow extends SplitPane {

	private static final int WIDTH = 900;
	private static final int HEIGHT = 600;

	private Stage stage;

	private final RootConfiguration configuration;
	private final Configuration meta;

	private final ConfigurationWindowExplorer explorer;
	private final ScrollPane explorerScrollPane;

	private final SectionTreeDisplay sectionTreeDisplay;
	private final VBox sectionDisplay;
	private final VBox basicSectionContents;

	public ConfigurationWindow(RootConfiguration configuration, Configuration meta) {
		this.stage = null;
		this.configuration = configuration;
		this.meta = meta;

		explorerScrollPane = new ScrollPane();
		explorerScrollPane.setFitToHeight(true);
		explorerScrollPane.setFitToWidth(true);
		this.explorer = new ConfigurationWindowExplorer(this, explorerScrollPane);
		explorerScrollPane.setContent(explorer);

		explorerScrollPane.getContent().addEventHandler(ScrollEvent.SCROLL, scrollEvent -> {
			double deltaY = scrollEvent.getDeltaY() * 0.003;
			explorerScrollPane.setVvalue(explorerScrollPane.getVvalue() - deltaY);
		});

		this.sectionTreeDisplay = new SectionTreeDisplay();

		this.sectionDisplay = new VBox();
		this.sectionDisplay.getStyleClass().add("configuration-window-display");

		this.basicSectionContents = new VBox();
		this.basicSectionContents.setPadding(new Insets(5, 0, 0, 5));
		this.basicSectionContents.getStyleClass().add("configuration-window-display-contents");

		sectionDisplay.getChildren().add(sectionTreeDisplay);

		init();
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public Configuration getMeta() {
		return meta;
	}

	public Stage getStage() {
		return stage;
	}

	private void init() {
		getItems().add(explorerScrollPane);
		getItems().add(sectionDisplay);
		SplitPane.setResizableWithParent(explorerScrollPane, false);
	}

	public void display(ConfigurationWindowSection section) {
		while (sectionDisplay.getChildren().size() > 1) {
			sectionDisplay.getChildren().remove(1);
		}

		if (section.isSpecial()) {
			Node node = section.getSpecialNode();
			if (node instanceof Region) {
				((Region) node).prefHeightProperty().bind(sectionDisplay.heightProperty()
						.subtract(sectionTreeDisplay.heightProperty()));
			}
			sectionDisplay.getChildren().add(node);
		} else {
			displayNormalSection(section);
		}

		sectionTreeDisplay.setSection(section);
	}

	private void displayNormalSection(ConfigurationWindowSection section) {
		basicSectionContents.getChildren().clear();

		List<ConfigurationWindowNode<?>> nodes = section.getNodes();
		String currentRegion = null;

		for (ConfigurationWindowNode<?> node : nodes) {
			if (currentRegion == null || !currentRegion.equals(node.getRegion())) {
				currentRegion = node.getRegion();
				if (currentRegion != null) {
					basicSectionContents.getChildren().add(new ConfigurationRegionDisplay(section, currentRegion));
				}
			}
			basicSectionContents.getChildren().add(node);
		}


		sectionDisplay.getChildren().add(basicSectionContents);
	}

	public void open() {
		if (stage == null) {
			stage = new Stage();
			Scene scene = new ThemedScene(this);
			stage.initOwner(JamsApplication.getStage());
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setScene(scene);

			stage.setWidth(WIDTH);
			stage.setHeight(HEIGHT);
			stage.setMinWidth(WIDTH >> 1);
			stage.setMinHeight(0);

			Stage main = JamsApplication.getStage();

			stage.setX(main.getX() + main.getWidth() / 2 - (WIDTH >> 1));
			stage.setY(main.getY() + main.getHeight() / 2 - (HEIGHT >> 1));

			stage.setTitle(Jams.getLanguageManager().getSelected().getOrDefault(Messages.CONFIG));
			JamsApplication.getIconManager().getOrLoadSafe(Icons.LOGO, Icons.LOGO_PATH, 250, 250)
					.ifPresent(stage.getIcons()::add);


			stage.setOnCloseRequest(event -> {
				try {
					configuration.save(true);
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			Jams.getLanguageManager().registerListeners(this, true);
		}

		stage.setOnShown(target -> Platform.runLater(() ->
				Platform.runLater(() -> setDividerPosition(0, 0.3))));

		stage.show();
	}

	@Listener
	public void onSelectedLanguageChange(SelectedLanguageChangeEvent.After event) {
		stage.setTitle(Jams.getLanguageManager().getSelected().getOrDefault(Messages.CONFIG));
	}
}

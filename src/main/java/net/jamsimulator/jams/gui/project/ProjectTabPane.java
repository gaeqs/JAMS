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

package net.jamsimulator.jams.gui.project;

import javafx.application.Platform;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import net.jamsimulator.jams.gui.mips.project.MIPSStructurePane;
import net.jamsimulator.jams.language.wrapper.LanguageTab;
import net.jamsimulator.jams.project.mips.MIPSProject;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class ProjectTabPane extends TabPane {

	private final ProjectTab projectTab;

	private final WorkingPane workingPane;

	private final Consumer<Tab> onClose;

	public ProjectTabPane(ProjectTab projectTab, BiConsumer<Tab, Tab> onSelect, Consumer<Tab> onClose) {
		getStyleClass().add("project-tab-pane");
		setTabClosingPolicy(TabClosingPolicy.ALL_TABS);
		this.projectTab = projectTab;
		this.onClose = onClose;

		projectTab.addTabCloseListener(event -> {
			for (Tab tab : getTabs()) {
				if (tab.getContent() instanceof ProjectPane)
					((ProjectPane) tab.getContent()).onClose();
			}
		});

		Platform.runLater(() -> getChildren().remove(0));
		getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> onSelect.accept(old, val));

		workingPane = createProjectPane((tab, pt) -> new MIPSStructurePane(tab, pt, (MIPSProject) pt.getProject()), false);
	}

	public WorkingPane getWorkingPane() {
		return workingPane;
	}

	public <E extends ProjectPane> E createProjectPane(BiFunction<Tab, ProjectTab, E> creator, boolean closeable) {
		LanguageTab tab = new LanguageTab("");
		tab.setClosable(closeable);
		getTabs().add(tab);

		E pane = creator.apply(tab, projectTab);
		if (!(pane instanceof Pane)) throw new IllegalArgumentException("Pane must be a Pane!");

		tab.setNode(pane.getLanguageNode());
		tab.setContent((Pane) pane);

		tab.setOnClosed(event -> {
			pane.onClose();
			if(onClose != null) onClose.accept(tab);
		});
		return pane;
	}
}

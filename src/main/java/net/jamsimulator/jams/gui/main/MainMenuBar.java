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

import javafx.application.Platform;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.action.Action;
import net.jamsimulator.jams.gui.action.context.ActionMenuItem;
import net.jamsimulator.jams.gui.action.context.ContextAction;
import net.jamsimulator.jams.gui.action.context.ContextActionMainMenuBuilder;
import net.jamsimulator.jams.gui.action.context.MainMenuRegion;
import net.jamsimulator.jams.gui.action.event.ActionBindEvent;
import net.jamsimulator.jams.gui.action.event.ActionUnbindEvent;
import net.jamsimulator.jams.language.event.DefaultLanguageChangeEvent;
import net.jamsimulator.jams.language.event.SelectedLanguageChangeEvent;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

/**
 * The main {@link MenuBar}.
 */
public class MainMenuBar extends MenuBar {

	public MainMenuBar() {
		refresh();
		Jams.getLanguageManager().registerListeners(this, true);
		JamsApplication.getActionManager().registerListeners(this, true);
	}

	public void refresh() {
		getMenus().clear();
		Set<MainMenuRegion> set = new HashSet<>();

		for (Action action : JamsApplication.getActionManager().getAll()) {
			if (!(action instanceof ContextAction)) continue;
			if (!((ContextAction) action).getMainMenuRegion().isPresent()) continue;
			set.add(((ContextAction) action).getMainMenuRegion().get());
		}

		set.stream().sorted(Comparator.comparingInt(MainMenuRegion::getPriority)).forEach(this::createMenu);
	}

	private void createMenu(MainMenuRegion region) {
		Set<ContextAction> set = getSupportedContextActions(region);
		if (set.isEmpty()) return;
		Menu main = new ContextActionMainMenuBuilder(region.getLanguageNode()).addAll(set).build();

		main.setOnShowing(event -> {
			for (MenuItem item : main.getItems()) {
				if (item instanceof ActionMenuItem) {
					item.setDisable(!((ActionMenuItem) item).getAction().supportsMainMenuState(this));
				}
			}
		});

		getMenus().add(main);
	}

	private Set<ContextAction> getSupportedContextActions(MainMenuRegion region) {
		Set<Action> actions = JamsApplication.getActionManager().getAll();
		Set<ContextAction> set = new HashSet<>();
		for (Action action : actions) {
			if (action instanceof ContextAction && region.equals(((ContextAction) action).getMainMenuRegion().orElse(null))) {
				set.add((ContextAction) action);
			}
		}
		return set;
	}

	@Listener
	private void onLanguageChange(DefaultLanguageChangeEvent.After event) {
		Platform.runLater(this::refresh);
	}

	@Listener
	private void onLanguageChange(SelectedLanguageChangeEvent.After event) {
		Platform.runLater(this::refresh);
	}

	@Listener
	private void onActionBind(ActionBindEvent.After event) {
		Platform.runLater(this::refresh);
	}

	@Listener
	private void onActionUnbind(ActionUnbindEvent.After event) {
		Platform.runLater(this::refresh);
	}
}

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

package net.jamsimulator.jams.gui.action.context;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.gui.action.Action;
import net.jamsimulator.jams.utils.Validate;

import java.util.Collection;
import java.util.Optional;
import java.util.TreeSet;

public class ContextActionMenuBuilder {

	private final Object node;
	private final ActionMainMenu menu;

	public ContextActionMenuBuilder(Object node) {
		Validate.notNull(node, "Node cannot be null!");
		this.node = node;
		menu = new ActionMainMenu(node);
	}

	public ContextActionMenuBuilder addAll(Collection<? extends ContextAction> actions) {
		actions.forEach(this::add);
		return this;
	}

	public ContextActionMenuBuilder add(ContextAction action) {
		ActionMenu menu = check(action.getRegion());
		menu.add(action);
		return this;
	}

	public ContextMenu build() {
		menu.refresh();
		return menu;
	}

	private ActionMenu check(ContextRegion region) {
		if (region.getSubmenu().isPresent()) {
			return check(region.getSubmenu().get());
		} else {
			return menu;
		}
	}

	private ActionMenu check(ContextSubmenu submenu) {
		ActionMenu parent = check(submenu.getRegion());
		Optional<ActionSubmenu> optional = parent.get(submenu);
		if (optional.isPresent()) return optional.get();
		ActionSubmenu newSubmenu = new ActionSubmenu(submenu, node);
		parent.add(newSubmenu);
		return newSubmenu;
	}


	private interface ActionMenu {
		void add(ContextRegionable action);

		Optional<ActionSubmenu> get(ContextSubmenu submenu);

		void refresh();
	}


	private static class ActionMainMenu extends ContextMenu implements ActionMenu {

		private final Object node;
		private final TreeSet<ContextRegionable> elements;

		public ActionMainMenu(Object node) {
			this.node = node;
			this.elements = new TreeSet<>(((o1, o2) -> {
				int val = o1.compareTo(o2);
				if (val == 0) return -1;
				return val;
			}));
		}

		@Override
		public Optional<ActionSubmenu> get(ContextSubmenu submenu) {
			return elements.stream().filter(target -> target instanceof ActionSubmenu
					&& ((ActionSubmenu) target).getSubmenu().equals(submenu))
					.map(target -> (ActionSubmenu) target).findAny();
		}

		@Override
		public void add(ContextRegionable action) {
			elements.add(action);
		}

		@Override
		public void refresh() {
			ContextActionMenuBuilder.refresh(getItems(), elements, node);
			elements.stream().filter(target -> target instanceof ActionMenu)
					.forEach(target -> ((ActionMenu) target).refresh());
		}
	}

	private static class ActionSubmenu extends Menu implements ActionMenu, ContextRegionable {

		private final ContextSubmenu submenu;
		private final Object node;
		private final TreeSet<ContextRegionable> elements;

		public ActionSubmenu(ContextSubmenu submenu, Object node) {
			super(submenu.getLanguageNode().isPresent()
					? Jams.getLanguageManager().getSelected().getOrDefault(submenu.getLanguageNode().get())
					: submenu.getName());
			this.submenu = submenu;
			this.node = node;
			this.elements = new TreeSet<>(((o1, o2) -> {
				int val = o1.compareTo(o2);
				if (val == 0) return -1;
				return val;
			}));
		}

		public ContextSubmenu getSubmenu() {
			return submenu;
		}

		@Override
		public void add(ContextRegionable action) {
			elements.add(action);
		}

		@Override
		public Optional<ActionSubmenu> get(ContextSubmenu submenu) {
			if (this.submenu.equals(submenu)) return Optional.of(this);
			return elements.stream().filter(target -> target instanceof ActionSubmenu
					&& ((ActionSubmenu) target).getSubmenu().equals(submenu))
					.map(target -> (ActionSubmenu) target).findAny();
		}

		@Override
		public void refresh() {
			ContextActionMenuBuilder.refresh(getItems(), elements, node);
			elements.stream().filter(target -> target instanceof ActionMenu)
					.forEach(target -> ((ActionMenu) target).refresh());
		}

		@Override
		public int compareTo(ContextRegionable o) {
			return submenu.getRegion().compareTo(o.getRegion());
		}

		@Override
		public ContextRegion getRegion() {
			return submenu.getRegion();
		}

		@Override
		public String getName() {
			return submenu.getName();
		}
	}


	private static void refresh(ObservableList<MenuItem> items, TreeSet<ContextRegionable> elements, Object node) {
		items.clear();
		ContextRegion current = null;
		for (ContextRegionable element : elements) {
			if (current == null) {
				current = element.getRegion();
			} else {
				if (current != element.getRegion()) {
					current = element.getRegion();
					items.add(new SeparatorMenuItem());
				}
			}

			if (element instanceof ContextAction) {
				items.add(new ActionMenuItem((Action) element, node, ((ContextAction) element).getIcon().orElse(null)));
			} else if (element instanceof ActionSubmenu) {
				items.add((ActionSubmenu) element);
			}
		}
	}

}

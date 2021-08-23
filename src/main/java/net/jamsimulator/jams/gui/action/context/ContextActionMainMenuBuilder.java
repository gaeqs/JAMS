/*
 *  MIT License
 *
 *  Copyright (c) 2021 Gael Rial Costas
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.jamsimulator.jams.gui.action.context;

import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import net.jamsimulator.jams.language.Language;
import net.jamsimulator.jams.language.wrapper.LanguageMenu;
import net.jamsimulator.jams.manager.Manager;

import java.util.Collection;
import java.util.Optional;
import java.util.TreeSet;

public class ContextActionMainMenuBuilder {

    private final ActionMainMenu menu;

    public ContextActionMainMenuBuilder(String languageNode) {
        menu = new ActionMainMenu(languageNode);
    }

    private static void refresh(ObservableList<MenuItem> items, TreeSet<ContextRegionable> elements) {
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
                items.add(new ActionMenuItem((ContextAction) element, null, ((ContextAction) element).getIcon().orElse(null), true));
            } else if (element instanceof ActionSubmenu) {
                items.add((ActionSubmenu) element);
            }
        }
    }

    public ContextActionMainMenuBuilder addAll(Collection<? extends ContextAction> actions) {
        actions.forEach(this::add);
        return this;
    }

    public ContextActionMainMenuBuilder add(ContextAction action) {
        ActionMenu menu = check(action.getRegion());
        menu.add(action);
        return this;
    }

    public Menu build() {
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
        ActionSubmenu newSubmenu = new ActionSubmenu(submenu);
        parent.add(newSubmenu);
        return newSubmenu;
    }


    private interface ActionMenu {
        void add(ContextRegionable action);

        Optional<ActionSubmenu> get(ContextSubmenu submenu);

        void refresh();
    }

    private static class ActionMainMenu extends LanguageMenu implements ActionMenu {

        private final TreeSet<ContextRegionable> elements;

        @SuppressWarnings("ComparatorMethodParameterNotUsed")
        public ActionMainMenu(String languageNode) {
            super(languageNode);
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
            ContextActionMainMenuBuilder.refresh(getItems(), elements);
            elements.stream().filter(target -> target instanceof ActionMenu)
                    .forEach(target -> ((ActionMenu) target).refresh());
        }
    }

    private static class ActionSubmenu extends Menu implements ActionMenu, ContextRegionable {

        private final ContextSubmenu submenu;
        private final TreeSet<ContextRegionable> elements;

        @SuppressWarnings("ComparatorMethodParameterNotUsed")
        public ActionSubmenu(ContextSubmenu submenu) {
            super(submenu.getLanguageNode().isPresent()
                    ? Manager.ofS(Language.class).getSelected().getOrDefault(submenu.getLanguageNode().get())
                    : submenu.getName());
            this.submenu = submenu;
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
            ContextActionMainMenuBuilder.refresh(getItems(), elements);
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
        public Optional<MainMenuRegion> getMainMenuRegion() {
            return Optional.empty();
        }

        @Override
        public String getName() {
            return submenu.getName();
        }
    }

}

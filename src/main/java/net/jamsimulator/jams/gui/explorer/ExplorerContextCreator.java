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

package net.jamsimulator.jams.gui.explorer;

import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import net.jamsimulator.jams.gui.action.ActionMenuItem;
import net.jamsimulator.jams.gui.action.defaults.explorerelement.ExplorerElementContextAction;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ExplorerContextCreator {

	public static ContextMenu createContextMenu(Set<ExplorerElementContextAction> supportedActions, Node node) {
		ContextMenu main = new ContextMenu();
		Map<String, javafx.scene.control.Menu> menus = new HashMap<>();

		String menuName;
		javafx.scene.control.Menu parent;
		javafx.scene.control.Menu current;
		String name;
		String totalName;
		for (ExplorerElementContextAction action : supportedActions) {
			menuName = action.getMenu();
			if (menuName.indexOf('.') != -1) {
				totalName = name = menuName.substring(0, menuName.indexOf('.'));

				if (menus.containsKey(totalName)) {
					parent = current = menus.get(totalName);
				} else {
					parent = current = new javafx.scene.control.Menu(name);
					main.getItems().add(current);
					menus.put(totalName, current);
				}

				menuName = menuName.substring(name.length() + 1);

				while (menuName.indexOf('.') != -1) {
					name = menuName.substring(0, menuName.indexOf('.'));
					totalName += "." + name;

					if (menus.containsKey(totalName)) {
						current = menus.get(totalName);
					} else {
						current = new Menu(name);
						parent.getItems().add(current);
						menus.put(totalName, current);
					}

					parent = current;
					menuName = menuName.substring(name.length() + 1);
				}

				current.getItems().add(new ActionMenuItem(action, node));

			} else {
				main.getItems().add(new ActionMenuItem(action, node));
			}
		}
		return main;
	}

}

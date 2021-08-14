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

import net.jamsimulator.jams.utils.Validate;

import java.util.Objects;

public class MainMenuRegion {

    public static MainMenuRegion FILE = new MainMenuRegion("file", "MAIN_MENU_FILE", 0);
    public static MainMenuRegion EDIT = new MainMenuRegion("edit", "MAIN_MENU_EDIT", 1);
    public static MainMenuRegion SIMULATION = new MainMenuRegion("simulation", "MAIN_MENU_SIMULATION", 2);
    public static MainMenuRegion TOOLS = new MainMenuRegion("tools", "MAIN_MENU_TOOLS", 3);
    public static MainMenuRegion HELP = new MainMenuRegion("help", "MAIN_MENU_HELP", Integer.MAX_VALUE);

    private final String name;
    private final String languageNode;
    private final int priority;

    public MainMenuRegion(String name, String languageNode, int priority) {
        Validate.notNull(name, "Name cannot be null!");
        this.name = name;
        this.languageNode = languageNode;
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public String getLanguageNode() {
        return languageNode;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MainMenuRegion that = (MainMenuRegion) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}

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

package net.jamsimulator.jams.gui.project.bottombar;

import javafx.geometry.Pos;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.gui.project.ProjectTab;
import net.jamsimulator.jams.gui.util.AnchorUtils;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * This is the bar that appears at the bottom of the main scene.
 * <p>
 * You can add element to the right and to the left of this bar using {@link #addElement(ProjectBottomBarElement)}
 * or {@link #addAllElements(ProjectBottomBarElement...)}.
 * <p>
 * To remove an element, use {@link #removeElement(String)}.
 */
public class ProjectBottomBar extends AnchorPane {

    public static final String STYLE_CLASS = "project-bottom-bar";

    private final Map<String, ProjectBottomBarElement> elements;
    private final HBox left, right;

    /**
     * Creates the bottom bar.
     * <p>
     * This consturctor also creates the default elements.
     *
     * @param projectTab the project tab using
     */
    public ProjectBottomBar(ProjectTab projectTab) {

        elements = new HashMap<>();

        getStyleClass().add(STYLE_CLASS);

        left = new HBox();
        right = new HBox();
        AnchorUtils.setAnchor(left, 0, 0, 0, -1);
        AnchorUtils.setAnchor(right, 0, 0, -1, 0);
        left.setAlignment(Pos.CENTER_LEFT);
        right.setAlignment(Pos.CENTER_RIGHT);
        getChildren().addAll(left, right);
        loadDefaults(projectTab);
    }

    /**
     * Returns the element inside this bar that matches the given name.
     * <p>
     * This method returns {@link Optional#empty()} when the element is not found.
     *
     * @param name the name.
     * @return the element if present.
     */
    public Optional<ProjectBottomBarElement> getElement(String name) {
        return Optional.ofNullable(elements.get(name));
    }

    /**
     * Adds the given elements to the bar.
     * <p>
     * If any of the elements has the same name of one of the elements already
     * present in the bar, the element is not added.
     *
     * @param elements the elements.
     * @return the amount of elements sucessfully added to the bar.
     */
    public int addAllElements(ProjectBottomBarElement... elements) {
        int amount = 0;
        boolean refreshLeft = false, refreshRight = false;
        for (var element : elements) {
            if (this.elements.containsKey(element.getName())) continue;
            this.elements.put(element.getName(), element);
            amount++;
            switch (element.getPosition()) {
                case LEFT -> refreshLeft = true;
                case RIGHT -> refreshRight = true;
            }
        }

        if (refreshLeft) refresh(ProjectBottomBarPosition.LEFT);
        if (refreshRight) refresh(ProjectBottomBarPosition.RIGHT);
        return amount;
    }

    /**
     * Adds the given element to the bar.
     * <p>
     * This method returns false and does nothing if an element
     * with the same name is already added.
     *
     * @param element the element to add.
     * @return whether the operation was sucessful.
     */
    public boolean addElement(ProjectBottomBarElement element) {
        if (elements.containsKey(element.getName())) return false;
        elements.put(element.getName(), element);
        refresh(element.getPosition());
        return true;
    }

    /**
     * Removes the element inside this bar that matches the given name.
     *
     * @param name the name.
     * @return whether the operation was sucessful.
     */
    public boolean removeElement(String name) {
        var element = elements.remove(name);
        if (element == null) return false;
        refresh(element.getPosition());
        return true;
    }

    private void refresh(ProjectBottomBarPosition position) {
        var hBox = switch (position) {
            case LEFT -> left;
            case RIGHT -> right;
        };

        hBox.getChildren().clear();
        elements.values().stream()
                .filter(it -> it.getPosition() == position)
                .sorted(Comparator.comparing(it -> -it.getPriority()))
                .map(ProjectBottomBarElement::asNode).forEach(hBox.getChildren()::add);
    }

    private void loadDefaults(ProjectTab tab) {
        var task = new ProjectTaskBarElement(tab.getProject());
        var memory = new JamsMemoryBarElement(tab.getProject());
        var lastLine = new ProjectLastLogBarElement(tab);
        addAllElements(task, memory, lastLine);
    }

}

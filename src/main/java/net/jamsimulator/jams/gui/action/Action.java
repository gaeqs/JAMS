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

package net.jamsimulator.jams.gui.action;

import javafx.scene.input.KeyCombination;
import net.jamsimulator.jams.manager.Labeled;
import net.jamsimulator.jams.utils.Validate;

import java.util.Objects;
import java.util.Optional;

/**
 * Represents an action that can be bind to a {@link javafx.scene.input.KeyCombination}.
 */
public abstract class Action implements Labeled {

    private final String name;
    private final String regionTag;
    private final String languageNode;

    private final KeyCombination defaultCombination;

    /**
     * Creates the action.
     *
     * @param name               the name of the action. This name must be unique.
     * @param regionTag          the region tag of this action. This action will only interact on regions that support this tag.
     * @param languageNode       the language node of this action.
     * @param defaultCombination the default combination of keys that a user needs to press to execute this action.
     */
    public Action(String name, String regionTag, String languageNode, KeyCombination defaultCombination) {
        Validate.notNull(name, "Name cannot be null!");
        Validate.notNull(regionTag, "Region tag cannot be null!");
        this.name = name;
        this.regionTag = regionTag;
        this.languageNode = languageNode;
        this.defaultCombination = defaultCombination;
    }

    /**
     * Returns the name of the action. This name must be unique.
     *
     * @return the name.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Returns the region tag of the action. Regions must have this tag to execute the action.
     *
     * @return the region tag.
     */
    public String getRegionTag() {
        return regionTag;
    }

    /**
     * Returns the language node of the action, if present.
     * <p>
     * This node is used when the action must be displayed on config or on context menus.
     *
     * @return the language node of the action, if present.
     */
    public Optional<String> getLanguageNode() {
        return Optional.ofNullable(languageNode);
    }

    /**
     * Returns the default combination of the action, if present.
     * <p>
     * If the combination is present, this action is not present in the actions file and
     * no actions are bind to this combination will be bind to this action.
     * <p>
     * The combination will not be bind if the action is present in the actions file, but it has no combinations.
     *
     * @return the default code combination, if present.
     */
    public Optional<KeyCombination> getDefaultCodeCombination() {
        return Optional.ofNullable(defaultCombination);
    }

    /**
     * Executes this action.
     *
     * @param node the current focused node.
     */
    public abstract void run(Object node);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Action action = (Action) o;
        return name.equals(action.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Action{" +
                "name='" + name + '\'' +
                ", regionTag='" + regionTag + '\'' +
                ", languageNode='" + languageNode + '\'' +
                ", defaultCombination=" + defaultCombination +
                '}';
    }
}

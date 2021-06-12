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

package net.jamsimulator.jams.gui.configuration.explorer.section.action;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.action.Action;
import net.jamsimulator.jams.gui.action.event.ActionBindEvent;
import net.jamsimulator.jams.gui.action.event.ActionUnbindEvent;
import net.jamsimulator.jams.gui.explorer.ExplorerBasicElement;
import net.jamsimulator.jams.gui.explorer.ExplorerSection;
import net.jamsimulator.jams.gui.explorer.ExplorerSeparatorRegion;
import net.jamsimulator.jams.gui.image.NearestImageView;
import net.jamsimulator.jams.language.wrapper.LanguageLabel;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a {@link net.jamsimulator.jams.gui.explorer.ExplorerElement} representing an {@link Action}.
 */
public class ActionsExplorerAction extends ExplorerBasicElement {

    /**
     * The amount of shortcuts an user can add.
     * This limit avoids a bad structure in the explorer.
     */
    public static final int MAX_SHORTCUTS = 3;

    protected final Action action;
    protected final Region bigSeparator;

    protected final List<ActionExplorerActionCombination> combinations;
    protected ActionExplorerActionCombinationAdd addButton;

    protected boolean smallRepresentation;

    /**
     * Creates an action explorer element.
     *
     * @param parent the {@link ExplorerSection} containing this element.
     * @param action the represented {@link Action}.
     */
    public ActionsExplorerAction(ActionExplorerRegion parent, Action action, boolean smallRepresentation) {
        super(parent, action.getName(), 2);
        this.action = action;
        this.smallRepresentation = smallRepresentation;
        this.combinations = new ArrayList<>();

        ((LanguageLabel) label).setNode(action.getLanguageNode().orElse(null));

        bigSeparator = new Region();
        HBox.setHgrow(bigSeparator, Priority.ALWAYS);
        getChildren().add(bigSeparator);

        JamsApplication.getActionManager().registerListeners(this, true);

        refresh();
    }

    public boolean isSmallRepresentation() {
        return smallRepresentation;
    }

    public void setSmallRepresentation(boolean smallRepresentation) {
        this.smallRepresentation = smallRepresentation;
        refresh();
    }

    /**
     * Returns the represented {@link Action}.
     *
     * @return the {@link Action}.
     */
    public Action getAction() {
        return action;
    }

    /**
     * Refresh all elements inside this representation.
     */
    public void refresh() {
        combinations.clear();
        getChildren().clear();
        getChildren().addAll(separator, icon);

        if (smallRepresentation) {
            refreshSmallRepresentation();
        } else {
            refreshBigRepresentation();
        }
    }

    private void refreshBigRepresentation() {
        getChildren().addAll(new Group(label), bigSeparator);
        List<KeyCombination> combinations = JamsApplication.getActionManager().getBindCombinations(action.getName());
        ActionExplorerActionCombination node;
        for (KeyCombination combination : combinations) {
            node = new ActionExplorerActionCombination(this, combination);
            this.combinations.add(node);
            getChildren().add(node);
        }
        if (combinations.size() < MAX_SHORTCUTS) {
            getChildren().add(addButton = new ActionExplorerActionCombinationAdd(this));
        }
    }

    private void refreshSmallRepresentation() {
        VBox split = new VBox();
        getChildren().add(split);

        split.getChildren().add(new Group(label));

        HBox bottom = new HBox();
        bottom.setSpacing(SPACING);
        split.getChildren().add(bottom);

        List<KeyCombination> combinations = JamsApplication.getActionManager().getBindCombinations(action.getName());
        ActionExplorerActionCombination node;
        for (KeyCombination combination : combinations) {
            node = new ActionExplorerActionCombination(this, combination);
            this.combinations.add(node);
            bottom.getChildren().add(node);
        }
        if (combinations.size() < MAX_SHORTCUTS) {
            bottom.getChildren().add(addButton = new ActionExplorerActionCombinationAdd(this));
        }
    }

    @Override
    public String getVisibleName() {
        return label.getText();
    }

    @Override
    protected void loadElements() {
        icon = new NearestImageView();
        label = new LanguageLabel(null);

        separator = new ExplorerSeparatorRegion(false, hierarchyLevel);

        setSpacing(SPACING);
        setPadding(new Insets(0, 5, 0, 0));
        setAlignment(Pos.CENTER_LEFT);
    }

    @Listener
    private void onCombinationBind(ActionBindEvent.After event) {
        if (action.equals(event.getAction()) || event.getReplacedActions().containsValue(action)) {
            refresh();
        }
    }

    @Listener
    private void onCombinationUnbind(ActionUnbindEvent.After event) {
        if (action.equals(event.getAction())) {
            refresh();
        }
    }

    @Override
    public double getRepresentationWidth() {
        return smallRepresentation ? getSmallRepresentationWidth() : getBigRepresentationWidth();
    }

    public double getSmallRepresentationWidth() {
        double top = label.getWidth();
        double bottom = 0;
        for (ActionExplorerActionCombination combination : combinations) {
            bottom += combination.getWidth() + ExplorerBasicElement.SPACING;
        }
        bottom += addButton.getWidth();

        return separator.getWidth() + icon.getFitWidth() + ExplorerBasicElement.SPACING * 2 + Math.max(top, bottom);
    }

    public double getBigRepresentationWidth() {
        double width = separator.getWidth() + icon.getFitWidth() + label.getWidth() + ExplorerBasicElement.SPACING * 3;
        for (ActionExplorerActionCombination combination : combinations) {
            width += combination.getWidth() + ExplorerBasicElement.SPACING;
        }
        return width + addButton.getWidth();
    }
}

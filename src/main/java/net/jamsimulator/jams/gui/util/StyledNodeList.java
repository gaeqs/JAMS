package net.jamsimulator.jams.gui.util;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import net.jamsimulator.jams.language.wrapper.LanguageLabel;
import net.jamsimulator.jams.utils.Validate;

import java.util.LinkedList;
import java.util.List;

public class StyledNodeList extends VBox {

    public static final String STYLE_CLASS = "styled-node-list";
    public static final String STYLE_CLASS_ENTRY = "styled-node-list-entry";

    private final List<Entry> entries;

    public StyledNodeList() {
        getStyleClass().add(STYLE_CLASS);
        entries = new LinkedList<>();
    }

    public void addEntry(String languageNode, Node... nodes) {
        Validate.notNull(languageNode, "Language node cannot be null!");
        Validate.hasNoNulls(nodes, "Nodes cannot have nulls!");
        var entry = new Entry(languageNode, nodes);
        entries.add(entry);
        getChildren().add(entry);
        refresh();

        entry.label.widthProperty().addListener((obs, old, val) -> refresh());
    }

    private void refresh() {
        double maxWidth = 0.0;

        for (Entry entry : entries) {
            maxWidth = Math.max(maxWidth, entry.label.getWidth());
        }

        for (Entry entry : entries) {
            entry.expansion.setPrefWidth(maxWidth - entry.label.getWidth());
        }
    }

    private static class Entry extends HBox {

        Label label;
        Region expansion;

        Entry(String languageNode, Node... nodes) {
            getStyleClass().add(STYLE_CLASS_ENTRY);

            label = new LanguageLabel(languageNode);
            expansion = new Region();

            getChildren().addAll(new Group(label), expansion);
            getChildren().addAll(nodes);

            if (nodes.length > 0 && nodes[0] instanceof ComboBox<?>) {
                ((ComboBox<?>) nodes[0]).setMinWidth(200);
            } else if (nodes.length > 0 && nodes[0] instanceof TextField) {
                var property = widthProperty()
                        .subtract(label.widthProperty())
                        .subtract(expansion.widthProperty())
                        // If we have N elements in nodes, we have N + 2 elements in this HBox.
                        // This means we have N + 2 - 1 spaces.
                        .subtract(spacingProperty().multiply(nodes.length + 1));

                for (int i = 1; i < nodes.length; i++) {
                    if (nodes[i] instanceof Region) {
                        property = property.subtract(((Region) nodes[i]).widthProperty());
                    }
                }

                ((TextField) nodes[0]).prefWidthProperty().bind(property);
            }
        }

    }

}

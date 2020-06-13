package net.jamsimulator.jams.utils;

import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

import java.util.UUID;

/**
 * This class help applications to add drag and drop support for Tabs.
 * Create an instance and call {@link #addSupport(TabPane)} to add drag and drop support
 * to a {@link TabPane}.
 * <p>
 * {@link TabPane}s that uses the same dragging support can share {@link Tab}s.
 */
public class DraggingSupport {

	private Tab current;
	private final String draggingID = "JDS-" + UUID.randomUUID();

	/**
	 * Adds drag and drop support to the given {@link TabPane}.
	 * <p>
	 * {@link TabPane}s that uses the same dragging support can share {@link Tab}s.
	 *
	 * @param tabPane the {@link TabPane}.
	 */
	public void addSupport(TabPane tabPane) {
		tabPane.getTabs().forEach(this::addDragHandlers);
		tabPane.getTabs().addListener((ListChangeListener.Change<? extends Tab> change) -> {
			while (change.next()) {
				if (change.wasAdded())
					change.getAddedSubList().forEach(this::addDragHandlers);
				if (change.wasRemoved())
					change.getRemoved().forEach(this::removeDragHandlers);
			}
		});
		addDragHandlersToTabPane(tabPane);
	}

	private void addDragHandlersToTabPane(TabPane tabPane) {
		tabPane.setOnDragOver(e -> {
			if (draggingID.equals(e.getDragboard().getString()) &&
					current != null &&
					current.getTabPane() != tabPane) {
				e.acceptTransferModes(TransferMode.MOVE);
			}
		});
		tabPane.setOnDragDropped(e -> {
			if (draggingID.equals(e.getDragboard().getString())
					&& current != null
					&& current.getTabPane() != tabPane) {
				current.getTabPane().getTabs().remove(current);
				tabPane.getTabs().add(current);
				current.getTabPane().getSelectionModel().select(current);
			}
		});
	}

	private void addDragHandlers(Tab tab) {
		//Moves the text to the graphic node, allowing drags.
		if (tab.getText() != null && !tab.getText().isEmpty()) {
			Label label = new Label(tab.getText(), tab.getGraphic());
			tab.setText(null);
			tab.setGraphic(label);
		}

		Node graphic = tab.getGraphic();
		graphic.setOnDragDetected(e -> {
			Dragboard dragboard = graphic.startDragAndDrop(TransferMode.MOVE);
			ClipboardContent content = new ClipboardContent();

			content.putString(draggingID);
			dragboard.setContent(content);
			dragboard.setDragView(graphic.snapshot(null, null));
			current = tab;
		});
		graphic.setOnDragOver(e -> {
			if (draggingID.equals(e.getDragboard().getString()) &&
					current != null &&
					current.getGraphic() != graphic) {
				e.acceptTransferModes(TransferMode.MOVE);
			}
		});
		graphic.setOnDragDropped(e -> {
			if (draggingID.equals(e.getDragboard().getString()) &&
					current != null &&
					current.getGraphic() != graphic) {

				int index = tab.getTabPane().getTabs().indexOf(tab);
				current.getTabPane().getTabs().remove(current);
				tab.getTabPane().getTabs().add(index, current);
				current.getTabPane().getSelectionModel().select(current);
			}
		});
		graphic.setOnDragDone(e -> current = null);
	}

	private void removeDragHandlers(Tab tab) {
		tab.getGraphic().setOnDragDetected(null);
		tab.getGraphic().setOnDragOver(null);
		tab.getGraphic().setOnDragDropped(null);
		tab.getGraphic().setOnDragDone(null);
	}

}

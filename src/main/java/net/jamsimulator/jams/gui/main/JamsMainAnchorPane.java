package net.jamsimulator.jams.gui.main;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;


public class JamsMainAnchorPane extends AnchorPane {

	public JamsMainAnchorPane() {
		MenuBar menu = new MenuBar();
		getChildren().add(menu);
		AnchorPane.setLeftAnchor(menu, 0.0);
		AnchorPane.setRightAnchor(menu, 0.0);

		menu.getMenus().add(new Menu("TEST"));


		Rectangle rectangle = new Rectangle(200, 200, 200, 200);
		rectangle.setFill(Paint.valueOf("0xFF0000"));
		getChildren().add(rectangle);


		getStyleClass().add("anchor-pane");

	}

}
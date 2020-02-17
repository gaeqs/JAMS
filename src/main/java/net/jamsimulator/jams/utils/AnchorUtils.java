package net.jamsimulator.jams.utils;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

public class AnchorUtils {

	public static void setAnchor(Node node, double top, double bottom, double left, double right) {
		if (top != -1) AnchorPane.setTopAnchor(node, top);
		if (bottom != -1) AnchorPane.setBottomAnchor(node, bottom);
		if (left != -1) AnchorPane.setLeftAnchor(node, left);
		if (right != -1) AnchorPane.setRightAnchor(node, right);
	}

}

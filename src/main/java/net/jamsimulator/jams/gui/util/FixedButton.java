package net.jamsimulator.jams.gui.util;

import javafx.scene.Node;
import javafx.scene.control.Button;

public class FixedButton extends Button {

	public FixedButton(double width, double height) {
		setPrefWidth(width);
		setPrefHeight(height);
	}

	public FixedButton(String s, double width, double height) {
		super(s);
		setPrefWidth(width);
		setPrefHeight(height);
	}

	public FixedButton(String s, Node node, double width, double height) {
		super(s, node);
		setPrefWidth(width);
		setPrefHeight(height);
	}
}

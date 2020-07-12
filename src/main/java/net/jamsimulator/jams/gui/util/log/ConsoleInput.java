package net.jamsimulator.jams.gui.util.log;

import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import net.jamsimulator.jams.utils.NumericUtils;

public class ConsoleInput extends Region {

	private final Label label;

	/**
	 * Creates the console input.
	 *
	 * @param text the handled input.
	 */
	public ConsoleInput(String text, Console console) {
		getStyleClass().add("input");
		label = new Label(text);
		getChildren().add(label);

		setOnMouseClicked(event -> {
			console.inputs.remove(text);
		});

		if (NumericUtils.isInteger(text) || NumericUtils.isLong(text)) {
			getStyleClass().add("input-integer");
		} else if (NumericUtils.isFloat(text) || NumericUtils.isDouble(text)) {
			getStyleClass().add("input-float");
		} else {
			getStyleClass().add("input-string");
		}
	}

	public String getText() {
		return label.getText();
	}


}

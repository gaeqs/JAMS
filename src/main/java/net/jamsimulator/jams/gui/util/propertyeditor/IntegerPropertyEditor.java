package net.jamsimulator.jams.gui.util.propertyeditor;

import javafx.beans.property.Property;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import net.jamsimulator.jams.utils.NumericUtils;

public class IntegerPropertyEditor extends TextField implements PropertyEditor<Integer> {

	private final Property<Integer> property;
	private String oldText;

	public IntegerPropertyEditor(Property<Integer> property) {
		this.property = property;

		setText(property.getValue().toString());

		oldText = getText();
		Runnable run = () -> {
			if (oldText.equals(getText())) return;
			try {
				int number = NumericUtils.decodeInteger(getText());
				property.setValue(number);
				oldText = getText();
			} catch (NumberFormatException ex) {
				setText(oldText);
			}
		};

		setOnAction(event -> run.run());
		focusedProperty().addListener((obs, old, val) -> {
			if (val) return;
			run.run();
		});
		setPrefWidth(40);
	}

	@Override
	public Property<Integer> getProperty() {
		return property;
	}

	@Override
	public Node thisInstanceAsNode() {
		return this;
	}
}

package net.jamsimulator.jams.gui.mips.simulator.register;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.register.event.RegisterChangeValueEvent;
import net.jamsimulator.jams.utils.NumericUtils;
import net.jamsimulator.jams.utils.StringUtils;

import java.util.Set;

/**
 * This class wraps a {@link Register}, making it valid to be used in a {@link javafx.scene.control.TableView}.
 */
public class RegisterPropertyWrapper {

	private final Register register;
	private final boolean useDecimals;

	private SimpleIntegerProperty identifierProperty;
	private SimpleStringProperty nameProperty;
	private SimpleStringProperty valueProperty;
	private SimpleStringProperty hexProperty;

	public RegisterPropertyWrapper(Register register, boolean useDecimals) {
		this.register = register;
		this.useDecimals = useDecimals;
		register.getRegisters().registerListeners(this, true);
	}

	public Register getRegister() {
		return register;
	}

	public ReadOnlyIntegerProperty identifierProperty() {
		if (identifierProperty == null) {
			identifierProperty = new SimpleIntegerProperty(this, "identifier");
			identifierProperty.setValue(register.getIdentifier());
		}
		return identifierProperty;
	}

	public SimpleStringProperty nameProperty() {
		if (nameProperty == null) {
			nameProperty = new SimpleStringProperty(this, "name");

			Set<String> names = register.getNames();
			StringBuilder builder = new StringBuilder();
			boolean first = true;

			String start = register.getRegisters().getValidRegistersStarts().stream()
					.findAny().map(String::valueOf).orElse("");
			String idToString = String.valueOf(register.getIdentifier());

			for (String name : names) {
				if (name.equals(idToString)) continue;
				if (first) first = false;
				else builder.append(" / ");
				builder.append(start).append(name);
			}

			nameProperty.setValue(builder.toString());
		}
		return nameProperty;
	}

	public synchronized SimpleStringProperty valueProperty() {
		if (valueProperty == null) {
			valueProperty = new SimpleStringProperty(this, "value");

			if (useDecimals) {
				valueProperty.setValue(String.valueOf(Float.intBitsToFloat(register.getValue())));
			} else {
				valueProperty.setValue(String.valueOf(register.getValue()));
			}

			valueProperty.addListener((obs, old, val) -> {

				if (val.equals(old)) return;
				try {
					int to = useDecimals ? Float.floatToIntBits(Float.parseFloat(val)) : NumericUtils.decodeInteger(val);
					if (register.getValue() != to) {
						register.setValue(to);
					}
				} catch (NumberFormatException ex) {
					valueProperty.setValue(old);
				}

			});
		}
		return valueProperty;
	}

	public StringProperty hexProperty() {
		if (hexProperty == null) {
			hexProperty = new SimpleStringProperty(this, "hex");
			hexProperty.setValue("0x" + StringUtils.addZeros(Integer.toHexString(register.getValue()), 8));
			hexProperty.addListener((obs, old, val) -> {
				if (!register.isModifiable()) return;
				if (old.equals(val)) return;
				int to = NumericUtils.decodeInteger(val);
				if (register.getValue() == to) return;
				try {
					register.setValue(to);
				} catch (NumberFormatException ex) {
					ex.printStackTrace();
					hexProperty.setValue(old);
				}
			});
		}
		return hexProperty;
	}

	@Listener
	private synchronized void onRegisterValueChange(RegisterChangeValueEvent.After event) {
		if (!event.getRegister().equals(register)) return;
		updateRegister(event.getNewValue());
	}

	private void updateRegister (int newValue) {
		if (valueProperty == null) valueProperty();
		if (hexProperty == null) hexProperty();

		if (useDecimals) {
			valueProperty.setValue(String.valueOf(Float.intBitsToFloat(newValue)));
		} else {
			valueProperty.setValue(String.valueOf(newValue));
		}

		hexProperty.set("0x" + StringUtils.addZeros(Integer.toHexString(newValue), 8));
	}
}

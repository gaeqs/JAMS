package net.jamsimulator.jams.gui.mips.simulator.register;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import net.jamsimulator.jams.mips.register.COP0Register;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.utils.NumericUtils;
import net.jamsimulator.jams.utils.StringUtils;

/**
 * This class wraps a {@link Register}, making it valid to be used in a {@link javafx.scene.control.TableView}.
 */
public class COP0RegisterPropertyWrapper {

	private final Object lock = new Object();
	private boolean updating;

	private final COP0Register register;
	private final boolean useDecimals;

	private SimpleIntegerProperty identifierProperty;
	private SimpleIntegerProperty selectionProperty;
	private SimpleStringProperty nameProperty;
	private SimpleStringProperty valueProperty;
	private SimpleStringProperty hexProperty;

	public COP0RegisterPropertyWrapper(COP0Register register, boolean useDecimals) {
		this.register = register;
		this.useDecimals = useDecimals;
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

	public ReadOnlyIntegerProperty selectionProperty() {
		if (selectionProperty == null) {
			selectionProperty = new SimpleIntegerProperty(this, "selection");
			selectionProperty.setValue(register.getSelection());
		}
		return selectionProperty;
	}

	public SimpleStringProperty nameProperty() {
		if (nameProperty == null) {
			nameProperty = new SimpleStringProperty(this, "name");
			nameProperty.setValue(register.getCop0Name());
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
				synchronized (lock) {
					if (updating || val.equals(old) || !register.isModifiable()) return;
					try {
						int to = useDecimals ? Float.floatToIntBits(Float.parseFloat(val)) : NumericUtils.decodeInteger(val);
						if (register.getValue() != to) {
							register.setValue(to);
						}
					} catch (NumberFormatException ex) {
						valueProperty.setValue(old);
					}
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
				synchronized (lock) {
					if (updating || !register.isModifiable()) return;
					if (old.equals(val)) return;
					int to = NumericUtils.decodeInteger(val);
					if (register.getValue() == to) return;
					try {
						register.setValue(to);
					} catch (NumberFormatException ex) {
						ex.printStackTrace();
						hexProperty.setValue(old);
					}
				}
			});
		}
		return hexProperty;
	}

	public void updateRegister() {
		updateRegister(register.getValue());
	}

	public void updateRegister(int newValue) {
		synchronized (lock) {
			updating = true;
			if (valueProperty == null) valueProperty();
			if (hexProperty == null) hexProperty();

			if (useDecimals) {
				valueProperty.setValue(String.valueOf(Float.intBitsToFloat(newValue)));
			} else {
				valueProperty.setValue(String.valueOf(newValue));
			}

			hexProperty.set("0x" + StringUtils.addZeros(Integer.toHexString(newValue), 8));
			updating = false;
		}
	}
}

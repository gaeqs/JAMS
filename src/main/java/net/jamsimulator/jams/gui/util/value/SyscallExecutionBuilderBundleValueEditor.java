package net.jamsimulator.jams.gui.util.value;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.util.converter.ActionValueConverter;
import net.jamsimulator.jams.gui.util.converter.ValueConverters;
import net.jamsimulator.jams.mips.syscall.bundle.SyscallExecutionBuilderBundle;
import net.jamsimulator.jams.mips.syscall.event.SyscallExecutionBuilderBundleRegisterEvent;
import net.jamsimulator.jams.mips.syscall.event.SyscallExecutionBuilderBundleUnregisterEvent;

import java.util.function.Consumer;

public class SyscallExecutionBuilderBundleValueEditor extends ComboBox<SyscallExecutionBuilderBundle> implements ValueEditor<SyscallExecutionBuilderBundle> {

	public static final String NAME = ActionValueConverter.NAME;

	private Consumer<SyscallExecutionBuilderBundle> listener = syscallExecutionBuilderBundle -> {
	};

	public SyscallExecutionBuilderBundleValueEditor() {
		setConverter(ValueConverters.getByTypeUnsafe(SyscallExecutionBuilderBundle.class));
		getItems().addAll(Jams.getSyscallExecutionBuilderManager().getAllBundles());
		getSelectionModel().select(0);
		getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> listener.accept(val));
		Jams.getSyscallExecutionBuilderManager().registerListeners(this, true);
	}

	@Override
	public void setCurrentValue(SyscallExecutionBuilderBundle value) {
		getSelectionModel().select(value);
	}

	@Override
	public SyscallExecutionBuilderBundle getCurrentValue() {
		return getValue();
	}

	@Override
	public Node getAsNode() {
		return this;
	}

	@Override
	public Node buildConfigNode(Label label) {
		var box =  new HBox(label, this);
		box.setSpacing(5);
		box.setAlignment(Pos.CENTER_LEFT);
		return box;
	}

	@Override
	public void addListener(Consumer<SyscallExecutionBuilderBundle> consumer) {
		listener = listener.andThen(consumer);
	}

	@Listener
	private void onSyscallExecutionBuilderBundleRegister(SyscallExecutionBuilderBundleRegisterEvent.After event) {
		getItems().add(event.getBundle());
	}

	@Listener
	private void onSyscallExecutionBuilderBundleUnregister(SyscallExecutionBuilderBundleUnregisterEvent.After event) {
		boolean remove = getSelectionModel().getSelectedItem().equals(event.getBundle());
		getItems().remove(event.getBundle());
		if (remove) {
			getSelectionModel().select(0);
		}
	}

	public static class Builder implements ValueEditor.Builder<SyscallExecutionBuilderBundle> {

		@Override
		public ValueEditor<SyscallExecutionBuilderBundle> build() {
			return new SyscallExecutionBuilderBundleValueEditor();
		}

	}
}

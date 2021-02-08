package net.jamsimulator.jams.gui.util.value;

import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.util.converter.SyscallExecutionBuilderValueConverter;
import net.jamsimulator.jams.language.Language;
import net.jamsimulator.jams.language.event.DefaultLanguageChangeEvent;
import net.jamsimulator.jams.language.event.SelectedLanguageChangeEvent;
import net.jamsimulator.jams.language.wrapper.SyscallLanguageListCell;
import net.jamsimulator.jams.mips.syscall.SyscallExecutionBuilder;
import net.jamsimulator.jams.mips.syscall.defaults.SyscallExecutionRunExceptionHandler;
import net.jamsimulator.jams.mips.syscall.event.SyscallExecutionBuilderRegisterEvent;
import net.jamsimulator.jams.mips.syscall.event.SyscallExecutionBuilderUnregisterEvent;
import net.jamsimulator.jams.utils.NumericStringComparator;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class SyscallExecutionBuilderValueEditor extends ComboBox<SyscallExecutionBuilder> implements ValueEditor<SyscallExecutionBuilder> {

	public static final String NAME = SyscallExecutionBuilderValueConverter.NAME;

	private static final List<SyscallExecutionBuilder<?>> SORTED_BUILDERS = new LinkedList<>();

	static {
		SORTED_BUILDERS.addAll(Jams.getSyscallExecutionBuilderManager());
		sort();
		var listeners = new StaticListeners();
		Jams.getLanguageManager().registerListeners(listeners, false);
		Jams.getSyscallExecutionBuilderManager().registerListeners(listeners, false);
	}

	private static void sort() {
		Language language = Jams.getLanguageManager().getSelected();
		SORTED_BUILDERS.sort(Comparator.comparing(target -> language.getOrDefault(target.getLanguageNode()), new NumericStringComparator()));
	}

	private Consumer<SyscallExecutionBuilder<?>> listener = syscallExecutionBuilder -> {
	};

	public SyscallExecutionBuilderValueEditor() {

		setCellFactory(list -> new SyscallLanguageListCell());
		setButtonCell(new SyscallLanguageListCell());

		getItems().setAll(SORTED_BUILDERS);
		getSelectionModel().select(Jams.getSyscallExecutionBuilderManager().get(SyscallExecutionRunExceptionHandler.NAME).orElse(null));
		getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> listener.accept(val));
		Jams.getSyscallExecutionBuilderManager().registerListeners(this, true);
	}

	private void refresh() {
		var selected = getSelectionModel().getSelectedItem();
		getItems().setAll(SORTED_BUILDERS);
		getSelectionModel().select(selected);
	}

	@Override
	public void setCurrentValue(SyscallExecutionBuilder value) {
		getSelectionModel().select(value);
	}

	@Override
	public SyscallExecutionBuilder getCurrentValue() {
		return getValue();
	}

	@Override
	public Node getAsNode() {
		return this;
	}

	@Override
	public void addListener(Consumer<SyscallExecutionBuilder> consumer) {
		listener = listener.andThen(consumer);
	}

	@Listener
	private void onSyscallExecutionBuilderRegister(SyscallExecutionBuilderRegisterEvent.After event) {
		refresh();
	}

	@Listener
	private void onSyscallExecutionBuilderUnregister(SyscallExecutionBuilderUnregisterEvent.After event) {
		if (getSelectionModel().getSelectedItem().equals(event.getSyscallExecutionBuilder()))
			getSelectionModel().select(Jams.getSyscallExecutionBuilderManager()
					.get(SyscallExecutionRunExceptionHandler.NAME).orElse(null));
		refresh();
	}

	public static class Builder implements ValueEditor.Builder<SyscallExecutionBuilder> {

		@Override
		public ValueEditor<SyscallExecutionBuilder> build() {
			return new SyscallExecutionBuilderValueEditor();
		}

	}


	private static class StaticListeners {

		@Listener(priority = Integer.MAX_VALUE)
		private void onLanguageChange(SelectedLanguageChangeEvent.After event) {
			sort();
		}

		@Listener(priority = Integer.MAX_VALUE)
		private void onLanguageChange(DefaultLanguageChangeEvent.After event) {
			sort();
		}

		@Listener(priority = Integer.MAX_VALUE)
		private void onSyscallRegister(SyscallExecutionBuilderRegisterEvent.After event) {
			SORTED_BUILDERS.add(event.getSyscallExecutionBuilder());
			sort();
		}

		@Listener(priority = Integer.MAX_VALUE)
		private void onSyscallUnregister(SyscallExecutionBuilderUnregisterEvent.After event) {
			SORTED_BUILDERS.add(event.getSyscallExecutionBuilder());
			sort();
		}

	}
}

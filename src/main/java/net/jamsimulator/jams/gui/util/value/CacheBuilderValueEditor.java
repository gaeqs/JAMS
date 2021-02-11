package net.jamsimulator.jams.gui.util.value;

import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.util.converter.CacheBuilderValueConverter;
import net.jamsimulator.jams.language.Language;
import net.jamsimulator.jams.language.event.DefaultLanguageChangeEvent;
import net.jamsimulator.jams.language.event.SelectedLanguageChangeEvent;
import net.jamsimulator.jams.language.wrapper.CacheBuilderLanguageListCell;
import net.jamsimulator.jams.mips.memory.cache.CacheBuilder;
import net.jamsimulator.jams.mips.memory.cache.event.CacheBuilderRegisterEvent;
import net.jamsimulator.jams.mips.memory.cache.event.CacheBuilderUnregisterEvent;
import net.jamsimulator.jams.mips.syscall.defaults.SyscallExecutionRunExceptionHandler;
import net.jamsimulator.jams.utils.NumericStringComparator;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class CacheBuilderValueEditor extends ComboBox<CacheBuilder<?>> implements ValueEditor<CacheBuilder<?>> {

    public static final String NAME = CacheBuilderValueConverter.NAME;

    private static final List<CacheBuilder<?>> SORTED_BUILDERS = new LinkedList<>();

    static {
        SORTED_BUILDERS.addAll(Jams.getCacheBuilderManager());
        sort();
        var listeners = new StaticListeners();
        Jams.getLanguageManager().registerListeners(listeners, false);
        Jams.getCacheBuilderManager().registerListeners(listeners, false);
    }

    private static void sort() {
        Language language = Jams.getLanguageManager().getSelected();
        SORTED_BUILDERS.sort(Comparator.comparing(target -> language.getOrDefault(target.getLanguageNode()), new NumericStringComparator()));
    }

    private Consumer<CacheBuilder<?>> listener = cacheBuilder -> {
    };

    public CacheBuilderValueEditor() {
        setCellFactory(list -> new CacheBuilderLanguageListCell());
        setButtonCell(new CacheBuilderLanguageListCell());

        getItems().setAll(SORTED_BUILDERS);
        getSelectionModel().select(Jams.getCacheBuilderManager().get(SyscallExecutionRunExceptionHandler.NAME).orElse(null));
        getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> listener.accept(val));
        Jams.getCacheBuilderManager().registerListeners(this, true);
    }

    private void refresh() {
        var selected = getSelectionModel().getSelectedItem();
        getItems().setAll(SORTED_BUILDERS);
        getSelectionModel().select(selected);
    }

    @Override
    public void setCurrentValue(CacheBuilder<?> value) {
        getSelectionModel().select(value);
    }

    @Override
    public CacheBuilder<?> getCurrentValue() {
        return getValue();
    }

    @Override
    public Node getAsNode() {
        return this;
    }

    @Override
    public Node buildConfigNode(Label label) {
        return new HBox(label, this);
    }

    @Override
    public void addListener(Consumer<CacheBuilder<?>> consumer) {
        listener = listener.andThen(consumer);
    }

    @Listener
    private void onCacheBuilderRegister(CacheBuilderRegisterEvent.After event) {
        refresh();
    }

    @Listener
    private void onCacheBuilderUnregister(CacheBuilderUnregisterEvent.After event) {
        if (getSelectionModel().getSelectedItem().equals(event.getCacheBuilder()))
            getSelectionModel().select(Jams.getCacheBuilderManager()
                    .get(SyscallExecutionRunExceptionHandler.NAME).orElse(null));
        refresh();
    }

    public static class Builder implements ValueEditor.Builder<CacheBuilder<?>> {

        @Override
        public ValueEditor<CacheBuilder<?>> build() {
            return new CacheBuilderValueEditor();
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
        private void onSyscallRegister(CacheBuilderRegisterEvent.After event) {
            SORTED_BUILDERS.add(event.getCacheBuilder());
            sort();
        }

        @Listener(priority = Integer.MAX_VALUE)
        private void onSyscallUnregister(CacheBuilderUnregisterEvent.After event) {
            SORTED_BUILDERS.add(event.getCacheBuilder());
            sort();
        }

    }
}

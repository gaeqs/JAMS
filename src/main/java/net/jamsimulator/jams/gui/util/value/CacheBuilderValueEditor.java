/*
 *  MIT License
 *
 *  Copyright (c) 2021 Gael Rial Costas
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.jamsimulator.jams.gui.util.value;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.util.converter.CacheBuilderValueConverter;
import net.jamsimulator.jams.gui.util.converter.ValueConverter;
import net.jamsimulator.jams.gui.util.converter.ValueConverters;
import net.jamsimulator.jams.language.Language;
import net.jamsimulator.jams.language.wrapper.CacheBuilderLanguageListCell;
import net.jamsimulator.jams.manager.Manager;
import net.jamsimulator.jams.manager.event.ManagerDefaultElementChangeEvent;
import net.jamsimulator.jams.manager.event.ManagerElementRegisterEvent;
import net.jamsimulator.jams.manager.event.ManagerElementUnregisterEvent;
import net.jamsimulator.jams.manager.event.ManagerSelectedElementChangeEvent;
import net.jamsimulator.jams.mips.memory.cache.CacheBuilder;
import net.jamsimulator.jams.mips.syscall.defaults.SyscallExecutionRunExceptionHandler;
import net.jamsimulator.jams.utils.representation.NumericStringComparator;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class CacheBuilderValueEditor extends ComboBox<CacheBuilder<?>> implements ValueEditor<CacheBuilder<?>> {

    public static final String NAME = CacheBuilderValueConverter.NAME;

    private static final List<CacheBuilder<?>> SORTED_BUILDERS = new LinkedList<>();

    static {
        SORTED_BUILDERS.addAll(Manager.of(CacheBuilder.class).stream().map(it -> (CacheBuilder<?>) it).toList());
        sort();
        var listeners = new StaticListeners();
        Manager.of(Language.class).registerListeners(listeners, false);
        Manager.of(CacheBuilder.class).registerListeners(listeners, false);
    }

    private Consumer<CacheBuilder<?>> listener = cacheBuilder -> {
    };

    public CacheBuilderValueEditor() {
        setCellFactory(list -> new CacheBuilderLanguageListCell());
        setButtonCell(new CacheBuilderLanguageListCell());

        getItems().setAll(SORTED_BUILDERS);
        getSelectionModel().select(Manager.of(CacheBuilder.class).get(SyscallExecutionRunExceptionHandler.NAME).orElse(null));
        getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> listener.accept(val));
        Manager.of(CacheBuilder.class).registerListeners(this, true);
    }

    private static void sort() {
        Language language = Manager.ofS(Language.class).getSelected();
        SORTED_BUILDERS.sort(Comparator.comparing(target -> language.getOrDefault(target.getLanguageNode()), new NumericStringComparator()));
    }

    private void refresh() {
        var selected = getSelectionModel().getSelectedItem();
        getItems().setAll(SORTED_BUILDERS);
        getSelectionModel().select(selected);
    }

    @Override
    public CacheBuilder<?> getCurrentValue() {
        return getValue();
    }

    @Override
    public void setCurrentValue(CacheBuilder<?> value) {
        getSelectionModel().select(value);
    }

    @Override
    public Node getAsNode() {
        return this;
    }

    @Override
    public Node buildConfigNode(Label label) {
        var box = new HBox(label, this);
        box.setSpacing(5);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    @Override
    public void addListener(Consumer<CacheBuilder<?>> consumer) {
        listener = listener.andThen(consumer);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ValueConverter<CacheBuilder<?>> getLinkedConverter() {
        return (ValueConverter<CacheBuilder<?>>)
                (Object) ValueConverters.getByTypeUnsafe(CacheBuilder.class);
    }

    @Listener
    private void onCacheBuilderRegister(ManagerElementRegisterEvent.After<CacheBuilder<?>> event) {
        refresh();
    }

    @Listener
    private void onCacheBuilderUnregister(ManagerElementUnregisterEvent.After<CacheBuilder<?>> event) {
        if (getSelectionModel().getSelectedItem().equals(event.getElement()))
            getSelectionModel().select(Manager.of(CacheBuilder.class)
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
        private void onLanguageChange(ManagerSelectedElementChangeEvent.After<Language> event) {
            sort();
        }

        @Listener(priority = Integer.MAX_VALUE)
        private void onLanguageChange(ManagerDefaultElementChangeEvent.After<Language> event) {
            sort();
        }

        @Listener(priority = Integer.MAX_VALUE)
        private void onSyscallRegister(ManagerElementRegisterEvent.After<CacheBuilder<?>> event) {
            SORTED_BUILDERS.add(event.getElement());
            sort();
        }

        @Listener(priority = Integer.MAX_VALUE)
        private void onSyscallUnregister(ManagerElementUnregisterEvent.After<CacheBuilder<?>> event) {
            SORTED_BUILDERS.add(event.getElement());
            sort();
        }

    }
}

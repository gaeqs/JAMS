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

package net.jamsimulator.jams.gui.mips.configuration.syscall;

import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import net.jamsimulator.jams.gui.explorer.ExplorerElement;
import net.jamsimulator.jams.gui.image.icon.Icons;
import net.jamsimulator.jams.gui.image.quality.QualityImageView;
import net.jamsimulator.jams.gui.util.AnchorUtils;
import net.jamsimulator.jams.gui.util.value.ValueEditors;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.language.wrapper.LanguageButton;
import net.jamsimulator.jams.language.wrapper.LanguageTooltip;
import net.jamsimulator.jams.manager.Manager;
import net.jamsimulator.jams.mips.syscall.SyscallExecutionBuilder;
import net.jamsimulator.jams.mips.syscall.bundle.SyscallExecutionBuilderBundle;
import net.jamsimulator.jams.mips.syscall.defaults.SyscallExecutionRunExceptionHandler;


public class MIPSConfigurationSyscallControls extends AnchorPane {

    public static final String STYLE_CLASS = "controls";
    public static final String BUTTONS_STYLE_CLASS = "buttons";
    public static final String BUNDLES_STYLE_CLASS = "bundles";

    private final MIPSConfigurationDisplaySyscallTab syscallTab;
    private final HBox buttonsHbox, bundleHbox;

    public MIPSConfigurationSyscallControls(MIPSConfigurationDisplaySyscallTab syscallTab) {
        this.syscallTab = syscallTab;

        getStyleClass().add(STYLE_CLASS);

        buttonsHbox = new HBox();
        bundleHbox = new HBox();

        buttonsHbox.getStyleClass().add(BUTTONS_STYLE_CLASS);
        bundleHbox.getStyleClass().add(BUNDLES_STYLE_CLASS);

        AnchorUtils.setAnchor(buttonsHbox, 0, 0, 0, -1);
        AnchorUtils.setAnchor(bundleHbox, 0, 0, -1, 0);
        getChildren().addAll(bundleHbox, buttonsHbox);

        populate();
    }


    private void populate() {
        generateAddButton();
        generateRemoveButton();
        generateSortButton();
        generateBundleBox();
    }

    private void generateAddButton() {
        var button = new Button(null, new QualityImageView(Icons.CONTROL_ADD, 16, 16));
        button.setTooltip(new LanguageTooltip(Messages.GENERAL_ADD));
        button.getStyleClass().add("button-bold");

        button.setOnAction(event -> {
            int id = syscallTab.getContents().getBiggestId() + 1;
            var builder = Manager.of(SyscallExecutionBuilder.class)
                    .get(SyscallExecutionRunExceptionHandler.NAME).map(SyscallExecutionBuilder::makeNewInstance).orElse(null);

            syscallTab.getConfiguration().getSyscallExecutionBuilders().put(id, builder);
            syscallTab.getContents().add(id, builder);
        });

        buttonsHbox.getChildren().add(button);
    }


    private void generateRemoveButton() {
        var button = new Button(null, new QualityImageView(Icons.CONTROL_REMOVE, 16, 16));
        button.setTooltip(new LanguageTooltip(Messages.GENERAL_REMOVE));
        button.getStyleClass().add("button-bold");

        button.setOnAction(event -> {
            var contents = syscallTab.getContents();
            var selected = contents.getSelectedElements();

            if (selected.isEmpty()) return;
            for (ExplorerElement element : selected) {
                if (!(element instanceof MIPSConfigurationSyscallContents.Representation)) continue;

                var previous = element.getPrevious();
                contents.remove((MIPSConfigurationSyscallContents.Representation) element);
                syscallTab.getConfiguration().getSyscallExecutionBuilders()
                        .remove(((MIPSConfigurationSyscallContents.Representation) element).getSyscallId());

                if (previous.isPresent()) {
                    contents.selectElementAlone(previous.get());
                } else {
                    contents.getMainSection().getElementByIndex(0).ifPresent(contents::selectElementAlone);

                    if (contents.getMainSection().isEmpty()) {
                        syscallTab.display(null);
                    }
                }
            }
        });

        buttonsHbox.getChildren().add(button);
    }

    private void generateSortButton() {
        var button = new Button(null, new QualityImageView(Icons.CONTROL_SORT, 16, 16));
        button.setTooltip(new LanguageTooltip(Messages.GENERAL_SORT));
        button.getStyleClass().add("button-bold");

        button.setOnAction(event -> syscallTab.getContents().sort());

        buttonsHbox.getChildren().add(button);
    }

    private void generateBundleBox() {

        var editor = ValueEditors.getByTypeUnsafe(SyscallExecutionBuilderBundle.class).build();

        var button = new LanguageButton(Messages.SIMULATION_CONFIGURATION_SYSTEM_CALLS_TAB_LOAD_BUNDLE);
        button.setOnAction(event -> {
            var bundle = editor.getCurrentValue();

            syscallTab.getConfiguration().getSyscallExecutionBuilders().clear();
            syscallTab.getConfiguration().getSyscallExecutionBuilders().putAll(bundle.buildBundle());
            syscallTab.getContents().reload();
        });

        bundleHbox.getChildren().addAll(editor.getAsNode(), button);
    }

}

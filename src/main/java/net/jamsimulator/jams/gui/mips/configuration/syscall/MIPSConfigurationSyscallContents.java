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

import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Region;
import net.jamsimulator.jams.gui.explorer.Explorer;
import net.jamsimulator.jams.gui.explorer.ExplorerBasicElement;
import net.jamsimulator.jams.gui.explorer.ExplorerSection;
import net.jamsimulator.jams.gui.util.value.ValueEditor;
import net.jamsimulator.jams.gui.util.value.ValueEditorBuilderManager;
import net.jamsimulator.jams.manager.Manager;
import net.jamsimulator.jams.mips.syscall.SyscallExecutionBuilder;

import java.util.ArrayList;
import java.util.List;

public class MIPSConfigurationSyscallContents extends Explorer {

    public static final String STYLE_CLASS = "contents";
    public static final String REPRESENTATION_STYLE_CLASS = "representation";

    private final MIPSConfigurationDisplaySyscallTab syscallTab;
    private final List<Representation> representations;

    public MIPSConfigurationSyscallContents(ScrollPane scrollPane, MIPSConfigurationDisplaySyscallTab syscallTab) {
        super(scrollPane, false, false);
        this.syscallTab = syscallTab;
        this.representations = new ArrayList<>();

        getStyleClass().add(STYLE_CLASS);

        generateMainSection();
        hideMainSectionRepresentation();
    }

    public void selectFirst() {
        if (!mainSection.isEmpty()) {
            mainSection.getElementByIndex(0).ifPresent(this::selectElementAlone);
        }
    }

    public int getBiggestId() {
        int max = 0;
        for (Representation representation : representations) {
            if (representation.getSyscallId() > max) {
                max = representation.getSyscallId();
            }
        }
        return max;
    }

    public void add(int id, SyscallExecutionBuilder<?> builder) {
        boolean wasEmpty = mainSection.isEmpty();
        var representation = new Representation(mainSection, id, builder);
        mainSection.addElement(representation);
        representations.add(representation);
        if (wasEmpty) {
            selectElementAlone(representation);
        }
    }

    public void remove(Representation representation) {
        mainSection.removeElement(representation);
        representations.remove(representation);
    }

    public void sort() {
        mainSection.refreshAllElements();
    }

    public void reload() {
        representations.clear();
        mainSection.clear();
        var data = syscallTab.getConfiguration().getSyscallExecutionBuilders();
        data.forEach(this::add);
        if (mainSection.isEmpty()) {
            syscallTab.display(null);
        }
    }

    @Override
    protected void generateMainSection() {
        mainSection = new ExplorerSection(this, null, "", 0, (o1, o2) -> {
            if (!(o1 instanceof Representation)) return -1;
            if (!(o2 instanceof Representation)) return 1;
            return ((Representation) o1).getSyscallId() - ((Representation) o2).getSyscallId();
        });

        reload();
        getChildren().add(mainSection);
    }

    private boolean isIdValid(int id) {
        for (Representation representation : representations) {
            if (representation.getSyscallId() == id) {
                return false;
            }
        }
        return true;
    }

    public class Representation extends ExplorerBasicElement {

        private int syscallId;
        private SyscallExecutionBuilder<?> builder;

        private ValueEditor<Integer> idEditor;
        @SuppressWarnings("rawtypes")
        private ValueEditor<SyscallExecutionBuilder> syscallEditor;

        public Representation(ExplorerSection parent, int syscallId, SyscallExecutionBuilder<?> builder) {
            super(parent, String.valueOf(syscallId), 1);
            getStyleClass().add(REPRESENTATION_STYLE_CLASS);
            this.syscallId = syscallId;
            this.builder = builder;

            idEditor.setCurrentValueUnsafe(syscallId);
            syscallEditor.setCurrentValueUnsafe(builder);

            idEditor.addListener(this::manageIdChange);
            syscallEditor.addListener(this::manageSyscallChange);
        }

        public int getSyscallId() {
            return syscallId;
        }

        public SyscallExecutionBuilder<?> getBuilder() {
            return builder;
        }

        @Override
        public void select() {
            if (selected) return;
            getStyleClass().add("selected-explorer-element");
            selected = true;
            syscallTab.display(builder);
        }

        @Override
        protected void loadElements() {
            var manager = Manager.get(ValueEditorBuilderManager.class);
            idEditor = manager.getByTypeUnsafe(Integer.class).build();
            syscallEditor = manager.getByTypeUnsafe(SyscallExecutionBuilder.class).build();
            var idNode = idEditor.getAsNode();
            var syscallNode = syscallEditor.getAsNode();

            getChildren().addAll(idNode, syscallNode);
            setSpacing(SPACING);
            setAlignment(Pos.CENTER_LEFT);

            ((Region) idEditor).setPrefWidth(50);
            ((Region) idEditor).setMinWidth(50);
            ((Region) syscallNode).setPrefWidth(1000000);

            idNode.focusedProperty().addListener((obs, old, val) -> {
                if (val) {
                    selectElementAlone(this);
                }
            });
            syscallNode.focusedProperty().addListener((obs, old, val) -> {
                if (val) {
                    selectElementAlone(this);
                }
            });
        }

        private void manageIdChange(int value) {
            if (syscallId == value) return;

            if (!isIdValid(value)) {
                idEditor.setCurrentValueUnsafe(syscallId);
                return;
            }

            var builders = syscallTab.getConfiguration().getSyscallExecutionBuilders();
            builders.remove(syscallId);
            builders.put(value, builder);

            syscallId = value;
        }

        private void manageSyscallChange(SyscallExecutionBuilder<?> builder) {
            this.builder = builder.makeNewInstance();

            var builders = syscallTab.getConfiguration().getSyscallExecutionBuilders();
            builders.put(syscallId, this.builder);

            if (selected) {
                syscallTab.display(this.builder);
            }
        }
    }

}

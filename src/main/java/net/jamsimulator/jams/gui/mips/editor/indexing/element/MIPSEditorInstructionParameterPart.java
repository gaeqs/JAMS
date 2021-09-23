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

package net.jamsimulator.jams.gui.mips.editor.indexing.element;

import net.jamsimulator.jams.gui.editor.code.indexing.EditorIndex;
import net.jamsimulator.jams.gui.editor.code.indexing.element.EditorIndexStyleableElement;
import net.jamsimulator.jams.gui.editor.code.indexing.element.EditorIndexedElementImpl;
import net.jamsimulator.jams.gui.editor.code.indexing.element.EditorIndexedParentElement;
import net.jamsimulator.jams.gui.editor.code.indexing.element.ElementScope;
import net.jamsimulator.jams.gui.editor.code.indexing.element.basic.EditorElementLabel;
import net.jamsimulator.jams.gui.editor.code.indexing.element.reference.EditorElementReference;
import net.jamsimulator.jams.gui.editor.code.indexing.element.reference.EditorReferencingElement;
import net.jamsimulator.jams.mips.parameter.ParameterPartType;
import net.jamsimulator.jams.project.mips.MIPSProject;
import net.jamsimulator.jams.utils.NumericUtils;
import net.jamsimulator.jams.utils.StringUtils;

import java.util.Collection;
import java.util.Set;

public class MIPSEditorInstructionParameterPart extends EditorIndexedElementImpl
        implements EditorIndexStyleableElement, EditorReferencingElement<EditorElementLabel> {

    protected final Type type;
    protected final Set<EditorElementReference<EditorElementLabel>> references;

    public MIPSEditorInstructionParameterPart(EditorIndex index, ElementScope scope, EditorIndexedParentElement parent,
                                              int start, String text, ParameterPartType partType) {
        super(index, scope, parent, start, text);

        if (partType == null) {
            type = Type.getByString(text, index.getProject() instanceof MIPSProject p ? p : null);
        } else {
            type = Type.getByType(partType);
        }

        if (type == Type.LABEL) {
            references = Set.of(new EditorElementReference<>(EditorElementLabel.class, getIdentifier()));
        } else {
            references = Set.of();
        }
    }

    @Override
    public Collection<String> getStyles() {
        if (type == Type.LABEL) {
            if (index.isIdentifierGlobal(getIdentifier())) {
                return Set.of(Type.GLOBAL_LABEL_STYLE);
            }
            var global = index.getGlobalIndex();
            if (global.isPresent()) {
                var reference = new EditorElementReference<>(EditorElementLabel.class, getIdentifier());
                var value = global.get().searchReferencedElement(reference);
                if (value.isPresent()) {
                    return Set.of(Type.GLOBAL_LABEL_STYLE);
                }
            }
        }

        return Set.of(type.getCssClass());
    }

    @Override
    public Set<EditorElementReference<EditorElementLabel>> getReferences() {
        return references;
    }

    public enum Type {

        REGISTER("instruction-parameter-register", "REGISTER"),
        IMMEDIATE("instruction-parameter-immediate", "IMMEDIATE"),
        STRING("instruction-parameter-string", "STRING"),
        LABEL("instruction-parameter-label", "LABEL");

        public static final String GLOBAL_LABEL_STYLE = "instruction-parameter-global-label";
        private final String cssClass;
        private final String languageNodeSufix;

        Type(String cssClass, String languageNodeSufix) {
            this.cssClass = cssClass;
            this.languageNodeSufix = languageNodeSufix;
        }

        public static Type getByType(ParameterPartType type) {
            return switch (type) {
                case REGISTER -> REGISTER;
                case IMMEDIATE -> IMMEDIATE;
                case LABEL -> LABEL;
                case STRING -> STRING;
            };
        }

        public static Type getByString(String string, MIPSProject project) {
            if (NumericUtils.isInteger(string) || NumericUtils.isFloat(string)) return IMMEDIATE;

            if (project == null) {
                if (string.startsWith("$")) return REGISTER;
            } else {
                if (project.getData().getRegistersBuilder().getValidRegistersStarts()
                        .stream().anyMatch(target -> string.startsWith(target.toString()))) {
                    return REGISTER;
                }
            }

            if (StringUtils.isStringOrChar(string)) return STRING;
            return LABEL;
        }

        public String getCssClass() {
            return cssClass;
        }

        public String getLanguageNodeSufix() {
            return languageNodeSufix;
        }
    }
}

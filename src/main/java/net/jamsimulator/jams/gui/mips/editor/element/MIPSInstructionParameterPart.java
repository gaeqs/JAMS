/*
 * MIT License
 *
 * Copyright (c) 2020 Gael Rial Costas
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.jamsimulator.jams.gui.mips.editor.element;

import net.jamsimulator.jams.mips.parameter.ParameterPartType;
import net.jamsimulator.jams.project.mips.MIPSProject;
import net.jamsimulator.jams.utils.NumericUtils;
import net.jamsimulator.jams.utils.StringUtils;

import java.util.List;

public class MIPSInstructionParameterPart extends MIPSCodeElement {

    private final MIPSInstructionParameter parameter;
    private final int index;
    private InstructionParameterPartType type;

    public MIPSInstructionParameterPart(MIPSLine line, MIPSFileElements elements, int startIndex, int endIndex, String text, MIPSInstructionParameter parameter, int index, ParameterPartType type) {
        super(line, startIndex, endIndex, text);

        this.parameter = parameter;
        this.index = index;

        if (type == null) {
            this.type = InstructionParameterPartType.getByString(text, elements.getProject().orElse(null));
        } else {
            this.type = InstructionParameterPartType.getByStringAndType(type);
        }
    }

    @Override
    public String getTranslatedNameNode() {
        return "MIPS_ELEMENT_INSTRUCTION_PARAMETER_" + type.getLanguageNodeSufix();
    }

    public MIPSInstructionParameter getParameter() {
        return parameter;
    }

    public int getIndex() {
        return index;
    }

    public InstructionParameterPartType getType() {
        return type;
    }

    @Override
    public String getSimpleText() {
        return text;
    }

    @Override
    public List<String> getStyles() {
        return getGeneralStyles(type.getCssClass());
    }

    @Override
    public void refreshMetadata(MIPSFileElements elements) {
        if (type != InstructionParameterPartType.LABEL && type != InstructionParameterPartType.GLOBAL_LABEL) return;

        var filesToAssemble = elements.getFilesToAssemble().orElse(null);

        boolean isGlobal;
        if (filesToAssemble == null) {
            isGlobal = elements.getLabels().contains(text) && elements.getSetAsGlobalLabel().contains(text);
        } else {
            isGlobal = filesToAssemble.getGlobalLabels().contains(text);
        }

        type = isGlobal ? InstructionParameterPartType.GLOBAL_LABEL : InstructionParameterPartType.LABEL;
    }

    public enum InstructionParameterPartType {
        REGISTER("mips-instruction-parameter-register", "REGISTER"),
        IMMEDIATE("mips-instruction-parameter-immediate", "IMMEDIATE"),
        STRING("mips-instruction-parameter-string", "STRING"),
        LABEL("mips-instruction-parameter-label", "LABEL"),
        GLOBAL_LABEL("mips-instruction-parameter-global-label", "GLOBAL_LABEL");

        private final String cssClass;
        private final String languageNodeSufix;

        InstructionParameterPartType(String cssClass, String languageNodeSufix) {
            this.cssClass = cssClass;
            this.languageNodeSufix = languageNodeSufix;
        }

        public String getCssClass() {
            return cssClass;
        }

        public String getLanguageNodeSufix() {
            return languageNodeSufix;
        }

        public static InstructionParameterPartType getByStringAndType(ParameterPartType type) {
            return switch (type) {
                case REGISTER -> REGISTER;
                case IMMEDIATE -> IMMEDIATE;
                case LABEL -> LABEL;
                case STRING -> STRING;
            };
        }

        public static InstructionParameterPartType getByString(String string, MIPSProject project) {
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
    }
}

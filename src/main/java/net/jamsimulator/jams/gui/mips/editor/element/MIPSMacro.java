package net.jamsimulator.jams.gui.mips.editor.element;

import net.jamsimulator.jams.utils.Validate;

import java.util.ArrayList;
import java.util.List;

public class MIPSMacro implements Comparable<MIPSMacro> {

    private final MIPSLine start;
    private final String name;
    private final String[] parameters;

    private int dummyStart;

    private MIPSLine end;

    public MIPSMacro(int start) {
        this.dummyStart = start;
        this.start = null;
        this.name = null;
        this.parameters = null;
    }

    public MIPSMacro(MIPSLine start, String name, String[] parameters) {
        Validate.notNull(start, "Start cannot be null!");
        Validate.notNull(name, "Name cannot be null!");
        Validate.notNull(parameters, "Parameters cannot be null!");
        this.start = start;
        this.name = name;
        this.parameters = parameters;
    }

    public MIPSMacro(MIPSLine start, MIPSDirective directive) {
        Validate.notNull(start, "Start cannot be null!");
        Validate.notNull(directive, "Directive cannot be null!");
        this.start = start;

        var parameters = directive.getParameters();

        if (parameters.isEmpty()) {
            this.name = "";
            this.parameters = new String[0];
        } else {
            this.name = parameters.get(0).getSimpleText();

            var list = new ArrayList<String>(parameters.size() - 1);

            for (int i = 1; i < parameters.size(); i++) {
                var parameter = parameters.get(i).getSimpleText()
                        .replace("(", "").replace(")", "");
                if (!parameter.isEmpty()) {
                    list.add(parameter);
                }
            }

            this.parameters = list.toArray(new String[0]);
        }
    }

    public String getName() {
        return name;
    }

    public String[] getParameters() {
        return parameters;
    }

    public MIPSLine getStart() {
        return start;
    }

    public MIPSLine getEnd() {
        return end;
    }

    public void setEndIfPrevious(MIPSLine end) {
        Validate.notNull(end, "End cannot be null!");
        Validate.isTrue(end.getStart() > start.getStart(), "The end cannot be previous to the start!");
        if (this.end == null || this.end.getStart() > end.getStart()) {
            this.end = end;
        }
    }

    public void searchNewEnd(int from, List<MIPSLine> lines) {
        end = null;

        var iterator = lines.listIterator(from);
        while (iterator.hasNext()) {
            var line = iterator.next();

            if (line.getDirective().isPresent()) {
                var directive = line.getDirective().get();
                if (directive.isMacro() || directive.isEndMacro()) {
                    end = line;
                    break;
                }
            }
        }
    }

    @Override
    public int compareTo(MIPSMacro o) {
        int o1 = start == null ? dummyStart : start.getStart();
        int o2 = o.start == null ? o.dummyStart : o.start.getStart();
        return o1 - o2;
    }
}

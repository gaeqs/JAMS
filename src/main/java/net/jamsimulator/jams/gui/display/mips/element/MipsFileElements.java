package net.jamsimulator.jams.gui.display.mips.element;

import net.jamsimulator.jams.gui.main.WorkingPane;
import net.jamsimulator.jams.utils.StringUtils;
import org.fxmisc.richtext.CodeArea;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a collection of assembly elements within an {@link net.jamsimulator.jams.mips.assembler.AssemblingFile}.
 */
public class MipsFileElements {

	private final List<MipsLine> lines;

	private final List<String> labels;

	/**
	 * Creates an empty element collection.
	 * To populate it use {@link #refreshAll(String, WorkingPane)}.
	 */
	public MipsFileElements() {
		this.lines = new ArrayList<>();
		this.labels = new LinkedList<>();
	}

	/**
	 * Returns all lines of the represented file.
	 *
	 * @return the lines.
	 */
	public List<MipsLine> getLines() {
		return lines;
	}

	/**
	 * Returns all labels of the represented file.
	 *
	 * @return the labels.
	 */
	public List<String> getLabels() {
		return labels;
	}

	/**
	 * Returns the element placed at the given index.
	 *
	 * @param index the index.
	 * @return the element, if found.
	 */
	public Optional<MipsCodeElement> getElementAt(int index) {
		MipsLine line = lines.get(lineOf(index));
		return line.getElementAt(index);
	}

	/**
	 * Returns the index of the file where the given absolute index is located at.
	 *
	 * @param index the absolute index.
	 * @return the line index or -1 if not found.
	 */
	public int lineOf(int index) {
		if (index == -1) return -1;
		MipsLine line;
		for (int i = 0; i < lines.size(); i++) {
			line = lines.get(i);
			if (line.getStart() <= index && line.getStart() + line.getText().length() >= index) return i;
		}
		return -1;
	}

	/**
	 * Removes the line located at the given index.
	 * <p>
	 * This method WONT remove the line from the file. This just removes it's style and internal information.
	 * If the line is not removed from the editor too the execution of this method will result on a buggy editor!
	 *
	 * @param lineIndex the absolute index.
	 */
	public void removeLine(int lineIndex) {
		if (lineIndex < 0 || lineIndex >= lines.size()) throw new IndexOutOfBoundsException("Index out of bounds");
		MipsLine line = lines.remove(lineIndex);
		line.getLabel().ifPresent(label -> labels.remove(label.getLabel()));
		int length = line.getText().length() + 1;

		for (int i = lineIndex; i < lines.size(); i++) {
			line = lines.get(i);
			line.setStart(line.getStart() - length);
		}
	}

	/**
	 * Adds a line at the given index.
	 * <p>
	 * This method WONT add the line to the file. This just adds it's style and internal information.
	 * If the line is not added to the editor too the execution of this method will result on a buggy editor!
	 *
	 * @param lineIndex the absolute index.
	 * @param line      the line to remove.
	 */
	public void addLine(int lineIndex, String line) {
		if (lineIndex < 0 || lineIndex > lines.size()) throw new IndexOutOfBoundsException("Index out of bounds");
		if (line.contains("\n") || line.contains("\r")) throw new IllegalArgumentException("Invalid line!");
		int start = 0;
		if (lineIndex > 0) {
			MipsLine previous = lines.get(lineIndex - 1);
			start = previous.getStart() + previous.getText().length() + 1;
		}
		MipsLine mipsLine = new MipsLine(start, line);
		parseLine(start, start + line.length(), line, mipsLine);
		lines.add(lineIndex, mipsLine);
		mipsLine.getLabel().ifPresent(label -> labels.add(label.getLabel()));

		int length = line.length() + 1;
		for (int i = lineIndex + 1; i < lines.size(); i++) {
			mipsLine = lines.get(i);
			mipsLine.setStart(mipsLine.getStart() + length);
		}
	}

	/**
	 * Replaces the line located at the given index with the new given line.
	 * <p>
	 * This method WONT edit the line of the file. This just edits it's style and internal information.
	 * If the line is not edited on the editor too the execution of this method will result on a buggy editor!
	 *
	 * @param lineIndex the absolute index.
	 * @param line      the line to edit.
	 */
	public void editLine(int lineIndex, String line) {
		if (lineIndex < 0 || lineIndex >= lines.size()) throw new IndexOutOfBoundsException("Index out of bounds");
		if (line.contains("\n") || line.contains("\r")) throw new IllegalArgumentException("Invalid line!");
		MipsLine old = lines.get(lineIndex);
		old.getLabel().ifPresent(label -> labels.remove(label.getLabel()));
		int difference = line.length() - old.getText().length();
		MipsLine mipsLine = new MipsLine(old.getStart(), line);
		parseLine(old.getStart(), old.getStart() + line.length(), line, mipsLine);
		lines.set(lineIndex, mipsLine);
		mipsLine.getLabel().ifPresent(label -> labels.add(label.getLabel()));

		for (int i = lineIndex + 1; i < lines.size(); i++) {
			mipsLine = lines.get(i);
			mipsLine.setStart(mipsLine.getStart() + difference);
		}
	}

	/**
	 * Returns whether the file has the given label declared.
	 *
	 * @param label the label
	 * @return whether the file has the given label declared.
	 */
	public boolean hasLabel(String label) {
		return labels.contains(label);
	}

	/**
	 * Returns the amount of times a label is declared on the file.
	 *
	 * @param label the label.
	 * @return the amount of times.
	 */
	public int labelCount(String label) {
		return (int) labels.stream().filter(target -> target.equals(label)).count();
	}

	/**
	 * Refreshes the file.
	 * This checks for errors automatically.
	 *
	 * @param lines       the file text.
	 * @param workingPane the working pane.
	 */
	public void refreshAll(String lines, WorkingPane workingPane) {
		this.lines.clear();

		if (lines.isEmpty()) return;

		int start = 0;
		int end = 0;
		StringBuilder builder = new StringBuilder();

		//Checks all lines
		char c;
		MipsLine mipsLine;
		while (lines.length() > end) {
			c = lines.charAt(end);
			if (c == '\n' || c == '\r') {
				mipsLine = new MipsLine(start, builder.toString());
				this.lines.add(mipsLine);
				//Checks line
				parseLine(start, end, builder.toString(), mipsLine);

				//Restarts the builder. 
				builder = new StringBuilder();
				start = end + 1;
			} else builder.append(c);
			end++;
		}
		if (end >= start) {
			mipsLine = new MipsLine(start, builder.toString());
			this.lines.add(mipsLine);
			//Checks the final line.
			parseLine(start, end, builder.toString(), mipsLine);
		}

		//Checks for errors.
		refreshLabels();
		searchAllErrors(workingPane);
	}

	/**
	 * Search for errors in all lines.
	 *
	 * @param workingPane the {@link WorkingPane} where the file is displayed.
	 */
	public void searchAllErrors(WorkingPane workingPane) {
		lines.forEach(line -> line.searchAllErrors(workingPane, this));
	}

	/**
	 * Search for errors in the selected lines.
	 *
	 * @param workingPane the {@link WorkingPane} where the file is displayed.
	 * @param from        the first line to check.
	 * @param amount      the amount of lines to check.
	 */
	public void searchAllErrors(WorkingPane workingPane, int from, int amount) {
		if (from < 0 || from + amount > lines.size())
			throw new IndexOutOfBoundsException("Index out of bounds. [" + from + ", " + (from + amount) + ")");
		for (int i = 0; i < amount; i++) {
			lines.get(from + i).searchAllErrors(workingPane, this);
		}
	}

	/**
	 * Search for label errors in all lines.
	 */
	public List<Integer> searchLabelErrors() {
		List<Integer> updated = new ArrayList<>();

		Iterator<MipsLine> iterator = lines.iterator();
		int i = 0;
		while (iterator.hasNext()) {
			if (iterator.next().searchLabelErrors(labels)) updated.add(i);
			i++;
		}
		return updated;
	}

	/**
	 * Refresh the labels' list.
	 */
	public void refreshLabels() {
		labels.clear();
		lines.forEach(line -> line.getLabel().ifPresent(label -> labels.add(label.getLabel())));
	}

	/**
	 * Styles the selected lines.
	 *
	 * @param area   the area to style.
	 * @param from   the first line index.
	 * @param amount the amount of lines to style.
	 */
	public void styleLines(CodeArea area, int from, int amount) {
		if (from < 0 || from + amount > lines.size())
			throw new IndexOutOfBoundsException("Index out of bounds. [" + from + ", " + (from + amount) + ")");
		for (int i = 0; i < amount; i++) {
			lines.get(from + i).styleLine(area, from + i);
		}
	}

	/**
	 * Styles the selected lines.
	 *
	 * @param area  the area to style.
	 * @param lines the lines to update.
	 */
	public void styleLines(CodeArea area, Collection<Integer> lines) {
		lines.forEach(target -> this.lines.get(target).styleLine(area, target));
	}

	public String getReformattedCode() {
		StringBuilder builder = new StringBuilder();
		boolean lastEmpty = false;
		String trim;
		for (MipsLine line : lines) {
			trim = line.getText().trim();
			if (trim.isEmpty()) {
				if (!lastEmpty) {
					lastEmpty = true;
					builder.append('\n');
				}
			} else {
				line.appendReformattedLine(builder);
				builder.append('\n');
				lastEmpty = false;
			}
		}
		String result = builder.toString();
		return result.isEmpty() ? result : result.substring(0, result.length() - 1);
	}

	private void parseLine(int start, int end, String line, MipsLine mipsLine) {

		//COMMENT
		int commentIndex = StringUtils.getCommentIndex(line);
		if (commentIndex != -1) {
			mipsLine.setComment(new DisplayComment(start + commentIndex, end, line.substring(commentIndex)));
			end = start + commentIndex;
			line = line.substring(0, commentIndex);
		}

		//LABEL
		int labelIndex = line.indexOf(":");
		if (labelIndex != -1) {
			mipsLine.setLabel(new DisplayLabel(start, start + labelIndex, line.substring(0, labelIndex + 1)));
			start = start + labelIndex + 1;
			line = line.substring(labelIndex + 1);
		}

		String trim = line.trim();
		if (trim.isEmpty()) return;
		//DIRECTIVE OR INSTRUCTION
		if (trim.startsWith(".")) parseDirective(start, line, mipsLine);
		else parseInstruction(start, line, mipsLine);
	}

	private void parseDirective(int start, String line, MipsLine mipsLine) {
		Map<Integer, String> parts = StringUtils.multiSplitIgnoreInsideStringWithIndex(line, false, " ", ",", "\t");
		if (parts.isEmpty()) return;

		//Sorts all entries by their indices.
		List<Map.Entry<Integer, String>> stringParameters = parts.entrySet().stream()
				.sorted(Comparator.comparingInt(Map.Entry::getKey)).collect(Collectors.toList());

		//The first entry is the directive itself.
		Map.Entry<Integer, String> first = stringParameters.get(0);
		DisplayDirective directive = new DisplayDirective(start + first.getKey(), start + first.getKey()
				+ first.getValue().length(), first.getValue());

		mipsLine.setDirective(directive);
		stringParameters.remove(0);

		//Adds all parameters.
		int parameterIndex = 0;
		DisplayDirectiveParameter parameter;
		for (Map.Entry<Integer, String> entry : stringParameters) {
			parameter = new DisplayDirectiveParameter(
					directive, parameterIndex,
					start + entry.getKey(),
					start + entry.getKey() + entry.getValue().length(), entry.getValue(),
					StringUtils.isStringOrChar(entry.getValue()));
			directive.addParameter(parameter);
			parameterIndex++;
		}
	}

	private void parseInstruction(int start, String line, MipsLine mipsLine) {
		Map<Integer, String> parts = StringUtils.multiSplitIgnoreInsideStringWithIndex(line, false, " ", ",", "\t");
		if (parts.isEmpty()) return;

		//Sorts all entries by their indices.
		List<Map.Entry<Integer, String>> stringParameters = parts.entrySet().stream()
				.sorted(Comparator.comparingInt(Map.Entry::getKey)).collect(Collectors.toList());

		//The first entry is the instruction itself.
		Map.Entry<Integer, String> first = stringParameters.get(0);
		DisplayInstruction instruction = new DisplayInstruction(start + first.getKey(), start + first.getKey()
				+ first.getValue().length(), first.getValue());
		mipsLine.setInstruction(instruction);
		stringParameters.remove(0);

		//Adds all parameters.
		int parameterIndex = 0;
		InstructionParameter parameter;
		for (Map.Entry<Integer, String> entry : stringParameters) {

			//Parses the parameter.
			parameter = parseParameter(start + entry.getKey(), entry.getValue(), instruction, parameterIndex);
			instruction.addParameter(parameter);
			parameterIndex++;
		}
	}

	private InstructionParameter parseParameter(int start, String line, DisplayInstruction instruction, int parameterIndex) {
		//Splits all parts.
		Map<Integer, String> parts = StringUtils.multiSplitIgnoreInsideStringWithIndex(line, false, "+", "(", ")");

		//The parameter instance.
		InstructionParameter parameter = new InstructionParameter(line, instruction);

		//Adds all parts.
		parts.forEach((index, string) -> {
			DisplayInstructionParameterPart part = new DisplayInstructionParameterPart(instruction, parameterIndex,
					start + index,
					start + index + string.length(), string,
					DisplayInstructionParameterPart.InstructionParameterPartType.getByString(string));
			parameter.addPart(part);
		});
		return parameter;
	}

}

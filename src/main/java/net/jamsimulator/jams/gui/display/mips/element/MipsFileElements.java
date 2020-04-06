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

	/**
	 * Creates an empty element collection.
	 * To populate it use {@link #refreshAll(String, WorkingPane)}.
	 */
	public MipsFileElements() {
		this.lines = new ArrayList<>();
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
		int difference = line.length() - old.getText().length();
		MipsLine mipsLine = new MipsLine(old.getStart(), line);
		parseLine(old.getStart(), old.getStart() + line.length(), line, mipsLine);
		lines.set(lineIndex, mipsLine);

		for (int i = lineIndex + 1; i < lines.size(); i++) {
			mipsLine = lines.get(i);
			mipsLine.setStart(mipsLine.getStart() + difference);
		}
	}

	/**
	 * Returns the amount of times a label is declared on the file.
	 *
	 * @param label the label.
	 * @return the amount of times.
	 */
	public int labelCount(String label) {
		return (int) lines.stream().filter(target -> target.getLabel().isPresent()
				&& target.getLabel().get().getLabel().equals(label)).count();
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
		searchErrors(workingPane);
	}

	public void searchErrors(WorkingPane workingPane) {
		this.lines.forEach(line -> line.searchErrors(workingPane, this));
	}

	public void styleLines(CodeArea area, int from, int amount) {
		if (from < 0 || from + amount > lines.size())
			throw new IndexOutOfBoundsException("Index out of bounds. [" + from + ", " + (from + amount) + ")");
		for (int i = 0; i < amount; i++) {
			lines.get(from + i).styleLine(area, from + i);
		}
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

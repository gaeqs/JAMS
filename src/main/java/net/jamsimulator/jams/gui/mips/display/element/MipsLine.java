package net.jamsimulator.jams.gui.mips.display.element;

import net.jamsimulator.jams.gui.main.WorkingPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.util.*;

/**
 * Represents a MIPS program's line. A line can contain a label, a directive or a instruction and a comment.
 * This class is not intended to be modified after it's creation, as it may cause several align problems in the editor.
 */
public class MipsLine {

	private int start;
	private final String text;

	private DisplayLabel label;
	private DisplayDirective directive;
	private DisplayInstruction instruction;
	private DisplayComment comment;

	private SortedSet<MipsCodeElement> elements;

	/**
	 * Creates a MipsLine. To add it's components use the component's setters.
	 *
	 * @param start the start index of the line.
	 * @param text  the text of the line.
	 */
	public MipsLine(int start, String text) {
		this.start = start;
		this.text = text;
	}

	/**
	 * Returns the start index of the line. This is the index of the first character of this line inside the file.
	 *
	 * @return the start index.
	 */
	public int getStart() {
		return start;
	}

	/**
	 * Sets the start index of the line. This also edits the start index of all it's children.
	 * <p>
	 * This method is used to move the line when one or more previous lines are edited.
	 *
	 * @param start the new start index.
	 */
	public void setStart(int start) {
		int offset = start - this.start;

		this.start = start;

		if (comment != null) comment.move(offset);
		if (label != null) label.move(offset);
		if (instruction != null) instruction.move(offset);
		if (directive != null) directive.move(offset);
	}

	/**
	 * Returns the text of the line.
	 *
	 * @return the text of the line.
	 */
	public String getText() {
		return text;
	}

	/**
	 * Returns the {@link DisplayLabel} of the line, if present.
	 *
	 * @return the {@link DisplayLabel} of the line, if present.
	 */
	public Optional<DisplayLabel> getLabel() {
		return Optional.ofNullable(label);
	}

	/**
	 * Sets the {@link DisplayLabel} of the line or null.
	 *
	 * @param label the {@link DisplayLabel} or null.
	 */
	public void setLabel(DisplayLabel label) {
		this.label = label;
		elements = null;
	}

	/**
	 * Returns the {@link DisplayDirective} of the line, if present.
	 *
	 * @return the {@link DisplayDirective} of the line, if present.
	 */
	public Optional<DisplayDirective> getDirective() {
		return Optional.ofNullable(directive);
	}

	/**
	 * Sets the {@link DisplayDirective} of the line or null.
	 *
	 * @param directive the {@link DisplayDirective} or null.
	 */
	public void setDirective(DisplayDirective directive) {
		this.directive = directive;
		elements = null;
	}

	/**
	 * Returns the {@link DisplayInstruction} of the line, if present.
	 *
	 * @return the {@link DisplayInstruction} of the line, if present.
	 */
	public Optional<DisplayInstruction> getInstruction() {
		return Optional.ofNullable(instruction);
	}

	/**
	 * Sets the {@link DisplayDirective} of the line or null.
	 *
	 * @param instruction the {@link DisplayDirective} or null.
	 */
	public void setInstruction(DisplayInstruction instruction) {
		this.instruction = instruction;
		elements = null;
	}

	/**
	 * Returns the {@link DisplayComment} of the line, if present.
	 *
	 * @return the {@link DisplayComment} of the line, if present.
	 */
	public Optional<DisplayComment> getComment() {
		return Optional.ofNullable(comment);
	}

	/**
	 * Sets the {@link DisplayComment} of the line or null.
	 *
	 * @param comment the {@link DisplayComment} or null.
	 */
	public void setComment(DisplayComment comment) {
		this.comment = comment;
		elements = null;
	}

	/**
	 * Returns all {@link MipsCodeElement} inside this line, sorted by their start index.
	 * <p>
	 * This is a lazy method. The set will be created if it's null.
	 * Any setter of this class will set the elements set to null.
	 *
	 * @return the {@link SortedSet}.
	 */
	public SortedSet<MipsCodeElement> getSortedElements() {
		if (elements != null) return elements;
		elements = new TreeSet<>(Comparator.comparingInt(o -> o.startIndex));
		if (comment != null) elements.add(comment);
		if (label != null) elements.add(label);

		if (instruction != null) {
			elements.add(instruction);
			instruction.getParameters().forEach(parameter -> elements.addAll(parameter.getParts()));
		}
		if (directive != null) {
			elements.add(directive);
			elements.addAll(directive.getParameters());
		}

		return elements;
	}

	/**
	 * Returns the element located at the given index, if present.
	 * <p>
	 * The given index must be an absolute file index, not a relative one.
	 * If you have a relative index you can calculate an absolute index using {@code index + line.getStart()}.
	 *
	 * @param index the absolute index.
	 * @return the element, if present.
	 */
	public Optional<MipsCodeElement> getElementAt(int index) {
		for (MipsCodeElement element : getSortedElements()) {
			if (element.startIndex <= index && element.endIndex > index) return Optional.of(element);
		}
		return Optional.empty();
	}

	/**
	 * Styles the selected line of the given {@link CodeArea}.
	 * <p>
	 * {@link MipsLine}s don't store their line index, so it must be given to the method.
	 *
	 * @param area the {@link CodeArea} to style.
	 * @param line the line index.
	 */
	public void styleLine(CodeArea area, int line) {
		int textLength = text.length();
		int lastEnd = start;
		StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

		if (textLength == 0 || getSortedElements().isEmpty()) {
			spansBuilder.add(Collections.emptyList(), 0);
			area.setStyleSpans(line, 0, spansBuilder.create());
			return;
		}


		for (MipsCodeElement element : getSortedElements()) {
			try {
				lastEnd = styleElement(spansBuilder, element, lastEnd);
			} catch (Exception exception) {
				System.err.println("Last: " + lastEnd);
				System.err.println("Element: " + element);
				throw exception;
			}
		}

		if (textLength > lastEnd) {
			spansBuilder.add(Collections.emptyList(), textLength - lastEnd);
		}
		area.setStyleSpans(line, 0, spansBuilder.create());
	}

	/**
	 * Search for errors inside all this {@link MipsLine}'s elements.
	 *
	 * @param pane     the {@link WorkingPane} where the file of this line is displayed.
	 * @param elements the {@link MipsFileElements elements of the file}.
	 */
	public void searchAllErrors(WorkingPane pane, MipsFileElements elements) {
		getSortedElements().forEach(target -> target.searchErrors(pane, elements));
	}

	/**
	 * Search for label errors inside all this {@link MipsLine}'s elements.
	 *
	 * @param labels the labels declared in the file.
	 * @return whether the line errors have been modified.
	 */
	public boolean searchLabelErrors(List<String> labels) {
		boolean updated = false;
		for (MipsCodeElement element : getSortedElements()) {
			updated |= element.searchLabelErrors(labels);
		}
		return updated;
	}

	public void appendReformattedLine(StringBuilder builder) {
		if (label != null) {
			builder.append(label.text.trim());
		}
		if (directive != null) {
			builder.append('\t');
			directive.appendReformattedCode(builder);
		}
		if (instruction != null) {
			builder.append('\t');
			instruction.appendReformattedCode(builder);
		}
		if (comment != null) {
			if (label != null) {
				if (directive == null || instruction == null) builder.append('\t');
				else builder.append(' ');
			} else if (directive == null || instruction == null) builder.append('\t');
			builder.append(comment.text);
		}
	}

	private int styleElement(StyleSpansBuilder<Collection<String>> spansBuilder, MipsCodeElement element, int lastEnd) {
		if (element.getStartIndex() != lastEnd) {
			spansBuilder.add(Collections.emptyList(), element.getStartIndex() - lastEnd);
		}
		spansBuilder.add(element.getStyles(), element.getEndIndex() - element.getStartIndex());
		return element.getEndIndex();
	}
}

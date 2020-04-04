package net.jamsimulator.jams.gui.display.assembly.element;

import net.jamsimulator.jams.utils.StringUtils;

import java.util.*;

public class AssemblyFileElements {

	//private final Pattern PATTERN = Pattern.compile("([^#\\n]+:)?([^#\"\\n]*)?([#;].*)?\\n?");

	private final Set<DisplayComment> comments;
	private final Set<DisplayLabel> labels;
	private final Set<DisplayString> strings;
	private final Set<DisplayDirective> directives;
	private final Set<DisplayDirectiveParameter> directivesParameters;

	public AssemblyFileElements() {
		this.comments = new HashSet<>();
		this.labels = new HashSet<>();
		this.strings = new HashSet<>();
		this.directives = new HashSet<>();
		this.directivesParameters = new HashSet<>();
	}

	public Set<DisplayComment> getComments() {
		return comments;
	}

	public Set<DisplayLabel> getLabels() {
		return labels;
	}

	public Set<DisplayDirective> getDirectives() {
		return directives;
	}

	public Set<AssemblyCodeElement> getElements() {
		Set<AssemblyCodeElement> elements = new HashSet<>();
		elements.addAll(comments);
		elements.addAll(labels);
		elements.addAll(strings);
		elements.addAll(directives);
		elements.addAll(directivesParameters);
		return Collections.unmodifiableSet(elements);
	}

	public SortedSet<AssemblyCodeElement> getSortedElements() {
		SortedSet<AssemblyCodeElement> elements = new TreeSet<>(Comparator.comparingInt(o -> o.startIndex));
		elements.addAll(comments);
		elements.addAll(labels);
		elements.addAll(strings);
		elements.addAll(directives);
		elements.addAll(directivesParameters);
		return Collections.unmodifiableSortedSet(elements);
	}

	public void refresh(String lines) {
		comments.clear();
		labels.clear();
		strings.clear();
		directives.clear();
		directivesParameters.clear();
		if (lines.isEmpty()) return;
		int start = 0;
		int end = 0;
		StringBuilder builder = new StringBuilder();

		char c;
		while (lines.length() > end) {
			c = lines.charAt(end);
			if (c == '\n' || c == '\r') {
				parseLine(start, end, builder.toString());
				builder = new StringBuilder();
				start = end + 1;
			} else builder.append(c);
			end++;
		}
		if (end < start) return; //Empty
		parseLine(start, end, builder.toString());
	}

	private void parseLine(int start, int end, String line) {
		/*Matcher matcher = PATTERN.matcher(line);

		String group;
		int rStart;
		int rEnd;
		while (matcher.find()) {
			//LABEL
			group = matcher.group(1);
			if (group != null) {
				rStart = matcher.start(1);
				rEnd = matcher.end(1);
				labels.add(new DisplayLabel(start + rStart, start + rEnd, group));
			}
			//DIRECTIVE OR INSTRUCTION
			group = matcher.group(2);
			if (group != null) {
				rStart = matcher.start(2);
				rEnd = matcher.end(2);
				if (group.trim().startsWith(".")) {
					parseDirective(start + rStart, start + rEnd, group);
				}
			}
			////STRING
			//group = matcher.group(3);
			//if (group != null) {
			//	rStart = matcher.start(3);
			//	rEnd = matcher.end(3);
			//	strings.add(new DisplayString(start + rStart, start + rEnd, group));
			//}
			//COMMENT
			group = matcher.group(3);
			if (group != null) {
				rStart = matcher.start(3);
				rEnd = matcher.end(3);
				comments.add(new DisplayComment(start + rStart, start + rEnd, group));
			}
		}*/

		//COMMENT
		int commentIndex = StringUtils.getCommentIndex(line);
		if (commentIndex != -1) {
			comments.add(new DisplayComment(start + commentIndex, end, line.substring(commentIndex)));
			end = start + commentIndex;
			line = line.substring(0, commentIndex);
		}

		//LABEL
		int labelIndex = line.indexOf(":");
		if (labelIndex != -1) {
			labels.add(new DisplayLabel(start, start + labelIndex, line.substring(0, labelIndex + 1)));
			start = start + labelIndex + 1;
			line = line.substring(labelIndex + 1);
		}

		//DIRECTIVE
		if (line.trim().startsWith(".")) parseDirective(start, line);

	}

	private void parseDirective(int start, String line) {
		Map<Integer, String> parts = StringUtils.multiSplitIgnoreInsideStringWithIndex(line, false, " ", ",", "\t");
		if (parts.isEmpty()) return;

		Map.Entry<Integer, String> first = parts.entrySet().stream().min(Comparator.comparingInt(Map.Entry::getKey)).get();
		directives.add(new DisplayDirective(start + first.getKey(), start + first.getKey()
				+ first.getValue().length(), first.getValue()));
		parts.remove(first.getKey());

		parts.forEach((key, value) -> directivesParameters.add(new DisplayDirectiveParameter(
				start + key,
				start + key + value.length(), value,
				isStringOrChar(value))));
	}


	private static boolean isStringOrChar(String string) {
		return string.startsWith("\"") && string.endsWith("\"") ||
				string.startsWith("'") && string.endsWith("'");
	}

}

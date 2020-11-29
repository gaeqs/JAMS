package net.jamsimulator.jams.gui.util;

import javafx.scene.control.IndexRange;
import org.fxmisc.richtext.StyleClassedTextArea;

import java.util.LinkedList;
import java.util.List;

public class StringStyler {

	public static void style(String string, StyleClassedTextArea area) {
		StringBuilder builder = new StringBuilder();
		List<Entry> styles = new LinkedList<>();

		//Data
		CurrentStyle currentStyle = new CurrentStyle();

		int close;
		char[] charArray = string.toCharArray();

		int from = 0;
		char c;
		for (int index = 0, charArrayLength = charArray.length; index < charArrayLength; index++) {
			c = charArray[index];
			if (c == '\\' && charArray[index + 1] == '<') {
				builder.append('<');
				index++;
			} else if (c == '<') {
				close = string.indexOf('>', index);
				if (close == -1) {
					throw new IllegalArgumentException("Bad string format");
				}

				//Create style
				styles.add(new Entry(new IndexRange(from, builder.length()), currentStyle.getStyles()));

				//Check
				currentStyle.update(string.substring(index + 1, close));
				from = builder.length();
				index = close;
			} else {
				builder.append(c);
			}
		}

		if (charArray.length - 1 > from) {
			//Create style
			styles.add(new Entry(new IndexRange(from, builder.length()), currentStyle.getStyles()));
		}

		area.replaceText(builder.toString());
		styles.forEach(style -> area.setStyle(style.getRange().getStart(), style.getRange().getEnd(), style.getStyle()));
	}

	private static class CurrentStyle {

		private boolean bold;
		private boolean code;
		private boolean black;
		private boolean underline;
		private boolean italic;
		private boolean sub;

		public List<String> getStyles() {
			var list = new LinkedList<String>();
			list = new LinkedList<>();S
			if (bold) list.add("bold");
			if (code) list.add("code");
			if (black) list.add("black");
			if (underline) list.add("underline");
			if (italic) list.add("italic");
			if (sub) list.add("sub");
			return list;
		}

		public void update(String tagText) {
			boolean value = true;
			if (tagText.startsWith("/")) {
				value = false;
				tagText = tagText.substring(1);
			}
			switch (tagText) {
				case "b" -> bold = value;
				case "code" -> code = value;
				case "black" -> black = value;
				case "u" -> underline = value;
				case "i" -> italic = value;
				case "sub" -> sub = value;
			}
		}


	}

	private static class Entry {

		private final IndexRange range;
		private final List<String> style;

		public Entry(IndexRange range, List<String> style) {
			this.range = range;
			this.style = style;
		}

		public IndexRange getRange() {
			return range;
		}

		public List<String> getStyle() {
			return style;
		}
	}

}

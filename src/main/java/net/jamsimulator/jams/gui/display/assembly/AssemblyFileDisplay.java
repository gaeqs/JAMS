package net.jamsimulator.jams.gui.display.assembly;

import net.jamsimulator.jams.gui.display.FileDisplay;
import net.jamsimulator.jams.gui.display.FileDisplayTab;
import net.jamsimulator.jams.gui.display.assembly.element.AssemblyCodeElement;
import net.jamsimulator.jams.gui.display.assembly.element.AssemblyFileElements;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.Subscription;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.SortedSet;

public class AssemblyFileDisplay extends FileDisplay {

	private AssemblyFileElements elements;

	public AssemblyFileDisplay(FileDisplayTab tab) {
		super(tab);
		elements = new AssemblyFileElements();
		Subscription subscription = multiPlainChanges().successionEnds(Duration.ofMillis(100))
				.subscribe(ignore -> index());
		index();
	}

	private void index() {
		elements.refresh(getText());
		setStyleSpans(0, computeHighlighting());
	}

	private StyleSpans<Collection<String>> computeHighlighting() {
		int textLength = getText().length();
		int lastKwEnd = 0;
		StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

		if(textLength == 0) {
			spansBuilder.add(Collections.emptyList(),  0);
			return spansBuilder.create();
		}

		SortedSet<AssemblyCodeElement> codeElements = elements.getSortedElements();

		for (AssemblyCodeElement element : codeElements) {
			spansBuilder.add(Collections.emptyList(), element.getStartIndex() - lastKwEnd);
			spansBuilder.add(Collections.singleton(element.getStyle()), element.getEndIndex() - element.getStartIndex());
			lastKwEnd = element.getEndIndex();
		}

		if(textLength > lastKwEnd) {
			spansBuilder.add(Collections.emptyList(), textLength - lastKwEnd);
		}
		return spansBuilder.create();
	}

	/*private StyleSpans<Collection<String>> computeHighlighting() {
		Matcher matcher = PATTERN.matcher(text);
		int lastKwEnd = 0;
		StyleSpansBuilder <Collection<String>> spansBuilder = new StyleSpansBuilder<>();
		while (matcher.find()) {
			String styleClass = getMatchedClass(matcher);

			spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
			spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
			lastKwEnd = matcher.end();
		}
		spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
		return spansBuilder.create();
	}*/


}
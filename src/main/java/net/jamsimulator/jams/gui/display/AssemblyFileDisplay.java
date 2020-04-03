package net.jamsimulator.jams.gui.display;

import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.Subscription;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AssemblyFileDisplay extends FileDisplay {

	private static final String COMMENT_PATTERN = "[#;][^\\n\\r]*";
	private static final String LABEL_PATTERN = "^[\\t ]*[A-Za-z0-9_\\-.]*:";
	private static final String DIRECTIVE_PATTERN = "\\.[^#\\n\\r]*";

	private static final Pattern PATTERN = Pattern.compile("(?<COMMENT>" + COMMENT_PATTERN + ")|" +
			"(?<LABEL>" + LABEL_PATTERN + ")|" +
			"(?<DIRECTIVE>" + DIRECTIVE_PATTERN + ")", Pattern.MULTILINE);

	public AssemblyFileDisplay(FileDisplayTab tab) {
		super(tab);
		Subscription subscription = multiPlainChanges().successionEnds(Duration.ofMillis(100))
				.subscribe(ignore -> setStyleSpans(0, computeHighlighting(getText())));
	}

	private static StyleSpans<Collection<String>> computeHighlighting(String text) {
		Matcher matcher = PATTERN.matcher(text);
		int lastKwEnd = 0;
		StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
		while (matcher.find()) {
			String styleClass = getMatchedClass(matcher);

			spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
			spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
			lastKwEnd = matcher.end();
		}
		spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
		return spansBuilder.create();
	}


	private static String getMatchedClass(Matcher matcher) {
		if (matcher.group("COMMENT") != null) return "comment";
		if (matcher.group("LABEL") != null) return "label";
		if (matcher.group("DIRECTIVE") != null) return "directive";
		return null;
	}
}

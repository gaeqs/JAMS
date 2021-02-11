package net.jamsimulator.jams.gui.util;

import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.util.Collection;
import java.util.Collections;

public class EasyStyleSpansBuilder {

    private final StyleSpansBuilder<Collection<String>> builder;
    private int to;

    public EasyStyleSpansBuilder() {
        builder = new StyleSpansBuilder<>();
        to = 0;
    }

    public void add(int index, String element, Collection<String> styles) {
        if (to < index) {
            builder.add(Collections.emptyList(), index - to);
        }
        builder.add(styles, element.length());
        to = index + element.length();
    }

    public boolean isEmpty() {
        return to == 0;
    }

    public StyleSpans<Collection<String>> create() {
        return builder.create();
    }


}

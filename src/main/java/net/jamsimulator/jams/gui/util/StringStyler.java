/*
 *  MIT License
 *
 *  Copyright (c) 2021 Gael Rial Costas
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.jamsimulator.jams.gui.util;

import javafx.scene.control.IndexRange;
import org.fxmisc.richtext.StyleClassedTextArea;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class StringStyler {

    public static void style(String string, StyleClassedTextArea area) {
        StringBuilder builder = new StringBuilder();
        List<Entry> styles = new LinkedList<>();

        //Data
        CurrentStyle currentStyle = new CurrentStyle();

        int close;
        char[] charArray = string.toCharArray();

        String tag;
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
                    builder.append(c);
                    continue;
                }

                tag = string.substring(index + 1, close);
                if (!CurrentStyle.isValid(tag)) {
                    builder.append(c);
                    continue;
                }

                //Create style
                styles.add(new Entry(new IndexRange(from, builder.length()), currentStyle.getStyles()));

                //Check
                currentStyle.update(tag);
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

        private static final Set<String> tags = Set.of("b", "code", "black", "u", "i", "sub");

        private boolean bold;
        private boolean code;
        private boolean black;
        private boolean underline;
        private boolean italic;
        private boolean sub;

        public static boolean isValid(String tag) {
            if (tag.startsWith("/")) tag = tag.substring(1);
            return tags.contains(tag);
        }

        public List<String> getStyles() {
            var list = new LinkedList<String>();
            list = new LinkedList<>();
            if (bold) list.add("bold");
            if (code) list.add("code");
            if (black) list.add("black");
            if (underline) list.add("underline");
            if (italic) list.add("italic");
            if (sub) list.add("sub");
            return list;
        }

        public void update(String tag) {
            boolean value = true;
            if (tag.startsWith("/")) {
                value = false;
                tag = tag.substring(1);
            }
            switch (tag) {
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

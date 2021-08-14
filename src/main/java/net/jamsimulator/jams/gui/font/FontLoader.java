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

package net.jamsimulator.jams.gui.font;

import javafx.scene.text.Font;
import net.jamsimulator.jams.Jams;

import java.io.IOException;
import java.io.InputStream;

/**
 * Loads the fonts bundled with JAMS.
 */
public class FontLoader {

    /**
     * Loads the fonts.
     */
    public static void load() {
        loadDefaults();
    }


    private static void loadDefaults() {
        try {
            load("/gui/font/JetBrainsMono-Bold.ttf");
            load("/gui/font/JetBrainsMono-Bold-Italic.ttf");
            load("/gui/font/JetBrainsMono-ExtraBold.ttf");
            load("/gui/font/JetBrainsMono-ExtraBold-Italic.ttf");
            load("/gui/font/JetBrainsMono-Italic.ttf");
            load("/gui/font/JetBrainsMono-Medium.ttf");
            load("/gui/font/JetBrainsMono-Medium-Italic.ttf");
            load("/gui/font/JetBrainsMono-Regular.ttf");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void load(String path) throws IOException {
        InputStream in = Jams.class.getResourceAsStream(path);
        if (in == null) throw new IOException("Couldn't load resource " + path + "!");
        Font.loadFont(in, 12);
        in.close();
    }

}

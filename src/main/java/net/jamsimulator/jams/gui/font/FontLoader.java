package net.jamsimulator.jams.gui.font;

import javafx.scene.text.Font;
import net.jamsimulator.jams.Jams;

import java.io.IOException;
import java.io.InputStream;

public class FontLoader {

	public static final String JETBRAINS_MONO = "JetBrains Mono";

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
		Font.loadFont(in, 12);
		in.close();
	}

}

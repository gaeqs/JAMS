module JAMS.main {
	requires java.desktop;
	requires javafx.controls;
	requires org.fxmisc.richtext;
	requires reactfx;
	requires flowless;
	requires org.json;
	requires FX.BorderlessScene;

	exports net.jamsimulator.jams;
	exports net.jamsimulator.jams.gui;

	opens net.jamsimulator.jams to javafx.graphics;
	opens net.jamsimulator.jams.gui to javafx.graphics;
}
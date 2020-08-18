module JAMS {

	requires java.base;

	requires javafx.fxml;
	requires javafx.base;
	requires javafx.graphics;
	requires javafx.media;
	requires javafx.controls;

	requires java.management;
	requires java.instrument;
	requires java.desktop;

	//requires  FX.BorderlessScene;
	requires richtextfx.fat;
	requires org.json;

	exports net.jamsimulator.jams;
}
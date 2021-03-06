package net.jamsimulator.jams.gui.util.log;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.fxmisc.flowless.ScaledVirtualized;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;

import java.util.Collections;

public class SimpleLog extends HBox implements Log {

	protected final CodeArea display;

	public SimpleLog() {
		super();

		VBox buttons = new VBox();
		Button clear = new Button("C");
		clear.setOnAction(event -> clear());
		clear.getStyleClass().add("bold-button");
		buttons.getChildren().add(clear);
		getChildren().add(buttons);

		display = new CodeArea();
		VirtualizedScrollPane<ScaledVirtualized<CodeArea>> scroll = new VirtualizedScrollPane<>(new ScaledVirtualized<>(display));
		display.setEditable(false);
		applyZoomListener(scroll);
		display.getStyleClass().add("display");
		getChildren().add(scroll);

		display.prefWidthProperty().bind(widthProperty().subtract(buttons.widthProperty()));
	}

	@Override
	public void print(Object object) {
		Platform.runLater(() -> display.appendText(object == null ? "null" : object.toString()));
	}

	@Override
	public void println(Object object) {
		Platform.runLater(() -> display.appendText((object == null ? "null" : object.toString()) + '\n'));
	}

	@Override
	public void printError(Object object) {
		printAndStyle(object, "log_error");
	}

	@Override
	public void printErrorLn(Object object) {
		printAndStyleLn(object, "log_error");
	}

	@Override
	public void printInfo(Object object) {
		printAndStyle(object, "log_info");
	}

	@Override
	public void printInfoLn(Object object) {
		printAndStyleLn(object, "log_info");
	}

	@Override
	public void printWarning(Object object) {
		printAndStyle(object, "log_warning");
	}

	@Override
	public void printWarningLn(Object object) {
		printAndStyleLn(object, "log_warning");
	}

	@Override
	public void printDone(Object object) {
		printAndStyle(object, "log_done");
	}

	@Override
	public void printDoneLn(Object object) {
		printAndStyleLn(object, "log_done");
	}

	@Override
	public void println() {
		println("");
	}

	@Override
	public void clear() {
		Platform.runLater(display::clear);
	}

	private void printAndStyle(Object object, String style) {
		Platform.runLater(() -> {
			int from = display.getLength();
			display.appendText(object == null ? "null" : object.toString());
			display.setStyle(from, display.getLength(), Collections.singleton(style));
		});
	}

	private void printAndStyleLn(Object object, String style) {
		Platform.runLater(() -> {
			int from = display.getLength();
			display.appendText((object == null ? "null" : object.toString()) + '\n');
			display.setStyle(from, display.getLength(), Collections.singleton(style));
		});
	}

	protected void applyZoomListener(VirtualizedScrollPane<ScaledVirtualized<CodeArea>> scroll) {
		display.addEventFilter(ScrollEvent.SCROLL, event -> {
			if (event.isControlDown()) {
				double current = scroll.getContent().getZoom().getX();
				if (event.getDeltaY() < 0) {
					if (current > 0.4) {
						scroll.getContent().getZoom().setX(current - 0.2);
						scroll.getContent().getZoom().setY(current - 0.2);
					}
				} else if (event.getDeltaY() > 0) {
					scroll.getContent().getZoom().setX(current + 0.2);
					scroll.getContent().getZoom().setY(current + 0.2);
				}
				event.consume();
			}
		});

		//RESET
		display.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
			if (event.isControlDown() && event.getButton() == MouseButton.MIDDLE) {
				scroll.getContent().getZoom().setX(1);
				scroll.getContent().getZoom().setY(1);
				scroll.getContent().getZoom().setZ(1);
			}
		});
	}
}

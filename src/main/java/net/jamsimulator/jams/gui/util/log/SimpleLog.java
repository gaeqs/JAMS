package net.jamsimulator.jams.gui.util.log;

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

		VirtualizedScrollPane<ScaledVirtualized<CodeArea>> scroll = new VirtualizedScrollPane<>(new ScaledVirtualized<>(new CodeArea()));
		getChildren().add(scroll);

		display = (CodeArea) scroll.getContent().getChildrenUnmodifiable().get(0);
		display.setEditable(false);
		applyZoomListener(scroll);

		display.prefWidthProperty().bind(widthProperty().subtract(buttons.widthProperty()));
	}

	@Override
	public void print(Object object) {
		display.appendText(object == null ? "null" : object.toString());
	}

	@Override
	public void println(Object object) {
		display.appendText((object == null ? "null" : object.toString()) + '\n');
	}

	@Override
	public void printError(Object object) {
		int length = display.getLength();
		print(object);
		display.setStyle(length, display.getLength(), Collections.singleton("log_error"));
	}

	@Override
	public void printErrorLn(Object object) {
		int length = display.getLength();
		println(object);
		display.setStyle(length, display.getLength(), Collections.singleton("log_error"));
	}

	@Override
	public void printInfo(Object object) {
		int length = display.getLength();
		print(object);
		display.setStyle(length, display.getLength(), Collections.singleton("log_info"));
	}

	@Override
	public void printInfoLn(Object object) {
		int length = display.getLength();
		println(object);
		display.setStyle(length, display.getLength(), Collections.singleton("log_info"));
	}

	@Override
	public void printWarning(Object object) {
		int length = display.getLength();
		print(object);
		display.setStyle(length, display.getLength(), Collections.singleton("log_warning"));
	}

	@Override
	public void printWarningLn(Object object) {
		int length = display.getLength();
		println(object);
		display.setStyle(length, display.getLength(), Collections.singleton("log_warning"));
	}

	@Override
	public void printDone(Object object) {
		int length = display.getLength();
		print(object);
		display.setStyle(length, display.getLength(), Collections.singleton("log_done"));
	}

	@Override
	public void printDoneLn(Object object) {
		int length = display.getLength();
		println(object);
		display.setStyle(length, display.getLength(), Collections.singleton("log_done"));
	}

	@Override
	public void println() {
		display.appendText("\n");
	}

	@Override
	public void clear() {
		display.clear();
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

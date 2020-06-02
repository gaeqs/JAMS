package net.jamsimulator.jams.gui.util;

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

public class Log extends HBox {

	protected final CodeArea codeArea;

	public Log() {
		super();

		VBox buttons = new VBox();
		Button clear = new Button("C");
		clear.setOnAction(event -> clear());
		clear.getStyleClass().add("bold-button");
		buttons.getChildren().add(clear);

		getChildren().add(buttons);

		VirtualizedScrollPane<ScaledVirtualized<CodeArea>> scroll = new VirtualizedScrollPane<>(new ScaledVirtualized<>(new CodeArea()));
		getChildren().add(scroll);

		codeArea = (CodeArea) scroll.getContent().getChildrenUnmodifiable().get(0);
		codeArea.setEditable(false);
		applyZoomListener(scroll);

		codeArea.prefWidthProperty().bind(widthProperty().subtract(buttons.widthProperty()));
	}

	public void print(Object object) {
		codeArea.appendText(object == null ? "null" : object.toString());
	}

	public void println(Object object) {
		codeArea.appendText((object == null ? "null" : object.toString()) + '\n');
	}

	public void printError(Object object) {
		int length = codeArea.getLength();
		print(object);
		codeArea.setStyle(length, codeArea.getLength(), Collections.singleton("log_error"));
	}

	public void printErrorLn(Object object) {
		int length = codeArea.getLength();
		println(object);
		codeArea.setStyle(length, codeArea.getLength(), Collections.singleton("log_error"));
	}

	public void printInfo(Object object) {
		int length = codeArea.getLength();
		print(object);
		codeArea.setStyle(length, codeArea.getLength(), Collections.singleton("log_info"));
	}

	public void printInfoLn(Object object) {
		int length = codeArea.getLength();
		println(object);
		codeArea.setStyle(length, codeArea.getLength(), Collections.singleton("log_info"));
	}


	public void printWarning(Object object) {
		int length = codeArea.getLength();
		print(object);
		codeArea.setStyle(length, codeArea.getLength(), Collections.singleton("log_warning"));
	}

	public void printWarningLn(Object object) {
		int length = codeArea.getLength();
		println(object);
		codeArea.setStyle(length, codeArea.getLength(), Collections.singleton("log_warning"));
	}


	public void printDone(Object object) {
		int length = codeArea.getLength();
		print(object);
		codeArea.setStyle(length, codeArea.getLength(), Collections.singleton("log_done"));
	}

	public void printDoneLn(Object object) {
		int length = codeArea.getLength();
		println(object);
		codeArea.setStyle(length, codeArea.getLength(), Collections.singleton("log_done"));
	}

	public void println() {
		codeArea.appendText("\n");
	}

	public void clear() {
		codeArea.clear();
	}

	protected void applyZoomListener(VirtualizedScrollPane<ScaledVirtualized<CodeArea>> scroll) {
		codeArea.addEventFilter(ScrollEvent.SCROLL, event -> {
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
		codeArea.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
			if (event.isControlDown() && event.getButton() == MouseButton.MIDDLE) {
				scroll.getContent().getZoom().setX(1);
				scroll.getContent().getZoom().setY(1);
				scroll.getContent().getZoom().setZ(1);
			}
		});
	}
}

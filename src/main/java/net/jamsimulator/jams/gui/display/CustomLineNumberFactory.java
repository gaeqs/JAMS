package net.jamsimulator.jams.gui.display;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import org.fxmisc.richtext.GenericStyledArea;
import org.reactfx.collection.LiveList;
import org.reactfx.value.Val;

import java.util.function.IntFunction;

public class CustomLineNumberFactory implements IntFunction<Node> {

	private static final Insets DEFAULT_INSETS = new Insets(0.0, 5.0, 0.0, 5.0);

	public static CustomLineNumberFactory
	get(GenericStyledArea<?, ?, ?> area) {
		return get(area, digits -> "%1$" + digits + "s");
	}

	public static CustomLineNumberFactory get(GenericStyledArea<?, ?, ?> area, IntFunction<String> format) {
		return new CustomLineNumberFactory(area, format);
	}

	private final Val<Integer> nParagraphs;
	private final IntFunction<String> format;


	private final Rectangle background;


	public CustomLineNumberFactory(
			GenericStyledArea<?, ?, ?> area,
			IntFunction<String> format) {
		nParagraphs = LiveList.sizeOf(area.getParagraphs());
		this.format = format;

		background = new Rectangle(0, 100000);
		background.getStyleClass().add("left-bar-background");
	}

	public Rectangle getBackground() {
		return background;
	}

	@Override
	public Node apply(int idx) {
		Val<String> formatted = nParagraphs.map(n -> format(idx + 1, n));

		Label lineNo = new Label();
		lineNo.setPadding(DEFAULT_INSETS);
		lineNo.setAlignment(Pos.BOTTOM_RIGHT);
		lineNo.getStyleClass().add("line-number");

		// bind label's text to a Val that stops observing area's paragraphs
		// when lineNo is removed from scene
		lineNo.textProperty().bind(formatted.conditionOnShowing(lineNo));


		HBox hBox = new HBox(lineNo);
		hBox.getStyleClass().add("left-bar");

		if (idx >= 0 && idx < 9) {
			background.widthProperty().bind(hBox.widthProperty());
		}

		return hBox;
	}

	private String format(int x, int max) {
		int digits = (int) Math.floor(Math.log10(max)) + 1;
		return String.format(format.apply(digits), x);
	}
}

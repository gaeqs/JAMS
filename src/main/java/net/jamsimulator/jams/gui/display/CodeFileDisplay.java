package net.jamsimulator.jams.gui.display;


import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.theme.event.SelectedThemeChangeEvent;
import net.jamsimulator.jams.utils.FileUtils;
import org.fxmisc.richtext.CodeArea;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeFileDisplay extends CodeArea implements FileDisplay {

	private final FileDisplayTab tab;
	private String old;

	public CodeFileDisplay(FileDisplayTab tab) {
		super(read(tab));
		this.tab = tab;

		CustomLineNumberFactory factory = CustomLineNumberFactory.get(this);
		getChildren().add(0, factory.getBackground());

		JamsApplication.getThemeManager().getSelected().apply(this);
		JamsApplication.getThemeManager().registerListeners(this);

		setParagraphGraphicFactory(factory);
		applyOldTextListener();
		applyAutoIndent();
		applyIndentRemover();
	}

	public FileDisplayTab getTab() {
		return tab;
	}

	public void onClose() {
		JamsApplication.getThemeManager().unregisterListeners(this);
	}

	private void applyOldTextListener() {
		textProperty().addListener((obs, old, value) -> {
			this.old = old;
		});
	}

	private void applyAutoIndent() {
		Pattern whiteSpace = Pattern.compile("^\\s+");
		addEventHandler(KeyEvent.KEY_PRESSED, event -> {
			if (event.getCode() == KeyCode.ENTER) {
				int caretPosition = getCaretPosition();
				int currentParagraph = getCurrentParagraph();
				Matcher m0 = whiteSpace.matcher(getParagraph(currentParagraph - 1).getSegments().get(0));
				if (m0.find()) Platform.runLater(() -> insertText(caretPosition, m0.group()));
			}
		});
	}

	private void applyIndentRemover() {

		addEventHandler(KeyEvent.KEY_PRESSED, event -> {
			if (event.getCode() == KeyCode.BACK_SPACE) {

				int caretPosition = getCaretPosition();
				System.out.println(caretPosition);

				//If shift is pressed, then just execute a normal backspace.
				if (event.isShiftDown()) {
					return;
				}

				int currentParagraph = getCurrentParagraph();
				Position position = offsetToPosition(caretPosition, Bias.Forward);

				String s = old.substring(caretPosition - position.getMinor(), caretPosition + 1);
				if (s.trim().isEmpty()) {
					int to = caretPosition - position.getMinor() - 1;
					if (to < 0) to = 0;

					boolean lastParagraphEmpty = currentParagraph != 0 && getParagraph(currentParagraph - 1).getText().isEmpty();

					replaceText(to, caretPosition, lastParagraphEmpty ? s : "");
				}
			}
		});
	}

	private static String read(FileDisplayTab tab) {
		try {
			return FileUtils.readAll(tab.getFile());
		} catch (IOException ex) {
			StringWriter writer = new StringWriter();
			ex.printStackTrace(new PrintWriter(writer));
			return writer.toString();
		}
	}

	@Listener
	public void onThemeChange(SelectedThemeChangeEvent.After event) {
		event.getNewTheme().apply(this);
	}

}

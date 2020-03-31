package net.jamsimulator.jams.gui.display;


import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.utils.FileUtils;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.SimpleEditableStyledDocument;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileDisplay extends CodeArea {

	private final FileDisplayTab tab;

	public FileDisplay(FileDisplayTab tab) {
		super(new SimpleEditableStyledDocument<>(Collections.emptyList(), Collections.emptyList()));
		this.tab = tab;
		JamsApplication.getThemeManager().getSelected().apply(this);
		setParagraphGraphicFactory(LineNumberFactory.get(this));
		applyAutoIndent();
	}

	public FileDisplayTab getTab() {
		return tab;
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

	private void refreshLineNumberFactoryClass () {
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

}

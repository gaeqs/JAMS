package net.jamsimulator.jams.gui.display.image;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import net.jamsimulator.jams.gui.display.FileDisplay;
import net.jamsimulator.jams.gui.display.FileDisplayTab;

import java.net.MalformedURLException;

public class ImageFileDisplay extends ImageView implements FileDisplay {

	private final FileDisplayTab tab;

	public ImageFileDisplay(FileDisplayTab tab) throws MalformedURLException, IllegalArgumentException {
		super(new Image(tab.getFile().toURI().toURL().toString()));
		this.tab = tab;
	}

	@Override
	public FileDisplayTab getTab() {
		return tab;
	}

	@Override
	public void onClose() {
	}
}

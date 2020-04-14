package net.jamsimulator.jams.gui.display.image;

import javafx.scene.image.Image;
import net.jamsimulator.jams.gui.display.FileDisplay;
import net.jamsimulator.jams.gui.display.FileDisplayTab;
import net.jamsimulator.jams.gui.image.NearestImageView;

import java.net.MalformedURLException;

public class ImageFileDisplay extends NearestImageView implements FileDisplay {

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

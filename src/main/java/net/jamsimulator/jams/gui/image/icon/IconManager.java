/*
 * MIT License
 *
 * Copyright (c) 2020 Gael Rial Costas
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.jamsimulator.jams.gui.image.icon;

import javafx.scene.image.Image;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.utils.Validate;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * This manager is used to easily manage JAMS's icons.
 * For default icon names, see {@link Icons}.
 *
 * @see Icons
 */
public class IconManager {

	public static final int SIZE = 40;

	public static IconManager INSTANCE = new IconManager();

	private final Map<String, Image> icons;

	private IconManager() {
		icons = new HashMap<>();
	}

	/**
	 * Returns the icon that matches the given name, if present.
	 *
	 * @param data the given data.
	 * @return the icon, if present.
	 */
	public Optional<Image> getIcon(IconData data) {
		return Optional.ofNullable(icons.get(data.getName()));
	}

	/**
	 * Loads a icon and registers it into the manager.
	 * <p>
	 * If another icon with the same name already exists within the manager and the replace boolean is
	 * false, the icon won't be loaded and added.
	 * <p>
	 * If the load fails, this method will throw the exception thrown by the image loader.
	 * <p>
	 * The image will be scaled using the given width and height.
	 *
	 * @param data    the data of the icon.
	 * @param replace whether the method should replace the already existing icon, if present.
	 * @param width   the width of the image.
	 * @param height  the height of the image.
	 * @return whether the image has been registered.
	 * @throws Exception any exception thrown by the image loader.
	 * @see Image#getException()
	 */
	public boolean register(IconData data, boolean replace, int width, int height) throws Exception {
		Validate.notNull(data.getName(), "Name cannot be null!");
		Validate.notNull(data.getUrl(), "Path cannot be null!");
		if (icons.containsKey(data.getName()) && !replace) return false;
		Image image = new Image(data.getUrl(), width, height, false, true);
		if (image.isError()) throw image.getException();
		icons.put(data.getName(), image);
		return true;
	}

	/**
	 * Registers the given image into the manager.
	 * <p>
	 * If another icon with the same name already exists within the manager and the replace boolean is
	 * false, the icon won't be  added.
	 *
	 * @param name    the name of the icon.
	 * @param image   the image representing the icon.
	 * @param replace whether the method should replace the already existing icon, if present.
	 * @return whether the image has been registered.
	 */
	public boolean register(String name, Image image, boolean replace) {
		Validate.notNull(name, "Name cannot be null!");
		Validate.notNull(image, "Image cannot be null!");
		if (icons.containsKey(name) && !replace) return false;
		icons.put(name, image);
		return true;
	}

	/**
	 * Calls the method {@link #register(IconData, boolean, int, int)}. If it throws an exception
	 * am error message will be sent and this method will return false.
	 * <p>
	 * If no exceptions are thrown the method will return the output of {@link #register(IconData, boolean, int, int)}.
	 *
	 * @param data    the data of the icon.
	 * @param replace whether the method should replace the already existing icon, if present.
	 * @param width   the width of the image.
	 * @param height  the height of the image.
	 * @return false if an exception was thrown or the output of {@link #register(IconData, boolean, int, int)}
	 */
	public boolean registerSafe(IconData data, boolean replace, int width, int height) {
		try {
			return register(data, replace, width, height);
		} catch (Exception ex) {
			System.err.println("Error while loading an icon " + ex.getMessage());
			return false;
		}
	}

	/**
	 * Returns the icon that matches the given name.
	 * If no icon is found, the method will load it using the given path and parameters.
	 * <p>
	 * If the load fails, this method will throw the exception thrown by the image loader.
	 *
	 * @param data the data of the icon.
	 * @return the icon.
	 * @throws Exception any exception thrown by the image loader.
	 */
	public Image getOrLoad(IconData data) throws Exception {
		Validate.notNull(data, "Name cannot be null!");
		Optional<Image> icon = getIcon(data);
		if (icon.isPresent()) return icon.get();

		InputStream stream = Jams.class.getResourceAsStream(data.getUrl());
		Image image = new Image(stream, SIZE, SIZE, false, false);

		if (image.isError()) throw image.getException();
		icons.put(data.getName(), image);

		return image;
	}

	/**
	 * Calls the method {@link #getOrLoad(IconData)} . If it throws an exception
	 * am error message will be sent and this method will return an empty {@link Optional}.
	 * <p>
	 * If no exceptions are thrown the method will return the {@link Optional} wrapping the icon.
	 *
	 * @param data the data of the icon.
	 * @return the icon, or empty if an exception was thrown.
	 */
	public Optional<Image> getOrLoadSafe(IconData data) {
		try {
			return Optional.of(getOrLoad(data));
		} catch (Exception ex) {
			System.err.println("Error while loading an icon (" + data + "): " + ex.getMessage());
			ex.printStackTrace();
			return Optional.empty();
		}
	}

}

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

package net.jamsimulator.jams.gui.image;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Represents an {@link ImageView} that doesn't smooth the image inside..
 */
public class NearestImageView extends ImageView {

	/**
	 * Creates the nearest image view.
	 */
	public NearestImageView() {
		setSmooth(false);
	}

	/**
	 * Creates the nearest image view.
	 *
	 * @param url the image's URL.
	 */
	public NearestImageView(String url) {
		super(url);
		setSmooth(false);
	}

	/**
	 * Creates the nearest image view.
	 *
	 * @param image the image.
	 */
	public NearestImageView(Image image) {
		super(image);
		setSmooth(false);
	}

}

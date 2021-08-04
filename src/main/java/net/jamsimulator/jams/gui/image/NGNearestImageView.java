/*
 *  MIT License
 *
 *  Copyright (c) 2021 Gael Rial Costas
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.jamsimulator.jams.gui.image;

import com.sun.javafx.sg.prism.NGImageView;
import com.sun.prism.Graphics;
import com.sun.prism.Image;
import com.sun.prism.ResourceFactory;
import com.sun.prism.Texture;
import com.sun.prism.image.Coords;

import java.lang.reflect.Field;


public class NGNearestImageView extends NGImageView {

    private final Field imageField, coordsField, xField, yField, wField, hField;
    private boolean logging = false;

    public NGNearestImageView() {
        super();
        try {
            imageField = NGImageView.class.getDeclaredField("image");
            coordsField = NGImageView.class.getDeclaredField("coords");
            xField = NGImageView.class.getDeclaredField("x");
            yField = NGImageView.class.getDeclaredField("y");
            wField = NGImageView.class.getDeclaredField("w");
            hField = NGImageView.class.getDeclaredField("h");

            imageField.setAccessible(true);
            coordsField.setAccessible(true);
            xField.setAccessible(true);
            yField.setAccessible(true);
            wField.setAccessible(true);
            hField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public void setLogging(boolean logging) {
        this.logging = logging;
    }

    @Override
    protected void renderContent(Graphics g) {
        try {
            var image = (Image) imageField.get(this);
            var coords = (Coords) coordsField.get(this);

            var x = xField.getFloat(this);
            var y = yField.getFloat(this);
            var w = wField.getFloat(this);
            var h = hField.getFloat(this);

            int imgW = image.getWidth();
            int imgH = image.getHeight();

            ResourceFactory factory = g.getResourceFactory();
            int maxSize = factory.getMaximumTextureSize();

            if (imgW <= maxSize && imgH <= maxSize) {
                Texture texture = getBestTexture(factory, image, imgW, imgH, w, h);
                if (coords == null) {
                    g.drawTexture(texture, x, y, x + w, y + h, 0, 0, imgW, imgH);
                } else {
                    coords.draw(texture, g, x, y);
                }
                texture.unlock();
            } else {
                super.renderContent(g);
            }
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        }
    }

    protected Texture getBestTexture(ResourceFactory factory, Image image,
                                     float imageWidth, float imageHeight, float width, float height) {
        Texture tex;
        if (width >= imageWidth || height > imageHeight) {
            // Use normal mode
            tex =  factory.getCachedTexture(image, Texture.WrapMode.CLAMP_TO_EDGE);
            tex.setLinearFiltering(false);
        } else {
            // Use mipmaps!
            tex = factory.getCachedTexture(image, Texture.WrapMode.REPEAT, true);
            tex.setLinearFiltering(true);
        }
        return tex;
    }
}

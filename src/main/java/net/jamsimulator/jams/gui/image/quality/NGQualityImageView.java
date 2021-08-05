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

package net.jamsimulator.jams.gui.image.quality;

import com.sun.javafx.sg.prism.NGImageView;
import com.sun.prism.Graphics;
import com.sun.prism.Image;
import com.sun.prism.ResourceFactory;
import com.sun.prism.Texture;
import com.sun.prism.image.Coords;
import javafx.scene.image.PixelFormat;

import java.lang.reflect.Field;
import java.nio.IntBuffer;


public class NGQualityImageView extends NGImageView {

    private final Field imageField, coordsField, xField, yField, wField, hField;

    private Texture currentTexture = null;
    private Image representedImage = null;
    private int textureWidth = -1, textureHeight = -1;

    public NGQualityImageView() {
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
                Texture texture = getBestTexture(factory, image, imgW, imgH, (int) w, (int) h);
                texture.lock();
                if (coords == null) {
                    g.drawTexture(texture, x, y, x + w, y + h, 0, 0, w, h);
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
                                     int imageWidth, int imageHeight, int width, int height) {

        if (width == textureWidth && height == textureHeight && representedImage == image) {
            // Nothing to do!
            return currentTexture;
        }

        if ((width != textureWidth || height != textureHeight) && currentTexture != null) {
            currentTexture.contentsNotUseful();
            currentTexture.dispose();
            currentTexture = null;
        }

        if (currentTexture == null) {
            currentTexture = factory.createTexture(com.sun.prism.PixelFormat.INT_ARGB_PRE,
                    Texture.Usage.DYNAMIC, Texture.WrapMode.CLAMP_TO_EDGE, width, height);
            currentTexture.contentsUseful();
            currentTexture.setLinearFiltering(false);

            textureWidth = width;
            textureHeight = height;
        } else {
            currentTexture.lock();
        }

        // Use mipmaps!

        var buffer = IntBuffer.allocate(imageWidth * imageHeight);
        image.getPixels(0, 0, imageWidth, imageHeight,
                PixelFormat.getIntArgbPreInstance(), buffer, imageWidth);

        var result = FastSincResampler.resample(buffer, imageWidth, imageHeight, width, height, 10);
        currentTexture.update(result, com.sun.prism.PixelFormat.INT_ARGB_PRE,
                0, 0, 0, 0, width, height, width * 4, false);

        representedImage = image;

        currentTexture.unlock();

        // Use resampling
        return currentTexture;
    }
}

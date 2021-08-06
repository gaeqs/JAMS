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

import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.tk.Toolkit;
import com.sun.prism.*;
import net.jamsimulator.jams.gui.image.icon.CachedTexture;
import net.jamsimulator.jams.gui.image.icon.IconData;

import java.nio.IntBuffer;


public class NGQualityImageView extends NGNode {

    protected IconData icon;

    protected float x, y, w, h;

    private Texture currentTexture = null;
    private CachedTexture currentCachedTexture;
    private IconData representedIcon = null;
    private int textureWidth = -1, textureHeight = -1;

    public void setIcon(Object i) {
        var newIcon = (IconData) i;
        if (icon == newIcon) return;

        icon = newIcon;
        geometryChanged();
    }

    public void setX(float x) {
        if (this.x != x) {
            this.x = x;
            geometryChanged();
        }
    }

    public void setY(float y) {
        if (this.y != y) {
            this.y = y;
            geometryChanged();
        }
    }

    public void setDimensions(float cw, float ch) {
        w = cw;
        h = ch;
    }


    @Override
    protected void doRender(Graphics g) {
        if (icon != null && icon.hasImage()) {
            super.doRender(g);
        }
    }


    @Override
    protected void renderContent(Graphics g) {
        if (w < 1 || h < 1) return;
        var cached = getBestTexture(g.getResourceFactory(), (int) w, (int) h);
        if (cached == null) return;
        currentTexture.lock();
        g.drawTexture(currentTexture, x, y, x + w, y + h, 0, 0, cached.width(), cached.height());
        currentTexture.unlock();
    }

    protected CachedTexture getBestTexture(ResourceFactory factory, int width, int height) {
        if (width == textureWidth && height == textureHeight && representedIcon == icon) {
            // Nothing to do!
            return currentCachedTexture;
        }

        if (currentTexture != null) {
            currentTexture.contentsNotUseful();
            currentTexture.dispose();
            currentTexture = null;
        }

        var cached = icon.getTexture(width, height).orElse(null);
        currentCachedTexture = cached;

        if (cached == null) {
            currentTexture = factory.createTexture(PixelFormat.INT_ARGB_PRE,
                    Texture.Usage.DYNAMIC, Texture.WrapMode.CLAMP_TO_EDGE, width, height);
        } else {
            currentTexture = factory.createTexture(PixelFormat.INT_ARGB_PRE,
                    Texture.Usage.DYNAMIC, Texture.WrapMode.CLAMP_TO_EDGE, cached.width(), cached.height());
        }

        currentTexture.contentsUseful();

        currentTexture.setLinearFiltering(false);
        textureWidth = width;
        textureHeight = height;


        if (cached == null) return null;

        currentTexture.update(IntBuffer.wrap(cached.buffer()), PixelFormat.INT_ARGB_PRE,
                0, 0, 0, 0, cached.width(), cached.height(),
                cached.width() * 4, false);

        representedIcon = icon;
        currentTexture.unlock();

        return currentCachedTexture;
    }

    @Override
    protected boolean hasOverlappingContents() {
        return false;
    }

    @Override
    protected boolean supportsOpaqueRegions() {
        return true;
    }

    @Override
    protected boolean hasOpaqueRegion() {
        if (!super.hasOpaqueRegion() || w < 1 || h < 1 || icon == null) return false;
        var op = icon.getImage();
        if (op.isEmpty()) return false;
        var image = (Image) Toolkit.getImageAccessor().getPlatformImage(op.get());
        return image.isOpaque();
    }

    @Override
    protected RectBounds computeOpaqueRegion(RectBounds opaqueRegion) {
        return (RectBounds) opaqueRegion.deriveWithNewBounds(x, y, 0, x + w, y + h, 0);
    }
}

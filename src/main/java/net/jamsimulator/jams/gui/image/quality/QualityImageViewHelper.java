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

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.util.Utils;
import javafx.scene.Node;

public class QualityImageViewHelper extends NodeHelper {

    private static final QualityImageViewHelper theInstance;
    private static ImageViewAccessor imageViewAccessor;

    static {
        theInstance = new QualityImageViewHelper();
        Utils.forceInit(QualityImageViewHelper.class);
    }

    private static QualityImageViewHelper getInstance() {
        return theInstance;
    }

    public static void initHelper(QualityImageView imageView) {
        setHelper(imageView, getInstance());
    }

    @Override
    protected NGNode createPeerImpl(Node node) {
        return imageViewAccessor.doCreatePeer(node);
    }

    @Override
    protected void updatePeerImpl(Node node) {
        super.updatePeerImpl(node);
        imageViewAccessor.doUpdatePeer(node);
    }

    @Override
    protected BaseBounds computeGeomBoundsImpl(Node node, BaseBounds bounds,
                                               BaseTransform tx) {
        return imageViewAccessor.doComputeGeomBounds(node, bounds, tx);
    }

    @Override
    protected boolean computeContainsImpl(Node node, double localX, double localY) {
        return imageViewAccessor.doComputeContains(node, localX, localY);
    }

    public static void setImageViewAccessor(final ImageViewAccessor newAccessor) {
        if (imageViewAccessor != null) {
            throw new IllegalStateException();
        }

        imageViewAccessor = newAccessor;
    }

    public interface ImageViewAccessor {
        NGNode doCreatePeer(Node node);

        void doUpdatePeer(Node node);

        BaseBounds doComputeGeomBounds(Node node, BaseBounds bounds, BaseTransform tx);

        boolean doComputeContains(Node node, double localX, double localY);
    }
}

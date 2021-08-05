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

package net.jamsimulator.jams.gui.image.nearest;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.sg.prism.NGNode;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.lang.reflect.InvocationTargetException;

/**
 * Represents an {@link ImageView} that doesn't smooth the image inside.
 */
public class NearestImageView extends ImageView {

    static {
        NearestImageViewHelper.setImageViewAccessor(new NearestImageViewHelper.ImageViewAccessor() {
            @Override
            public NGNode doCreatePeer(Node node) {
                return new NGNearestImageView();
            }

            @Override
            public void doUpdatePeer(Node node) {
                try {
                    var method = ImageView.class.getDeclaredMethod("doUpdatePeer");
                    method.setAccessible(true);
                    method.invoke(node);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public BaseBounds doComputeGeomBounds(Node node, BaseBounds bounds, BaseTransform tx) {
                try {
                    var method = ImageView.class.getDeclaredMethod(
                            "doComputeGeomBounds", BaseBounds.class, BaseTransform.class);
                    method.setAccessible(true);
                    return (BaseBounds) method.invoke(node, bounds, tx);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public boolean doComputeContains(Node node, double localX, double localY) {
                try {
                    var method = ImageView.class.getDeclaredMethod(
                            "doComputeContains", double.class, double.class);
                    method.setAccessible(true);
                    return (boolean) method.invoke(node, localX, localY);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
    }

    {
        NearestImageViewHelper.initHelper(this);
    }

    /**
     * Creates the nearest image view.
     */
    public NearestImageView() {
    }

    /**
     * Creates the nearest image view.
     *
     * @param url the image's URL.
     */
    public NearestImageView(String url) {
        super(url);
    }

    /**
     * Creates the nearest image view.
     *
     * @param image the image.
     */
    public NearestImageView(Image image) {
        super(image);
    }

    /**
     * Creates the nearest image view.
     *
     * @param image the image.
     */
    public NearestImageView(Image image, double width, double height) {
        super(image);
        setFitWidth(width);
        setFitHeight(height);
    }
}

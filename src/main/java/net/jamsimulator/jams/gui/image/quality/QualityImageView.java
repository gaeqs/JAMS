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
import com.sun.javafx.scene.DirtyBits;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.sg.prism.NGNode;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.FloatPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.geometry.NodeOrientation;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import net.jamsimulator.jams.gui.image.icon.IconData;

/**
 * The quality image view is a fully reworked implementation of the JavaFX's
 * {@link javax.swing.text.html.ImageView Image View} designed to show downscaled images
 * with the best quality possible.
 * <p>
 * The downscale process uses a parallel implementation of the sinc resampler,
 * a computationally expensive but easily parallelizable algorithm.
 * This implementation uses an {@link java.util.concurrent.Executor Executor} with the
 * same amount of threads as the amount of processors available to the JVM.
 * <p>
 * In the case the image is upscaled, this implementation uses the nearest filtering approach.
 * <p>
 * Because this image view modifies de size of the texture and not the other way around,
 * a width and height must be provided. Use the parameters width and height in the construction or
 * the method {@link #setFitWidth(float)} and {@link #setFitHeight(float)} to set them.
 */
public class QualityImageView extends Node {

    private static final String DEFAULT_STYLE_CLASS = "image-view";

    static {
        QualityImageViewHelper.setImageViewAccessor(new QualityImageViewHelper.ImageViewAccessor() {
            @Override
            public NGNode doCreatePeer(Node node) {
                return ((QualityImageView) node).doCreatePeer();
            }

            @Override
            public void doUpdatePeer(Node node) {
                ((QualityImageView) node).doUpdatePeer();
            }

            @Override
            public BaseBounds doComputeGeomBounds(Node node, BaseBounds bounds, BaseTransform tx) {
                return ((QualityImageView) node).doComputeBounds(bounds, tx);
            }

            @Override
            public boolean doComputeContains(Node node, double localX, double localY) {
                return ((QualityImageView) node).doComputeContains();
            }
        });
    }

    {
        QualityImageViewHelper.initHelper(this);
    }

    /**
     * The icon property, lazy-initialized.
     */
    protected ObjectProperty<IconData> icon;

    /**
     * The x and y properties, lazy-initialized.
     */
    protected FloatProperty x, y;

    /**
     * The dimension properties, lazy-initialized.
     */
    protected FloatProperty fitWidth, fitHeight;

    /**
     * Whether the calculated width and calculated height variables are valid in the current context.
     * Use {@link #recomputeWidthHeight()} to recalculate them.
     */
    protected boolean validCalculatedDimensions;

    /**
     * The width and height used by the peer node to draw the image.
     * This values may be outdated. Use {@link #recomputeWidthHeight()} to update them.
     */
    protected float calculatedWidth, calculatedHeight;


    /**
     * Creates the image view without an image.
     */
    public QualityImageView() {
        getStyleClass().add(DEFAULT_STYLE_CLASS);
        setAccessibleRole(AccessibleRole.IMAGE_VIEW);
        setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
    }


    /**
     * Creates an image view displaying the given icon.
     *
     * @param icon the icon to display.
     */
    public QualityImageView(IconData icon) {
        this();
        setIcon(icon);
    }

    /**
     * Creates an image view displaying the given icon.
     * Ths image view size is given by the parameters width and height.
     *
     * @param icon   the icon to display.
     * @param width  the width of the image view.
     * @param height the height of the image view.
     */
    public QualityImageView(IconData icon, float width, float height) {
        this(icon);
        setFitWidth(width);
        setFitHeight(height);
    }

    /**
     * Sets the icon this view will display.
     * <p>
     * If the icon is null, this view won't display anything.
     *
     * @param icon the icon to display or null.
     */
    public void setIcon(IconData icon) {
        iconProperty().set(icon);
    }

    /**
     * Returns the icon this view is currently displaying.
     * <p>
     * This value is null if this view is not displaying any icon.
     *
     * @return the icon or null.
     */
    public IconData getIcon() {
        return icon == null ? null : icon.get();
    }

    /**
     * The property that manages the icon value of this view.
     * <p>
     * If the property contains no value, this view is not displaying any icon.
     *
     * @return the property.
     */
    public ObjectProperty<IconData> iconProperty() {
        if (icon == null) {
            icon = new ObjectPropertyBase<>() {

                @Override
                protected void invalidated() {
                    NodeHelper.markDirty(QualityImageView.this, DirtyBits.NODE_CONTENTS);
                }

                @Override
                public Object getBean() {
                    return QualityImageView.this;
                }

                @Override
                public String getName() {
                    return "icon";
                }
            };
        }
        return icon;
    }

    /**
     * Sets the horizontal displacement of this image view.
     *
     * @param x the horizontal displacement.
     */
    public void setX(float x) {
        xProperty().set(x);
    }

    /**
     * Returns the horizontal displacement of this image view.
     *
     * @return the horizontal displacement.
     */
    public float getX() {
        return x == null ? 0.0f : x.get();
    }

    /**
     * Returns the property that manages the horizontal displacement of this image view.
     *
     * @return the property.
     */
    public FloatProperty xProperty() {
        if (x == null) {
            x = new FloatPropertyBase() {

                @Override
                protected void invalidated() {
                    NodeHelper.markDirty(QualityImageView.this, DirtyBits.NODE_GEOMETRY);
                    NodeHelper.geomChanged(QualityImageView.this);
                }

                @Override
                public Object getBean() {
                    return QualityImageView.this;
                }

                @Override
                public String getName() {
                    return "x";
                }
            };
        }
        return x;
    }

    /**
     * Sets the vertical displacement of this image view.
     *
     * @param y the vertical displacement.
     */
    public void setY(float y) {
        yProperty().set(y);
    }

    /**
     * Returns the vertical displacement of this image view.
     *
     * @return the vertical displacement.
     */
    public float getY() {
        return y == null ? 0.0f : y.get();
    }

    /**
     * Returns the property that manages the vertical displacement of this image view.
     *
     * @return the property.
     */
    public FloatProperty yProperty() {
        if (y == null) {
            y = new FloatPropertyBase() {

                @Override
                protected void invalidated() {
                    NodeHelper.markDirty(QualityImageView.this, DirtyBits.NODE_GEOMETRY);
                    NodeHelper.geomChanged(QualityImageView.this);
                }

                @Override
                public Object getBean() {
                    return QualityImageView.this;
                }

                @Override
                public String getName() {
                    return "y";
                }
            };
        }
        return y;
    }

    /**
     * Sets the width the icon displayed by this view will fit.
     * If this value is negative or zero, the image won't be displayed.
     * <p>
     * This value overrides the viewport width if positive.
     *
     * @param fitWidth the width.
     */
    public void setFitWidth(float fitWidth) {
        fitWidthProperty().set(fitWidth);
    }

    /**
     * Returns the width the icon displayed by this view fits.
     * If this value is negative or zero, the image won't be displayed.
     * <p>
     * This value overrides the viewport width if positive.
     *
     * @return the width.
     */
    public float getFitWidth() {
        return fitWidth == null ? 0.0f : fitWidth.get();
    }

    /**
     * Returns the property that manages the width the icon displayed by this view fits.
     * If this value is negative or zero, the image won't be displayed.
     * <p>
     * This value overrides the viewport width if positive.
     *
     * @return the property.
     */
    public FloatProperty fitWidthProperty() {
        if (fitWidth == null) {
            fitWidth = new FloatPropertyBase() {

                @Override
                protected void invalidated() {
                    invalidateWidthHeight();
                    NodeHelper.markDirty(QualityImageView.this, DirtyBits.NODE_VIEWPORT);
                    NodeHelper.geomChanged(QualityImageView.this);
                }

                @Override
                public Object getBean() {
                    return QualityImageView.this;
                }

                @Override
                public String getName() {
                    return "fitWidth";
                }
            };
        }
        return fitWidth;
    }

    /**
     * Sets the height the icon displayed by this view will fit.
     * If this value is negative or zero, the image won't be displayed.
     * <p>
     * This value overrides the viewport height if positive.
     *
     * @param fitHeight the height.
     */
    public void setFitHeight(float fitHeight) {
        fitHeightProperty().set(fitHeight);
    }

    /**
     * Returns the height the icon displayed by this view fits.
     * If this value is negative or zero, the image won't be displayed.
     * <p>
     * This value overrides the viewport height if positive.
     *
     * @return the height.
     */
    public float getFitHeight() {
        return fitHeight == null ? 0.0f : fitHeight.get();
    }

    /**
     * Returns the property that manages the height the icon displayed by this view fits.
     * If this value is negative or zero, the image won't be displayed.
     * <p>
     * This value overrides the viewport height if positive.
     *
     * @return the property.
     */
    public FloatProperty fitHeightProperty() {
        if (fitHeight == null) {
            fitHeight = new FloatPropertyBase() {

                @Override
                protected void invalidated() {
                    invalidateWidthHeight();
                    NodeHelper.markDirty(QualityImageView.this, DirtyBits.NODE_VIEWPORT);
                    NodeHelper.geomChanged(QualityImageView.this);
                }

                @Override
                public Object getBean() {
                    return QualityImageView.this;
                }

                @Override
                public String getName() {
                    return "fitHeight";
                }
            };
        }
        return fitHeight;
    }

    protected NGNode doCreatePeer() {
        return new NGQualityImageView();
    }

    protected void doUpdatePeer() {
        var peer = (NGQualityImageView) NodeHelper.getPeer(this);
        if (NodeHelper.isDirty(this, DirtyBits.NODE_GEOMETRY)) {
            peer.setX(getX());
            peer.setY(getY());
        }
        if (NodeHelper.isDirty(this, DirtyBits.NODE_CONTENTS)) {
            peer.setIcon(getIcon());
        }
        if (NodeHelper.isDirty(this, DirtyBits.NODE_VIEWPORT)
                || NodeHelper.isDirty(this, DirtyBits.NODE_CONTENTS)) {
            updateDimensions(peer);
        }
    }

    private void updateDimensions(NGQualityImageView peer) {
        recomputeWidthHeight();
        if (getIcon() == null || !getIcon().hasImage()) return;
        peer.setDimensions(calculatedWidth, calculatedHeight);
    }

    protected BaseBounds doComputeBounds(BaseBounds bounds, BaseTransform tx) {
        recomputeWidthHeight();
        bounds = bounds.deriveWithNewBounds(getX(), getY(), 0.0f,
                getX() + calculatedWidth, getY() + calculatedHeight, 0.0f);
        return tx.transform(bounds, bounds);
    }

    protected void invalidateWidthHeight() {
        validCalculatedDimensions = false;
    }

    protected void recomputeWidthHeight() {
        if (validCalculatedDimensions) return;
        calculatedWidth = Math.max(getFitWidth(), 0);
        calculatedHeight = Math.max(getFitHeight(), 0);
        validCalculatedDimensions = true;
    }

    protected boolean doComputeContains() {
        return getIcon() != null;
        // Local Note bounds contain test is already done by the caller.
        // (Node.contains()).
        // The image will always be streched. We have no way to check if the alpha is fully transparent.
    }
}

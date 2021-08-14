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

package net.jamsimulator.jams.gui.image.icon;

import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.gui.image.quality.FastSincResampler;
import net.jamsimulator.jams.plugin.Plugin;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Optional;

/**
 * Represents the data of an Icon.
 */
public class IconData {

    public static final int CACHE_SIZE = 10;

    private final String name, url;
    private final Class<?> holder;
    private final LinkedList<CachedTexture> textures;
    private Image image;
    private CachedTexture imageData;

    /**
     * Creates the data.
     *
     * @param name the name of the icon.
     * @param url  the url of the icon.
     */
    public IconData(String name, String url) {
        this.name = name;
        this.url = url;
        this.holder = Jams.class;
        this.textures = new LinkedList<>();
    }

    /**
     * Creates the data.
     *
     * @param name   the name of the icon.
     * @param url    the url of the icon.
     * @param holder the {@link Class} from where the icon will be loaded.
     */
    public IconData(String name, String url, Class<?> holder) {
        this.name = name;
        this.url = url;
        this.holder = holder;
        this.textures = new LinkedList<>();
    }

    /**
     * Creates the data.
     *
     * @param name   the name of the icon.
     * @param url    the url of the icon.
     * @param plugin the {@link Plugin} that owns the icon.
     */
    public IconData(String name, String url, Plugin plugin) {
        this.name = name;
        this.url = url;
        this.holder = plugin.getClass();
        this.textures = new LinkedList<>();
    }

    /**
     * Creates the data.
     *
     * @param name        the name of the icon.
     * @param inputStream the input stream the icon will be loaded from.
     */
    public IconData(String name, InputStream inputStream) {
        this.name = name;
        this.url = null;
        this.holder = Jams.class;
        this.textures = new LinkedList<>();
        initImage(inputStream);
    }

    /**
     * Creates the data.
     *
     * @param name        the name of the icon.
     * @param inputStream the input stream the icon will be loaded from.
     * @param holder      the {@link Class} from where the icon will be loaded.
     */
    public IconData(String name, InputStream inputStream, Class<?> holder) {
        this.name = name;
        this.url = null;
        this.holder = holder;
        this.textures = new LinkedList<>();
        initImage(inputStream);
    }

    /**
     * Creates the data.
     *
     * @param name        the name of the icon.
     * @param inputStream the input stream the icon will be loaded from.
     * @param plugin      the {@link Plugin} that owns the icon.
     */
    public IconData(String name, InputStream inputStream, Plugin plugin) {
        this.name = name;
        this.url = null;
        this.holder = plugin.getClass();
        this.textures = new LinkedList<>();
        initImage(inputStream);
    }

    /**
     * Returns the name of the icon.
     *
     * @return the name of the icon.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the url of the icon.
     *
     * @return the url of the icon.
     */
    public Optional<String> getUrl() {
        return Optional.ofNullable(url);
    }

    /**
     * Returns the {@link Class} from where the icon should be loaded.
     *
     * @return the {@link Class}.
     */
    public Class<?> getHolder() {
        return holder;
    }

    public boolean hasImage() {
        return image != null || getImage().isPresent();
    }

    public Optional<Image> getImage() {
        if (image == null) {
            InputStream stream = holder.getResourceAsStream(url);
            if (stream == null) {
                new NullPointerException("Stream is null.").printStackTrace();
                return Optional.empty();
            }
            initImage(stream);
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Optional.ofNullable(image);
    }

    public Optional<CachedTexture> getImageData() {
        if (imageData == null) getImage();
        return Optional.ofNullable(imageData);
    }

    public Optional<CachedTexture> getTexture(int width, int height) {
        if (width < 0 || height < 0) return Optional.empty();

        var imageData = getImageData();
        if (imageData.isEmpty() || width >= imageData.get().width() && height >= imageData.get().height()) {
            return imageData;
        }

        var optional = textures.stream()
                .filter(it -> it.width() == width && it.height() == height)
                .findAny();

        if (optional.isPresent()) {
            // Move to the front
            textures.remove(optional.get());
            textures.addFirst(optional.get());
            return optional;
        }

        // Resample
        var result = FastSincResampler.resample(imageData.get().buffer(),
                imageData.get().width(), imageData.get().height(),
                width, height, FastSincResampler.THREADS);
        return Optional.of(addToCache(width, height, result));
    }

    private CachedTexture addToCache(int width, int height, int[] buffer) {
        var texture = new CachedTexture(width, height, buffer);
        textures.addFirst(texture);
        if (textures.size() > CACHE_SIZE) textures.removeLast();
        return texture;
    }

    private void initImage(InputStream stream) {
        image = new Image(stream);

        if (image.isError()) {
            image = null;
            return;
        }

        var w = (int) image.getWidth();
        var h = (int) image.getHeight();
        var buffer = new int[w * h];
        image.getPixelReader().getPixels(0, 0, w, h, PixelFormat.getIntArgbPreInstance(), buffer, 0, w);
        imageData = new CachedTexture(w, h, buffer);
    }

    @Override
    public String toString() {
        return "IconData{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}

package net.jamsimulator.jams.gui.image.icon;

import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.plugin.Plugin;

/**
 * Represents the data of an Icon. Use a {@link IconManager} to load this icon.
 */
public class IconData {

    private final String name, url;
    private final Class<?> holder;

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
    public String getUrl() {
        return url;
    }

    /**
     * Returns the {@link Class} from where the icon should be loaded.
     *
     * @return the {@link Class}.
     */
    public Class<?> getHolder() {
        return holder;
    }

    @Override
    public String toString() {
        return "IconData{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}

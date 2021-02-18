package net.jamsimulator.jams.gui.image.icon;

/**
 * Represents the data of an Icon. Use a {@link IconManager} to load this icon.
 */
public class IconData {

    private final String name, url;

    /**
     * Creates the data.
     *
     * @param name the name of the icon.
     * @param url  the url of the icon.
     */
    public IconData(String name, String url) {
        this.name = name;
        this.url = url;
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

    @Override
    public String toString() {
        return "IconData{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}

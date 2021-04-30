package net.jamsimulator.jams.gui.action.context;

import net.jamsimulator.jams.utils.Validate;

import java.util.Objects;

public class MainMenuRegion {

    public static MainMenuRegion FILE = new MainMenuRegion("file", "MAIN_MENU_FILE", 0);
    public static MainMenuRegion EDIT = new MainMenuRegion("edit", "MAIN_MENU_EDIT", 1);
    public static MainMenuRegion MIPS = new MainMenuRegion("mips", "MAIN_MENU_MIPS", 2);
    public static MainMenuRegion TOOLS = new MainMenuRegion("tools", "MAIN_MENU_TOOLS", 3);
    public static MainMenuRegion HELP = new MainMenuRegion("help", "MAIN_MENU_HELP", Integer.MAX_VALUE);

    private final String name;
    private final String languageNode;
    private final int priority;

    public MainMenuRegion(String name, String languageNode, int priority) {
        Validate.notNull(name, "Name cannot be null!");
        this.name = name;
        this.languageNode = languageNode;
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public String getLanguageNode() {
        return languageNode;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MainMenuRegion that = (MainMenuRegion) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}

package net.jamsimulator.jams.gui.sidebar;

import javafx.scene.layout.AnchorPane;
import net.jamsimulator.jams.utils.Validate;

/**
 * Represents a sidebar that has another sidebar above it.
 */
public class BottomSidebar extends Sidebar {

    private Sidebar topSidebar;

    /**
     * Creates a bottom sidebar.
     *
     * @param left     whether this is a left or right sidebar.
     * @param sidePane the handled {@link SidePane}.
     */
    public BottomSidebar(boolean left, SidePane sidePane, Sidebar topSidebar) {
        super(left, false, sidePane);
        Validate.notNull(topSidebar, "Top sidebar cannot be null!");
        this.topSidebar = topSidebar;

        topSidebar.heightProperty().addListener((obs, old, val) -> {

            AnchorPane.setTopAnchor(BottomSidebar.this, val.doubleValue());
        });

        new Thread(() -> {
            while (true) {
                System.out.println(BottomSidebar.this.isLeft() + " --- " + BottomSidebar.this.getHeight());
                System.out.println(BottomSidebar.this.getMinHeight());
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Returns the {@link Sidebar} that it's above this sidebar.
     *
     * @return the {@link Sidebar}.
     */
    public Sidebar getTopSidebar() {
        return topSidebar;
    }

    public void refreshHeight() {
        AnchorPane.setTopAnchor(this, topSidebar.getHeight());
    }
}

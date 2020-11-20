package net.jamsimulator.jams.gui.action.defaults.general;

import net.jamsimulator.jams.gui.action.context.ContextRegion;

public class GeneralActionRegions {

	//FILE
	public static final ContextRegion PROJECT = new ContextRegion("project", null, 0);
	public static final ContextRegion SETTINGS = new ContextRegion("settings", null, 1);

	//MIPS
	public static final ContextRegion MIPS_PRIORITY = new ContextRegion("mips_priority", null, 0);

	//HELP
	public static final ContextRegion ABOUT = new ContextRegion("about", null, Integer.MAX_VALUE);
}

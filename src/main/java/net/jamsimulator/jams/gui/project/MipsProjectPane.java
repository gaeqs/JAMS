package net.jamsimulator.jams.gui.project;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.input.ScrollEvent;
import net.jamsimulator.jams.gui.JamsApplication;
import net.jamsimulator.jams.gui.display.FileDisplayList;
import net.jamsimulator.jams.gui.explorer.folder.FolderExplorer;
import net.jamsimulator.jams.gui.image.icon.Icons;
import net.jamsimulator.jams.gui.main.WorkingPane;
import net.jamsimulator.jams.gui.sidebar.SidebarButton;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.project.MipsProject;

/**
 * This class represent the working pane of a project.
 */
public class MipsProjectPane extends WorkingPane {

	protected final MipsProject project;
	protected FolderExplorer explorer;

	public MipsProjectPane(Tab parent, ProjectTab projectTab, MipsProject project) {
		super(parent, projectTab, new FileDisplayList(null));
		this.project = project;

		getFileDisplayList().setWorkingPane(this);
		loadSidebarModules();
	}

	public MipsProject getProject() {
		return project;
	}

	public FileDisplayList getFileDisplayList() {
		return (FileDisplayList) center;
	}

	private void loadSidebarModules() {
		Image explorerIcon = JamsApplication.getIconManager().getOrLoadSafe(Icons.SIDEBAR_EXPLORER,
				Icons.SIDEBAR_EXPLORER_PATH, SidebarButton.IMAGE_SIZE, SidebarButton.IMAGE_SIZE).orElse(null);

		ScrollPane pane = new ScrollPane();
		explorer = new FolderExplorer(project.getFolder(), pane);
		pane.setContent(explorer);
		pane.setFitToHeight(true);
		pane.setFitToWidth(true);

		pane.getContent().addEventHandler(ScrollEvent.SCROLL, scrollEvent -> {
			double deltaY = scrollEvent.getDeltaY() * 0.003;
			pane.setVvalue(pane.getVvalue() - deltaY);
		});


		//explorer.prefWidthProperty().bind(pane.widthProperty().subtract(2));
		topLeftSidebar.addNode("Explorer", pane, explorerIcon, Messages.EXPLORER_NAME);

		explorer.setFileOpenAction(file -> getFileDisplayList().openFile(file.getFile()));
	}

	@Override
	protected void onClose() {
		explorer.killWatchers();
	}
}

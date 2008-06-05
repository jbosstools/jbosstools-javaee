package org.jboss.tools.seam.pages.xml.model.helpers;

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.event.XModelTreeEvent;
import org.jboss.tools.jst.web.model.helpers.WebProcessUpdateHelper;
import org.jboss.tools.seam.pages.xml.model.SeamPagesConstants;
import org.jboss.tools.seam.pages.xml.model.impl.SeamPagesDiagramImpl;

public class SeamPagesUpdateHelper implements WebProcessUpdateHelper {
	private XModelObject config;
	private SeamPagesDiagramImpl diagram;
	private SeamPagesDiagramHelper helper;

	public SeamPagesUpdateHelper(SeamPagesDiagramImpl diagram) {
		this.diagram = diagram;
		this.helper = diagram.getHelper();
		this.config = diagram.getParent();
		SeamPagesUpdateManager.getInstance(diagram.getModel()).register(config.getPath(), this);
	}
	
	public void unregister() {
		SeamPagesUpdateManager.getInstance(diagram.getModel()).unregister(config.getPath(), this);
	}
    
	public boolean isActive() {
		return diagram.isActive();
	}

	public void nodeChanged(XModelTreeEvent event, String localPath) {
		if(localPath == null || localPath.length() == 0) {
			return;
		} else if(localPath.startsWith(SeamPagesConstants.FOLDER_PAGES)
				|| localPath.startsWith(SeamPagesConstants.FOLDER_EXCEPTIONS)
		) {
			helper.updateDiagram();
		}
	}

	public void structureChanged(XModelTreeEvent event, String localPath) {
		if(localPath == null) return;
		if(localPath.startsWith(SeamPagesConstants.FOLDER_PAGES)
				|| localPath.startsWith(SeamPagesConstants.FOLDER_EXCEPTIONS)
		) {
			helper.updateDiagram();
			if(!helper.isUpdateLocked()) {
				helper.autolayout();
			}
		}
	}

}

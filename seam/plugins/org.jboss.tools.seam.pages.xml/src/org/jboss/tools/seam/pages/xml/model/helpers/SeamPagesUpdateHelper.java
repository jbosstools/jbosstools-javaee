package org.jboss.tools.seam.pages.xml.model.helpers;

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.event.XModelTreeEvent;
import org.jboss.tools.jst.web.model.helpers.WebProcessUpdateHelper;
import org.jboss.tools.seam.pages.xml.model.SeamPagesConstants;
import org.jboss.tools.seam.pages.xml.model.impl.SeamPagesProcessImpl;

public class SeamPagesUpdateHelper implements WebProcessUpdateHelper {
	private XModelObject config;
	private SeamPagesProcessImpl process;
	private SeamPagesProcessHelper helper;

	public SeamPagesUpdateHelper(SeamPagesProcessImpl process) {
		this.process = process;
		this.helper = process.getHelper();
		this.config = process.getParent();
		SeamPagesUpdateManager.getInstance(process.getModel()).register(config.getPath(), this);
	}
	
	public void unregister() {
		SeamPagesUpdateManager.getInstance(process.getModel()).unregister(config.getPath(), this);
	}
    
	public boolean isActive() {
		return process.isActive();
	}

	public void nodeChanged(XModelTreeEvent event, String localPath) {
		if(localPath == null || localPath.length() == 0) {
			return;
		} else if(localPath.startsWith(SeamPagesConstants.FOLDER_PAGES)
				|| localPath.startsWith(SeamPagesConstants.FOLDER_EXCEPTIONS)
		) {
			helper.updateProcess();
		}
	}

	public void structureChanged(XModelTreeEvent event, String localPath) {
		if(localPath == null) return;
		if(localPath.startsWith(SeamPagesConstants.FOLDER_PAGES)
				|| localPath.startsWith(SeamPagesConstants.FOLDER_EXCEPTIONS)
		) {
			helper.updateProcess();
			if(!helper.isUpdateLocked()) {
				helper.autolayout();
			}
		}
	}

}

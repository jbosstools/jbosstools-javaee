/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.jsf.model.helpers;

import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.event.XModelTreeEvent;
import org.jboss.tools.jsf.model.*;
import org.jboss.tools.jst.web.model.helpers.WebProcessUpdateHelper;

public class JSFUpdateHelper implements WebProcessUpdateHelper {
	private XModelObject config;
	private FacesProcessImpl process;
	private JSFProcessHelper helper;

	public JSFUpdateHelper(FacesProcessImpl process) {
		this.process = process;
		this.helper = process.getHelper();
		this.config = process.getParent();
		JSFUpdateManager.getInstance(process.getModel()).register(config.getPath(), this);
	}
	
	public void unregister() {
		JSFUpdateManager.getInstance(process.getModel()).unregister(config.getPath(), this);
	}
    
	public boolean isActive() {
		return process.isActive();
	}

	public void nodeChanged(XModelTreeEvent event, String localPath) {
		if(localPath == null || localPath.length() == 0) {
			return;
		} else if(localPath.startsWith(JSFConstants.FOLDER_NAVIGATION_RULES)) {
			helper.updateProcess();
		}
	}

	public void structureChanged(XModelTreeEvent event, String localPath) {
		if(localPath == null) return;
		if(localPath.startsWith(JSFConstants.FOLDER_NAVIGATION_RULES)) {
			helper.updateProcess();
			if(!helper.isUpdateLocked()) {
				helper.autolayout();
			}
		}
	}

}

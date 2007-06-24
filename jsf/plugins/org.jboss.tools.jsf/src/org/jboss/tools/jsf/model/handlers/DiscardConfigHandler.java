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
package org.jboss.tools.jsf.model.handlers;

import java.util.*;

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.filesystems.impl.DiscardFileHandler;
import org.jboss.tools.common.model.loaders.XObjectLoader;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;
import org.jboss.tools.jsf.model.helpers.JSFProcessStructureHelper;
import org.jboss.tools.jst.web.model.WebProcessLoader;

public class DiscardConfigHandler extends DiscardFileHandler {
	
	public void executeHandler(XModelObject object, Properties p) throws Exception {
		if(!isEnabled(object)) return;
		XModelObject process = JSFProcessStructureHelper.instance.getProcess(object);
		if(process != null && JSFProcessStructureHelper.instance.isProcessLoaded(process)) {
			XModelObject[] os = process.getChildren();
			for (int i = 0; i < os.length; i++) os[i].removeFromParent();
		}
		super.executeHandler(object, p);
		if(process != null) {
			XObjectLoader loader = XModelObjectLoaderUtil.getObjectLoader(process.getParent());
			((WebProcessLoader)loader).reloadProcess(process.getParent());
		}
	}	

}

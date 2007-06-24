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
package org.jboss.tools.jsf.web.helpers.context;

import java.io.File;
import java.util.*;

import org.eclipse.osgi.util.NLS;

import org.jboss.tools.common.model.*;
import org.jboss.tools.jsf.JSFPreference;
import org.jboss.tools.jsf.messages.JSFUIMessages;
import org.jboss.tools.jsf.project.JSFNature;
import org.jboss.tools.jst.web.context.AdoptWebProjectContext;
import org.jboss.tools.jst.web.context.ImportWebDirProjectContext;

public class ImportProjectWizardContext extends ImportWebDirProjectContext {
	
	public ImportProjectWizardContext(XModelObject target) {
		super(target);
	}	
	
	protected void initRegistry() {
		getRegisterTomcatContext().setNatureIndex(JSFNature.NATURE_NICK);
		getRegisterTomcatContext().setPreferences(JSFPreference.REGISTER_IMPORTED_JSF_PROJECT_IN_TOMCAT);
		getRegisterTomcatContext().init();
	}
	
	public void setPexFileName(String value) {
	}

	public boolean createConfigFile(String path) {
		File f = new File(path);
		if(f.exists()) {
			ServiceDialog d = target.getModel().getService();
			String message = NLS.bind(JSFUIMessages.FILE_EXISTS, path);
			d.showDialog(JSFUIMessages.ERROR, message, new String[]{JSFUIMessages.OK}, null, ServiceDialog.ERROR);			
			return false;
		}
		createConfigFile(f, "FacesConfig");
		return true;		
	}
	
	public String getNatureID() {
		return JSFNature.NATURE_ID;
	}

	protected AdoptWebProjectContext createAdoptContext() {
		return new AdoptJSFProjectContext(); 
	}

	public void addSupportDelta(Properties p) {}
	
	public void rollbackSupportDelta() {}
	
	public void commitSupportDelta() {}
	
}

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
package org.jboss.tools.jsf.ui.operation;

import java.io.File;

import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.jsf.JSFPreference;
import org.jboss.tools.jsf.project.JSFNature;
import org.jboss.tools.jsf.web.JSFTemplate;
import org.jboss.tools.jsf.web.helpers.context.AdoptJSFProjectFinisher;
import org.jboss.tools.jst.web.context.IImportWebProjectContext;
import org.jboss.tools.jst.web.ui.operation.WebProjectAdoptOperation;

public class JSFProjectAdoptOperation extends WebProjectAdoptOperation {

	public JSFProjectAdoptOperation(IImportWebProjectContext context) {
		super(context);
	}
		
	protected void execute() throws Exception {
		AdoptJSFProjectFinisher finisher = new AdoptJSFProjectFinisher();
		finisher.setContext(model, context);
		finisher.execute();
	}
	
	protected void copyLibraries() {
		if(!context.getAddLibraries()) return;
		String version = context.getTemplateVersion();
		JSFTemplate template = new JSFTemplate();
		String[] jars = template.getLibraries(version);
		String libDir = context.getLibLocation();
		if(libDir == null || libDir.trim().length() == 0) {
			//should not be
			return;
		}

		for (int i = 0; i < jars.length; i++) {
			File source = new File(jars[i]); 
			FileUtil.copyFile(source, new File(libDir, source.getName()), true);
		}
	}

	protected String getNatureID() {
		return JSFNature.NATURE_ID;
	}

	protected String getDefaultServletVersion() {
		return JSFPreference.DEFAULT_JSF_IMPORT_SERVLET_VERSION.getValue();
	}

}

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
package org.jboss.tools.jsf.web;

import org.jboss.tools.jst.web.project.version.ProjectVersions;

public class JSFVersions extends ProjectVersions {
	static JSFVersions instance;
	
	public static JSFVersions getInstance(String path) {
		if(instance == null) {
			instance = new JSFVersions();
			instance.setDescriptorFileName("/JSFVersions.xml");
		}
		instance.setPath(path);
		return instance;
	}
	
	protected String getWizardEntitySuffix() {
		return "JSF";
	}

}

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

import org.jboss.tools.common.model.*;
import org.jboss.tools.jsf.web.pattern.*;
import org.jboss.tools.jst.web.project.WebProject;

public class JSFWebProject {
	public static JSFWebProject getInstance(XModel model) {
		JSFWebProject instance = (JSFWebProject)model.getManager("JSFWebProject");
		if(instance == null) {
			instance = new JSFWebProject();
			instance.setModel(model);
			model.addManager("JSFWebProject", instance);
		}
		instance.update();
		return instance;
	}

	private WebProject webProject;
	private PatternLoader patterns = new PatternLoader();
	
	private void setModel(XModel model) {
		webProject = WebProject.getInstance(model);
	}
	
	public void update() {
	}

	public XModel getModel() {
		return webProject.getModel();
	}

	public PatternLoader getPatternLoader() {
		return patterns;
	}
    
	public JSFUrlPattern getUrlPattern() {
		return patterns.getUrlPattern();
	}

	public WebProject getWebProject() {
		return webProject;
	}

}

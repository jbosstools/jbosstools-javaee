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
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.jsf.project.JSFNature;
import org.jboss.tools.common.model.project.IWatcherContributor;
import org.jboss.tools.jst.web.model.helpers.WebAppHelper;

public class JSFWatcherContributor implements IWatcherContributor {
	XModel model = null;
	XModelObject webxml;

	public void init(XModel model) {
		this.model = model;
	}

	public boolean isActive() {
		return EclipseResourceUtil.hasNature(model, JSFNature.NATURE_ID);
	}

	public void update() {
		webxml = WebAppHelper.getWebApp(model);
	}

	public String getError() {
		try {
			checkCorrectness();
		} catch (Exception e) {
			return e.getMessage();
		}
		return null;
	}
	
	private void checkCorrectness() throws Exception {		
	}

	public void updateProject() {
		JSFWebProject.getInstance(model).getPatternLoader().revalidate(webxml);
		JSFWebProject.getInstance(model).getWebProject().getTaglibMapping().revalidate(webxml);
	}

}

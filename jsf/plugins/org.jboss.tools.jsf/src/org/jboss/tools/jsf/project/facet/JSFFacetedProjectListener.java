/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.jsf.project.facet;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectEvent;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectEvent.Type;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectListener;
import org.eclipse.wst.common.project.facet.core.events.IProjectFacetActionEvent;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.jsf.JSFModelPlugin;
import org.jboss.tools.jsf.project.JSFNature;
import org.jboss.tools.jst.web.WebModelPlugin;
import org.jboss.tools.jst.web.kb.IKbProject;
import org.jboss.tools.jst.web.kb.internal.KbBuilder;

/**
 * 
 * @author eskimo & Viacheslav Kabanovich
 *
 */
public class JSFFacetedProjectListener implements IFacetedProjectListener  {

	@Override
	public void handleEvent(IFacetedProjectEvent event) {
		if(event.getType() == Type.PRE_INSTALL && event instanceof IProjectFacetActionEvent) {
			IProject project = event.getProject().getProject();
			String facetID = ((IProjectFacetActionEvent)event).getProjectFacet().getId();
			if("jst.webfragment".equals(facetID) || "jst.jsf".equals(facetID)) {
				try {
					WebModelPlugin.addNatureToProjectWithValidationSupport(project, KbBuilder.BUILDER_ID, IKbProject.NATURE_ID);
				} catch (CoreException e) {
					JSFModelPlugin.getDefault().logError(e);
				}
			}
			if("jst.jsf".equals(facetID)) {
				try {
					EclipseResourceUtil.addNatureToProject(project, JSFNature.NATURE_ID);
				} catch (CoreException e) {
					JSFModelPlugin.getDefault().logError(e);
				}
			}
		}
	}	
}

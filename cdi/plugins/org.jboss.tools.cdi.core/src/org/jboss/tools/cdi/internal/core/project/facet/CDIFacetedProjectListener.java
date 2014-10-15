/******************************************************************************* 
 * Copyright (c) 2014 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.internal.core.project.facet;

import java.util.HashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.common.componentcore.internal.util.IModuleConstants;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectEvent;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectEvent.Type;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectListener;
import org.eclipse.wst.common.project.facet.core.events.IProjectFacetActionEvent;
import org.jboss.tools.cdi.core.CDIUtil;

/**
 * Adds CDI capabilities to a faceted project that has one of facets
 *     jst.web of version 3.1 or higher
 *     jst.ejb of version 3.2 or higher
 *     jst.utility
 * 
 * File beans.xml is not created as it is not required in CDI 1.1.
 * That makes this implementation work ok when user enables CDI facet
 * in New Faceted Project wizard - beans.xml will be created with 
 * the version selected by user.
 * 
 * A modification to this implementation may be needed only if 
 * some new version of CDI require beans.xml as it was in CDI 1.0.
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class CDIFacetedProjectListener implements IFacetedProjectListener  {
	static HashMap<String, String> minimalVersions = new HashMap<String, String>();

	static {
		minimalVersions.put(IModuleConstants.JST_WEB_MODULE, "3.1");
		minimalVersions.put(IModuleConstants.JST_EJB_MODULE, "3.2");
		minimalVersions.put(IModuleConstants.JST_UTILITY_MODULE, null);
	}

	public CDIFacetedProjectListener() {
	}

	@Override
	public void handleEvent(IFacetedProjectEvent event) {
		if(event.getType() == Type.PRE_INSTALL && event instanceof IProjectFacetActionEvent) {
			IProject project = event.getProject().getProject();
			IProjectFacet facet = ((IProjectFacetActionEvent)event).getProjectFacet();
			IProjectFacetVersion version = ((IProjectFacetActionEvent)event).getProjectFacetVersion();
			if(isCDIRequired(facet, version)) {
				CDIUtil.enableCDI(project, new NullProgressMonitor());
			}
		}
	}

	boolean isCDIRequired(IProjectFacet facet, IProjectFacetVersion version) {
		if(!minimalVersions.containsKey(facet.getId())) {
			return false;
		}
		String minimalVersionId = minimalVersions.get(facet.getId());
		if(version != null && minimalVersionId != null) {
			IProjectFacetVersion minimalVersion = facet.getVersion(minimalVersionId);
			return minimalVersion != null && minimalVersion.compareTo(version) <= 0;
		}
		return true;
	}
}

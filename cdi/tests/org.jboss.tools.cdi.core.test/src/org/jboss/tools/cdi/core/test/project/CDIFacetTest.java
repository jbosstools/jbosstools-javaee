/******************************************************************************* 
 * Copyright (c) 2013 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.core.test.project;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jst.common.project.facet.core.JavaFacet;
import org.eclipse.jst.j2ee.project.facet.IJ2EEFacetConstants;
import org.eclipse.jst.jsf.core.IJSFCoreConstants;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.jboss.tools.cdi.internal.core.project.facet.CDIFacetInstallDelegate;

/**
 * @author Alexey Kazakov
 */
public class CDIFacetTest extends TestCase {

	public void testCDI10Actions() {
		assertActions(CDIFacetInstallDelegate.CDI_10);
	}

	public void testCDI11Actions() {
		assertActions(CDIFacetInstallDelegate.CDI_11);
	}

	private void assertActions(IProjectFacetVersion cdiVersion) {
		IProjectFacet jsfFacet = ProjectFacetsManager.getProjectFacet(IJSFCoreConstants.JSF_CORE_FACET_ID);
		IProjectFacetVersion jsfVersion = jsfFacet.getVersion(IJSFCoreConstants.FACET_VERSION_2_0);

		Set<IProjectFacetVersion> vs = new HashSet<IProjectFacetVersion>();
		vs.add(JavaFacet.VERSION_1_7);
		vs.add(IJ2EEFacetConstants.DYNAMIC_WEB_30);
		vs.add(jsfVersion);

		Set<IFacetedProject.Action> actions = new HashSet<Action>();
		actions.add(new Action(IFacetedProject.Action.Type.INSTALL, cdiVersion, null));

		IStatus status = ProjectFacetsManager.check(vs, actions);
		assertTrue(status.getMessage(), status.isOK());

		vs.add(cdiVersion);
		actions.clear();
		actions.add(new Action(IFacetedProject.Action.Type.UNINSTALL, cdiVersion, null));

		status = ProjectFacetsManager.check(vs, actions);
		assertTrue(status.getMessage(), status.isOK());

		actions.clear();
		actions.add(new Action(IFacetedProject.Action.Type.VERSION_CHANGE, cdiVersion, null));
		status = ProjectFacetsManager.check(vs, actions);
		assertTrue(status.getMessage(), status.isOK());
	}
}
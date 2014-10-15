/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.core.test;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.common.componentcore.internal.util.IModuleConstants;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ResourcesUtils;

/**
 *   
 * @author V.Kabanovich
 *
 */
public class CDIFacetedProjectListenerTest extends TestCase {
	protected static String PLUGIN_ID = "org.jboss.tools.cdi.core.test";
	IProject project = null;

	public CDIFacetedProjectListenerTest() {}

	@Override
	public void setUp() throws Exception {
		project = ResourcesUtils.importProject(PLUGIN_ID, "/projects/FacetedProject");
		project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
	}

	public void testWeb30() throws Exception {
		doTestFacet(IModuleConstants.JST_WEB_MODULE, "3.0", false);
	}

	public void testWeb31() throws Exception {
		doTestFacet(IModuleConstants.JST_WEB_MODULE, "3.1", true);
	}

	public void testEJB31() throws Exception {
		doTestFacet(IModuleConstants.JST_EJB_MODULE, "3.1", false);
	}

	public void testEJB32() throws Exception {
		doTestFacet(IModuleConstants.JST_EJB_MODULE, "3.2", true);
	}

	public void testUtility() throws Exception {
		doTestFacet(IModuleConstants.JST_UTILITY_MODULE, null, true);
	}

	void doTestFacet(String id, String versionId, boolean cdiExpected) throws CoreException {
		IProjectFacet facet = ProjectFacetsManager.getProjectFacet(id);
		IProjectFacetVersion version = versionId !=null ? facet.getVersion(versionId)
				: facet.getDefaultVersion();
		IFacetedProject fp = ProjectFacetsManager.create(project);
		IFacetedProjectWorkingCopy wc = fp.createWorkingCopy();
		wc.addProjectFacet(version);
		wc.commitChanges(new NullProgressMonitor());
		wc.dispose();
		JobUtils.waitForIdle(1000);

		ICDIProject cdi = CDICorePlugin.getCDIProject(project, false);
		if(cdiExpected) {
			assertNotNull("CDI is expected for facet " + id + " of version " + versionId, cdi);
		} else {
			assertNull("CDI is not expected for facet " + id + " of version " + versionId,cdi);
		}
	}

	@Override
	public void tearDown() throws Exception {
		boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
		project.delete(true, true, null);
		JobUtils.waitForIdle();
		ResourcesUtils.setBuildAutomatically(saveAutoBuild);
	}


}
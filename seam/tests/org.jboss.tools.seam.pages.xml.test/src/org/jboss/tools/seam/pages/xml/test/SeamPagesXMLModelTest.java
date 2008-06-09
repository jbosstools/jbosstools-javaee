/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.seam.pages.xml.test;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.seam.pages.xml.model.SeamPagesConstants;
import org.jboss.tools.seam.pages.xml.model.helpers.SeamPagesDiagramHelper;
import org.jboss.tools.seam.pages.xml.model.helpers.SeamPagesDiagramStructureHelper;
import org.jboss.tools.seam.pages.xml.model.impl.SeamPagesDiagramImpl;
import org.jboss.tools.test.util.ResourcesUtils;
import org.jboss.tools.test.util.xpl.EditorTestHelper;

public class SeamPagesXMLModelTest extends TestCase {
	IProject project = null;

	public SeamPagesXMLModelTest() {
		super("Seam Scanner test");
	}
	
	protected void setUp() throws Exception {
		project = ResourcesUtils.importProject(
				"org.jboss.tools.seam.pages.xml.test","/projects/Test" , new NullProgressMonitor());
		project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		EditorTestHelper.joinBackgroundActivities();
	}

	/**
	 * This test is to check different cases of declaring components in xml.
	 * It does not check interaction of xml declaration with other declarations.
	 */
	public void testXMLModel() {
		IFile f = project.getFile(new Path("pages/wiki.xml"));
		assertNotNull("File pages/wiki.xml is not found in Test project.", f);

		XModelObject fileObject = EclipseResourceUtil.createObjectForResource(f);
		assertNotNull("Cannot create XModel object for file pages/wiki.xml.", fileObject);

		String entity = fileObject.getModelEntity().getName();
		assertEquals("File pages/wiki.xml is incorrectly parsed by XModel.", SeamPagesConstants.ENT_FILE_SEAM_PAGES_20, entity);

		XModelObject diagramXML = SeamPagesDiagramStructureHelper.instance.getProcess(fileObject);
		assertNotNull("Cannot find XModel diagram object for file pages/wiki.xml.", diagramXML);

		if(!(diagramXML instanceof SeamPagesDiagramImpl)) {
			fail("XModel diagram object must be instance of SeamPagesDiagramImpl.");
		}

		SeamPagesDiagramImpl impl = (SeamPagesDiagramImpl)diagramXML;
		//invoke loading
		diagramXML.getChildren();

		SeamPagesDiagramHelper h = impl.getHelper();

		checkTargetMatch("/docDisplay#{a}", h, "/docDisplay*");
		checkTargetMatch("/docDisplay", h, "/docDisplay*");
		checkTargetMatch("/docDispla", h, "/*");

		//TODO continue test
	}
	
	@Override
	protected void tearDown() throws Exception {
		EditorTestHelper.joinJobs(1000, 10000, 500);
		project.delete(true,true, null);
	}

	private void checkTargetMatch(String targetViewId, SeamPagesDiagramHelper h, String expectedMatch) {
		String s = h.findBestMatch(targetViewId);
		assertEquals("Match for target view id is incorrectly computed.", expectedMatch, s);
	}
}

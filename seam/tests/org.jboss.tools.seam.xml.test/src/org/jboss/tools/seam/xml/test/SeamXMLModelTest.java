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
package org.jboss.tools.seam.xml.test;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.markers.XMarkerManager;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.seam.xml.components.model.SeamComponentConstants;
import org.jboss.tools.test.util.JobUtils;

public class SeamXMLModelTest extends TestCase {
	IProject project = null;

	@Override
	protected void setUp() throws Exception {
		project = ResourcesPlugin.getWorkspace().getRoot().getProject("Test");
	}

	/**
	 * This test is to check different cases of declaring components in xml.
	 * It does not check interaction of xml declaration with other declarations.
	 */
	public void testXMLModel() {
		IFile f = project.getFile(new Path("components22.xml"));
		assertTrue("File components22.xml is not accessible in Test project.", f.isAccessible());

		XModelObject fileObject = EclipseResourceUtil.createObjectForResource(f);
		assertNotNull("Cannot create XModel object for file components22.xml.", fileObject);

		String entity = fileObject.getModelEntity().getName();
		assertEquals("File components22.xml is incorrectly parsed by XModel.", SeamComponentConstants.ENT_SEAM_COMPONENTS_22, entity);

		//TODO continue test
	}

	public void testComponentFile() {
		XModelObject fileObject = getComponent22Object();
		String entity = fileObject.getModelEntity().getName();
		assertEquals("File XYZ.component.xml is incorrectly parsed by XModel.", SeamComponentConstants.ENT_SEAM_COMPONENT_FILE_22, entity);
	}

	protected XModelObject getComponents22Object() {
		IFile f = project.getFile(new Path("components22.xml"));
		assertTrue(f.exists());
		return EclipseResourceUtil.createObjectForResource(f);
	}

	protected XModelObject getComponent22Object() {
		IFile f = project.getFile(new Path("XYZ.component.xml"));
		assertNotNull(f);
		assertTrue(f.exists());
		return EclipseResourceUtil.createObjectForResource(f);
	}

	protected XModelObject getComponents23Object() {
		IFile f = project.getFile(new Path("components23.xml"));
		assertTrue(f.exists());
		return EclipseResourceUtil.createObjectForResource(f);
	}

	public void testNavigationPagesComponent() {
		XModelObject fileObject = getComponents22Object();
		assertNotNull("Cannot create XModel object for file components22.xml.", fileObject);

		XModelObject navigationPages = fileObject.getChildByPath("org.jboss.seam.navigation.pages");
		assertNotNull("Cannot find org.jboss.seam.navigation.pages", navigationPages);

		XModelObject resources = navigationPages.getChildByPath("resources");
		assertNotNull("Cannot find resources in org.jboss.seam.navigation.pages", resources);

		XModelObject[] resourcesList = resources.getChildren();
		assertEquals(1, resourcesList.length);

		assertAttribute(navigationPages, "no-conversation-view-id", "a.xhtml");
		assertAttribute(navigationPages, "login-view-id", "b.xhtml");
		assertAttribute(navigationPages, "http-port", "1111");
		assertAttribute(navigationPages, "https-port", "1112");
	}

	public void testDebugAttribute() { //JBIDE-7362
		XModelObject fileObject = getComponents22Object();
		JobUtils.waitForIdle();
		XMarkerManager.getInstance();
		assertNotNull("Cannot create XModel object for file components22.xml.", fileObject);
		
		XModelObject coreInit0 = fileObject.getChildByPath("org.jboss.seam.core.init");
		assertNotNull("Cannot find component org.jboss.seam.core.init.", coreInit0);
		assertFalse("Validator found wrong errors in component org.jboss.seam.core.init", XMarkerManager.getInstance().hasErrors(coreInit0));

		XModelObject coreInit1 = fileObject.getChildByPath("org.jboss.seam.core.init1");
		assertNotNull("Cannot find component org.jboss.seam.core.init1.", coreInit1);
		assertTrue("Validator failed to report an error in component org.jboss.seam.core.init1", XMarkerManager.getInstance().hasErrors(coreInit1));

		XModelObject coreInit2 = fileObject.getChildByPath("org.jboss.seam.core.init2");
		assertNotNull("Cannot find component org.jboss.seam.core.init2.", coreInit2);
		assertFalse("Validator found wrong errors in component org.jboss.seam.core.init2", XMarkerManager.getInstance().hasErrors(coreInit2));

	}

	public void testXML23Model() {
		IFile f = project.getFile(new Path("components23.xml"));
		assertTrue("File components23.xml is not accessible in Test project.", f.isAccessible());

		XModelObject fileObject = EclipseResourceUtil.createObjectForResource(f);
		assertNotNull("Cannot create XModel object for file components22.xml.", fileObject);

		String entity = fileObject.getModelEntity().getName();
		assertEquals("File components23.xml is incorrectly parsed by XModel.", SeamComponentConstants.ENT_SEAM_COMPONENTS_23, entity);

		//TODO continue test
	}

	protected void assertAttribute(XModelObject object, String name, String value) {
		String actual = object.getAttributeValue(name);
		assertEquals("Attribute " + name + " in " + object.getPresentationString() + " is incorrect.", value, actual);
	}
}
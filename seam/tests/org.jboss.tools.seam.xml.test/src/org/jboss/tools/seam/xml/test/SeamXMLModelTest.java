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
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.seam.xml.components.model.SeamComponentConstants;

public class SeamXMLModelTest extends TestCase {
	IProject project = null;

	public SeamXMLModelTest() {
		super("Seam Scanner test");
		project = getTestProject();
	}
	
	public IProject getTestProject() {
		if(project==null) {
			try {
				project = findTestProject();
				if(project==null || !project.exists()) {
//					project = importPreparedProject("/");
				}
			} catch (Exception e) {
				e.printStackTrace();
				fail("Can't import Seam XML test project: " + e.getMessage());
			}
		}
		return project;
	}

	public static IProject findTestProject() {
		return ResourcesPlugin.getWorkspace().getRoot().getProject("Test");
	}


	/**
	 * This test is to check different cases of declaring components in xml.
	 * It does not check interaction of xml declaration with other declarations.
	 */
	public void testXMLModel() {
		IFile f = project.getFile(new Path("components22.xml"));
		assertNotNull("File components22.xml is not found in Test project.", f);

		XModelObject fileObject = EclipseResourceUtil.createObjectForResource(f);
		assertNotNull("Cannot create XModel object for file components22.xml.", fileObject);

		String entity = fileObject.getModelEntity().getName();
		assertEquals("File components22.xml is incorrectly parsed by XModel.", SeamComponentConstants.ENT_SEAM_COMPONENTS_22, entity);

		//TODO continue test
	}

	protected XModelObject getComponents22Object() {
		assertNotNull(getTestProject());
		IFile f = project.getFile(new Path("components22.xml"));
		assertNotNull(f);
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

	protected void assertAttribute(XModelObject object, String name, String value) {
		String actual = object.getAttributeValue(name);
		assertEquals("Attribute " + name + " in " + object.getPresentationString() + " is incorrect.", value, actual);
	}
	
}

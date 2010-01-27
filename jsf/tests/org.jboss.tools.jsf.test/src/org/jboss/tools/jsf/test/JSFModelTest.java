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
package org.jboss.tools.jsf.test;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.jboss.tools.common.test.util.TestDescription;
import org.jboss.tools.common.test.util.TestProjectProvider;

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.project.IModelNature;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.jsf.model.pv.JSFProjectsRoot;
import org.jboss.tools.jsf.model.pv.JSFProjectsTree;

import junit.framework.TestCase;

public class JSFModelTest extends TestCase {
	TestProjectProvider provider = null;
	IProject project = null;
	boolean makeCopy = true;
	
	public JSFModelTest() {}
	
	public void setUp() throws Exception {
		provider = new TestProjectProvider("org.jboss.tools.jsf.test", null, "JSFKickStartOldFormat", false); 
		project = provider.getProject();
	}
	
	public void testModelExists() {
		IModelNature n = EclipseResourceUtil.getModelNature(project);
		assertTrue("Test project " + project.getName() + " has no model nature.", n != null);
		assertTrue("XModel for project " + project.getName() + " is not loaded.", n.getModel() != null);
	}
	
	public void testPaths() {
		IModelNature n = EclipseResourceUtil.getModelNature(project);
		String testName = "JSFModelTest:testPaths";
		ArrayList<TestDescription> tests = provider.getTestDescriptions(testName);
		System.out.println(testName + " " + (tests == null ? -1 : tests.size()));
		StringBuffer sb = new StringBuffer();
		int errorCount = 0;
		
		if(tests != null) for (int i = 0; i < tests.size(); i++) {
			TestDescription t = tests.get(i);
			String path = t.getProperty("path");
			XModelObject o = n.getModel().getByPath(path);
			if(o == null) {
				sb.append(path).append("\n");
				errorCount++;
			}
		}
		assertTrue("Cannot find objects at " + errorCount + " paths\n" + sb.toString(), errorCount == 0);
		
		testName = "JSFModelTest:testPaths:attribute";
		tests = provider.getTestDescriptions(testName);
		System.out.println(testName + " " + (tests == null ? -1 : tests.size()));
		sb = new StringBuffer();
		errorCount = 0;
		if(tests != null) for (int i = 0; i < tests.size(); i++) {
			TestDescription t = tests.get(i);
			String path = t.getProperty("path");
			XModelObject o = n.getModel().getByPath(path);
			if(o == null) {
				sb.append("Cannot find object at " + path).append("\n");
				errorCount++;
				continue;
			}
			String attribute = t.getProperty("attributeName");
			if(attribute == null) {
				sb.append("Attribute name is required for this test " + path).append("\n");
				errorCount++;
				continue;
			}
			if(o.getModelEntity().getAttribute(attribute) == null) {
				sb.append("Attribute " + attribute + " is not found in object " + path).append("\n");
				errorCount++;
				continue;
			}
			String testValue = t.getProperty("attributeValue");
			String realValue = o.getAttributeValue(attribute);
			if(realValue == null || !realValue.equals(testValue)) {
				sb.append("Attribute " + attribute + " in object " + path + " has unexpected value '" + realValue + "'").append("\n");
				errorCount++;
				continue;
			}
		}
		assertTrue(sb.toString(), errorCount == 0);
		
		XModelObject o = n.getModel().getByPath("/faces-config-2.xml");
//		printPaths(o);
		
	}

	void printPaths(XModelObject o) {
		if(o == null) {
			System.out.println("null");
			return;
		}
		System.out.println(o.getPath());
		XModelObject[] cs = o.getChildren();
		for (int i = 0; i < cs.length; i++) printPaths(cs[i]);
	}
	
	public void testJSFProjectStructure() {
		IModelNature n = EclipseResourceUtil.getModelNature(project);
		JSFProjectsRoot root = JSFProjectsTree.getProjectsRoot(n.getModel());
		assertTrue("Cannot find root object.", root != null);
		XModelObject[] cs = root.getTreeChildren();
		for (int i = 0; i < cs.length; i++) {
			System.out.println(cs[i].getPath());
		}		
	}
	
	
	protected void tearDown() throws Exception {
		if(provider != null) {
			provider.dispose();
		}
	}

}

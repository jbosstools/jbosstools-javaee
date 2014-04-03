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
package org.jboss.tools.jsf.test;


import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.project.IModelNature;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.jsf.model.pv.JSFProjectsRoot;
import org.jboss.tools.jsf.model.pv.JSFProjectsTree;
import org.jboss.tools.test.util.ProjectImportTestSetup;

public class JSFWithoutWEBINFTest extends TestCase {
	IProject project = null;
	
	public JSFWithoutWEBINFTest() {}
	
	public void setUp() throws Exception {
		project = ProjectImportTestSetup.loadProject("JSFWithoutWEBINF");
	}
	
	public void testModelExists() {
		IModelNature n = EclipseResourceUtil.getModelNature(project);
		assertTrue("Test project " + project.getName() + " has no model nature.", n != null);
		assertTrue("XModel for project " + project.getName() + " is not loaded.", n.getModel() != null);
	}

	public void testWebRoot() {
		IModelNature n = EclipseResourceUtil.getModelNature(project);
		XModel model = n.getModel();
		XModelObject f = model.getByPath("FileSystems/WEB-ROOT/pages/inputname.xhtml");
		assertNotNull("Cannot find file in web root.", f);
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

}

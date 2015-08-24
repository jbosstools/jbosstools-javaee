/*******************************************************************************
 * Copyright (c) 2007-2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.jsf.test;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.filesystems.FileSystemsHelper;
import org.jboss.tools.common.model.project.IModelNature;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.jsf.model.pv.JSFProjectsRoot;
import org.jboss.tools.jsf.model.pv.JSFProjectsTree;
import org.jboss.tools.test.util.ProjectImportTestSetup;

public class ModelFormat_2_0_0_Test extends TestCase {
	IProject project = null;
	
	public ModelFormat_2_0_0_Test() {}
	
	public void setUp() throws Exception {
		project = ProjectImportTestSetup.loadProject("JSFKickStartOldFormat");
	}
	
	public void testModelExists() {
		IModelNature n = EclipseResourceUtil.getModelNature(project);
		assertTrue("Test project " + project.getName() + " has no model nature.", n != null);
		assertTrue("XModel for project " + project.getName() + " is not loaded.", n.getModel() != null);
	}
	
	public void testObjects() {
		IModelNature n = EclipseResourceUtil.getModelNature(project);
		XModel model = n.getModel();
		XModelObject fs = FileSystemsHelper.getFileSystems(model);
		assertTrue("XModel for project " + project.getName() + " is not loaded.", fs != null);
		String applicationName = fs.getAttributeValue("application name");
		assertTrue("Project application name is " + applicationName + "; should be JSFKickStartOldFormat", "JSFKickStartOldFormat".equals(applicationName));
		
		String[] fileSystemList = {
			"WEB-ROOT",
			"WEB-INF",
			"lib-jstl-1.1.0.jar"	
		};
		List<String> unfound = new ArrayList<String>();
		for (int i = 0; i < fileSystemList.length; i++) {
			XModelObject fs1 = FileSystemsHelper.getFileSystem(model, fileSystemList[i]);
			if(fs1 == null) {
				unfound.add(fileSystemList[i]);
			}
		}
		
		if(unfound.size() > 0) {
			String message = "File systems not found: ";
			for (int i = 0; i < unfound.size(); i++) {
				if(i > 0) message += ", ";
				message += unfound.get(i);
			}
			assertTrue(message, false);
		}
		
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
	
//	protected void tearDown() throws Exception {
//		if(provider!=null) {
//			provider.dispose();
//			provider=null;
//		}
//	}

}

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
package org.jboss.tools.struts.vpe.struts.test;

import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.jboss.tools.vpe.ui.test.ProjectsLoader;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.jboss.tools.vpe.ui.test.VpeTest;

/**
 * Class for testing html struts components
 * 
 * @author dazarov
 * 
 */
public class StrutsHTMLComponentTest extends VpeTest {

	// import project name
	static final String IMPORT_PROJECT_NAME = "StrutsTest";

	public StrutsHTMLComponentTest(String name) {
		super(name);
	}

	/*
	 * Struts HTML test cases
	 */

	public void testBase() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/html/base.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testErrors() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/html/errors.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testFrame() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/html/frame.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
	
	public void testHtml() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/html/html.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
	
	public void testImage() throws Throwable {
		/*
		 * XXX temporary code is used to debug Struts tests on Hudson.
		 * It must be removed when the problem with the failing tests is resolved.
		 */
		StringBuilder debugInfo = new StringBuilder();
		debugInfo.append("projectNameToPath = ")
				.append(ProjectsLoader.getInstance().getProjectNameToPath())
				.append(";\n");
		
		IProject project = ProjectsLoader.getInstance().getProject(IMPORT_PROJECT_NAME);
		assertNotNull(debugInfo.toString(), project);
		debugInfo.append("project = ").append(project).append(";\n")
				.append("project.exists() = ").append(project.exists()).append(";\n")
				.append("project.isOpen() = ").append(project.isOpen()).append(";\n");
				
		IFolder folder = project.getFolder("WebContent/pages");
		assertNotNull(debugInfo.toString(), folder);
		debugInfo.append("folder = ").append(folder).append(";\n")
				.append("folder.exists() = ").append(folder.exists()).append(";\n");
		
		IFile testFile = (IFile) folder.findMember("components/html/image.jsp");
		assertNotNull(debugInfo.toString(), testFile);
		debugInfo.append("testFile = ").append(folder).append(";\n")
				.append("testFile.exists() = ").append(testFile.exists()).append(";\n");

		performTestForVpeComponent(testFile);
		fail(debugInfo.toString());
		
		// performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/html/image.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
	
	public void testImg() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/html/img.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
	
	public void testLink() throws Throwable {
		// XXX wait is added just to check if the test will fail on Hudson.
		// Most probably it is not needed.
		TestUtil.waitForIdle();
		
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/html/link.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
	
	public void testRewrite() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/html/rewrite.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
	
	public void testMessages() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/html/messages.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testJavascript() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/html/javascript.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testOptionsCollection() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/html/optionsCollection.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testXHTML() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/html/xhtml.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
}

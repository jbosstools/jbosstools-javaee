/*******************************************************************************
 * Copyright (c) 2007-2010 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.jsf.test.validation;


import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.jboss.tools.jsf.jsf2.util.JSF2ResourceUtil;
import org.jboss.tools.jst.web.kb.IKbProject;
import org.jboss.tools.jst.web.kb.KbProjectFactory;
import org.jboss.tools.jst.web.kb.internal.KbProject;
import org.jboss.tools.jst.web.kb.taglib.IAttribute;
import org.jboss.tools.jst.web.kb.taglib.IComponent;
import org.jboss.tools.jst.web.kb.taglib.ITagLibrary;
import org.jboss.tools.test.util.ProjectImportTestSetup;

import junit.framework.TestCase;

/**
 * 
 * @author yzhishko
 *
 */

public class JSF2ComponentsInClassFolderTest extends TestCase {

	private static String projectName = "JSF2ComponentsValidator"; //$NON-NLS-1$
	private static IProject project;

	public JSF2ComponentsInClassFolderTest() {
		super("JSF 2 Components In Class Folder Test"); //$NON-NLS-1$
	}

	protected void setUp() throws Exception {
		project = ProjectImportTestSetup.loadProject(projectName);
		project.build(IncrementalProjectBuilder.FULL_BUILD,
				new NullProgressMonitor());
	}

	public void testJSF2ComponentsInClassFolder() throws Exception {
		IKbProject kb = KbProjectFactory.getKbProject(project, true);
		ITagLibrary[] ls = kb.getTagLibraries("http://java.sun.com/jsf/composite/example");
		assertEquals(1, ls.length);
		IComponent[] cs = ls[0].getComponents();
		assertTrue(cs.length > 0);
		assertEquals("input", cs[0].getName());
		IAttribute[] as = cs[0].getAttributes();
		assertEquals(5, as.length);
		IAttribute a = cs[0].getAttribute("value7");
		assertNotNull(a);

	}

}



/*******************************************************************************
 * Copyright (c) 2007-2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.jsf.ui.test;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.StructuredSelection;
import org.jboss.tools.jsf.ui.action.AddJSFNatureActionDelegate;
import org.jboss.tools.test.util.ProjectImportTestSetup;

public class AddJSFCapabilitiesTest extends TestCase {
	private static final String PROJECT_NAME = "test_add_jsf_capabilities";

	IProject project = null;

	public AddJSFCapabilitiesTest() {
		super("Add JSF Capabilities Test");
	}

	public AddJSFCapabilitiesTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		project = ProjectImportTestSetup.loadProject(PROJECT_NAME);
	}

	public void testAddJSFCapabilities() {
		IFile f = project.getFile(new Path(".settings/org.eclipse.wst.common.project.facet.core.xml"));
		assertFalse(f.exists());

		try {
			assertFalse(project.hasNature("org.jboss.tools.jsf.jsfnature"));
		} catch (CoreException e) {
			fail(e.getMessage());
		}

		AddJSFNatureActionDelegate action = new AddJSFNatureActionDelegate(false);
		action.selectionChanged(null, new StructuredSelection(project));
		action.run(null);
		
		try {
			assertTrue(project.hasNature("org.jboss.tools.jsf.jsfnature"));
		} catch (CoreException e) {
			fail(e.getMessage());
		}
		
		assertTrue(f.exists());
	}
}

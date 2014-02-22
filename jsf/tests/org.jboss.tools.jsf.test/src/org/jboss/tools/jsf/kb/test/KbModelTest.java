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
package org.jboss.tools.jsf.kb.test;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.jboss.tools.jst.web.kb.IKbProject;
import org.jboss.tools.jst.web.kb.internal.taglib.composite.CompositeTagLibrary;
import org.jboss.tools.jst.web.kb.taglib.IComponent;
import org.jboss.tools.jst.web.kb.taglib.ITagLibrary;
import org.jboss.tools.test.util.JUnitUtils;

public class KbModelTest extends TestCase {

	IProject project = null;
	boolean makeCopy = true;

	public KbModelTest() {
		super("Kb Model Test");
	}

	public void setUp() throws Exception {
		project = ResourcesPlugin.getWorkspace().getRoot().getProject("TestKbModel");
		assertNotNull("Can't load TestKbModel", project); //$NON-NLS-1$
	}

	private IKbProject getKbProject() {
		IKbProject kbProject = null;
		try {
			kbProject = (IKbProject)project.getNature(IKbProject.NATURE_ID);
		} catch (Exception e) {
			JUnitUtils.fail("Cannot get seam nature.",e);
		}
		return kbProject;
	}

	public void testCompositeComponentLibraries() {
		assertCompositeComponentLibraries("http://java.sun.com/jsf/composite/demo", "");
	}

	public void testCompositeComponent22Libraries() {
		assertCompositeComponentLibraries("http://xmlns.jcp.org/jsf/composite/demo", "CC22");
	}

	public void testFaceletLibraryWithCompositeLibraryName() {
		IKbProject kbProject = getKbProject();
		ITagLibrary[] ls = kbProject.getTagLibraries("myFaceletWithCompositeLibraryName");
		assertEquals(1, ls.length);
		ITagLibrary l = ls[0];
		IComponent[] cs = l.getComponents();
		assertEquals(4, cs.length);
		IComponent c = l.getComponent("input");
		assertNotNull(c);
	}

	public void assertCompositeComponentLibraries(String uri, String sufix) {
		IKbProject kbProject = getKbProject();
		ITagLibrary[] ls = kbProject.getTagLibraries(uri);
		assertEquals(1, ls.length);
		ITagLibrary l = ls[0];
		assertTrue(l instanceof CompositeTagLibrary);

		//input.xhtml has root element other than <html>
		IComponent c = l.getComponent("input" + sufix);
		assertNotNull(c);
		assertNotNull(c.getAttribute("label"));

		c = l.getComponent("input2" + sufix);
		assertNotNull(c);
		assertNotNull(c.getAttribute("label2"));
	}
}
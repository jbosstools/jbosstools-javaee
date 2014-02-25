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
import org.jboss.tools.jst.web.kb.internal.KbProject;
import org.jboss.tools.jst.web.kb.internal.taglib.myfaces.MyFacesTagLibrary;
import org.jboss.tools.jst.web.kb.taglib.IComponent;
import org.jboss.tools.jst.web.kb.taglib.ITagLibrary;
import org.jboss.tools.test.util.JUnitUtils;

public class MyFacesKbModelTest extends TestCase {

	protected IProject project = null;
	protected boolean makeCopy = true;

	public MyFacesKbModelTest() {
		super("MyFaces Kb Model Test");
	}

	public void setUp() throws Exception {
		project = ResourcesPlugin.getWorkspace().getRoot().getProject("MyFaces");
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

	public void testKbProjectObjects() {
		IKbProject kbProject = getKbProject();
		assertNotNull(kbProject);

		ITagLibrary[] ls = kbProject.getTagLibraries("http://java.sun.com/jsf/facelets");
		assertEquals(1, ls.length);
		assertTrue(ls[0] instanceof MyFacesTagLibrary);
		MyFacesTagLibrary faceletsTagLib = (MyFacesTagLibrary)ls[0];
		String[] faceletTags = {"component", "fragment", "composition", "decorate", "define", "include", "insert", "param"};
		for (String tag: faceletTags) {
			assertNotNull(faceletsTagLib.getComponent(tag));
		}
		
		MyFacesTagLibrary coreTagLib = null;
		ls = kbProject.getTagLibraries("http://java.sun.com/jsf/core");
		for (int i = 0; i < ls.length; i++) {
			if(ls[i] instanceof MyFacesTagLibrary) {
				coreTagLib = (MyFacesTagLibrary)ls[i];
			}
		}
		assertNotNull(coreTagLib);
		String[] coreTags = {"event", "metadata"};
		for (String tag: coreTags) {
			IComponent component = null;
			for (int i = 0; i < ls.length; i++) {
				if(ls[i] instanceof MyFacesTagLibrary) {
					coreTagLib = (MyFacesTagLibrary)ls[i];
					component = coreTagLib.getComponent(tag);
				}
				if(component != null) break;
			}
			assertNotNull("Cannot find tag '" + tag + "'", component);
		}
		assertEquals(2, coreTagLib.getComponent("event").getAttributes().length);
		
	}

	public void testReload() {
		IKbProject kbProject = getKbProject();
		((KbProject)kbProject).reload();
		testKbProjectObjects();
	}

}
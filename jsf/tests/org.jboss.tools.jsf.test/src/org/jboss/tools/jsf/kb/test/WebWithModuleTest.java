/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.jsf.kb.test;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.jst.web.kb.IKbProject;
import org.jboss.tools.jst.web.kb.KbProjectFactory;
import org.jboss.tools.jst.web.kb.taglib.IComponent;
import org.jboss.tools.jst.web.kb.taglib.ITagLibrary;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class WebWithModuleTest extends TestCase {
	protected IProject utility = null;
	protected IProject webapp = null;

	public WebWithModuleTest() {
		super("MyFaces Kb Model Test");
	}

	public void setUp() throws Exception {
		utility = ResourcesPlugin.getWorkspace().getRoot().getProject("utility");
		assertNotNull("Can't load utility", utility); //$NON-NLS-1$
		webapp = ResourcesPlugin.getWorkspace().getRoot().getProject("webapp");
		assertNotNull("Can't load webapp", webapp); //$NON-NLS-1$
	}

	/**
	 * webapp project has kb nature, and depends on utility project without kb nature.
	 * In this case builder adds kb problem marker to 'webapp'
	 * and sets on 'utility' property '...mock' to 'true'..
	 * 
	 * Check that file 'utility' has correct property '...mock'.
	 * Check that kb model of 'webapp' has tag library declared in sources of 'utility'.
	 * 
	 * @throws CoreException
	 */
	public void testWebProject() throws CoreException {
		assertTrue("true".equals(utility.getPersistentProperty(KbProjectFactory.NATURE_MOCK)));
		
		IKbProject kbUtility = KbProjectFactory.getKbProject(utility, true);
		assertNotNull(kbUtility);
		int w = 0;
		while(kbUtility.getTagLibraries().length == 0 && w++ < 50) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				break;
			}
		}

		IKbProject kb = KbProjectFactory.getKbProject(webapp, true);
		ITagLibrary[] ls = kb.getTagLibraries("utility-lib");
		assertTrue(ls.length > 0);
		ls = kb.getTagLibraries("http://java.sun.com/jsf/composite/cc");
		assertTrue(ls.length > 0);
		IComponent c = ls[0].getComponent("inputText");
		assertNotNull(c);
	}

}

/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.seam.ui.test;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.jboss.tools.common.ui.JBossPerspectiveFactory;

/**
 * @author Alexey Kazakov
 */
public class JBossPerspectiveTest extends TestCase {

	/**
	 * Tests JBoss perspective has Seam stuff
	 * See https://issues.jboss.org/browse/JBIDE-10142
	 * @throws WorkbenchException
	 */
	public void testJBossPerspective() throws WorkbenchException {
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		IWorkbenchPage page = workbench.showPerspective(JBossPerspectiveFactory.PERSPECTIVE_ID, window);
		assertNotNull(page);
		String[] shortcuts = page.getNewWizardShortcuts();
		Set<String> shortcutSet = new HashSet<String>();
		for (String shortcut : shortcuts) {
			shortcutSet.add(shortcut);
		}
		assertTrue("Have not found org.jboss.tools.seam.ui.wizards.SeamProjectWizard in " + JBossPerspectiveFactory.PERSPECTIVE_ID, shortcutSet.contains("org.jboss.tools.seam.ui.wizards.SeamProjectWizard"));
	}
}
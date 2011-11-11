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
package org.jboss.tools.jsf.ui.test;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.WorkbenchException;
import org.jboss.tools.common.ui.JBossPerspectiveFactory;
import org.jboss.tools.test.util.WorkbenchUtils;

/**
 * @author Alexey Kazakov
 */
public class JBossPerspectiveTest extends TestCase {

	/**
	 * Tests JBoss perspective has JSF stuff
	 * See https://issues.jboss.org/browse/JBIDE-10145
	 * @throws WorkbenchException
	 */
	public void testJBossPerspective() throws WorkbenchException {
		IWorkbenchPage page = WorkbenchUtils.getWorkbench().getActiveWorkbenchWindow().openPage(JBossPerspectiveFactory.PERSPECTIVE_ID, null);
		assertNotNull(page);
		IViewPart[] views = page.getViews();
		System.out.println("Views number: " + views.length);
		IViewReference[] viewReferences = page.getViewReferences();
		Set<String> viewIds = new HashSet<String>();
		for (IViewReference viewReference : viewReferences) {
			System.out.println("View ID: " + viewReference.getId());
			viewIds.add(viewReference.getId());
		}
		assertTrue("Have not found org.eclipse.gef.ui.palette_view in org.eclipse.jst.j2ee.J2EEPerspective.", viewIds.contains("org.eclipse.gef.ui.palette_view"));
	}
}
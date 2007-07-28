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

package org.jboss.tools.seam.core.test.project.facet;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.jboss.tools.seam.internal.core.project.facet.WtpUtils;

import junit.framework.TestCase;

/**
 * @author eskimo
 *
 */
public class WtpUtilsTest extends TestCase {

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for {@link org.jboss.tools.seam.internal.core.project.facet.WtpUtils#createEclipseProject(java.lang.String)}.
	 */
	public void testCreateEclipseProject() {
		WtpUtils.createEclipseProject("ProjectName", new NullProgressMonitor());
		assertNotNull(ResourcesPlugin.getWorkspace().getRoot().findMember("ProjectName"));
	}

	/**
	 * Test method for {@link org.jboss.tools.seam.internal.core.project.facet.WtpUtils#createEjbProject(java.lang.String, java.lang.String[])}.
	 */
	public void testCreateEjbProject() {
		WtpUtils.createDefaultEjbProject("ejbProject", new NullProgressMonitor());
		assertNotNull(ResourcesPlugin.getWorkspace().getRoot().findMember("ejbProject"));
	}

	/**
	 * Test method for {@link org.jboss.tools.seam.internal.core.project.facet.WtpUtils#createEarProject()}.
	 */
	public void testCreateEarProject() {
		fail("Not yet implemented");
	}

}

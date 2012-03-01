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
package org.jboss.tools.seam.core.test.project.facet;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import junit.framework.TestCase;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.jboss.tools.seam.core.SeamUtil;
import org.jboss.tools.seam.core.project.facet.SeamRuntime;
import org.jboss.tools.seam.core.project.facet.SeamRuntimeManager;
import org.jboss.tools.seam.core.project.facet.SeamVersion;
import org.osgi.framework.Bundle;

/**
 * @author eskimo
 *
 */
public class SeamRuntimeManagerTest extends TestCase {

	SeamRuntimeManager manager;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Bundle seamCoreTest = Platform.getBundle("org.jboss.tools.seam.core.test");
		URL seamUrl = FileLocator.resolve(seamCoreTest.getEntry("/seam/seam-1.2.0"));
		File folder = new File(seamUrl.getPath());
		manager = SeamRuntimeManager.getInstance();
		assertNotNull("Cannot obtainSeamRuntimeManager instance", manager);
		if(manager.findRuntimeByName("Seam 1.2.0")!=null) return;
		manager.addRuntime("Seam 1.2.0", folder.getAbsolutePath(), SeamVersion.SEAM_1_2, true);
	}

	/**
	 * Test method for {@link org.jboss.tools.seam.core.project.facet.SeamRuntimeManager#getRuntimes()}.
	 * @throws IOException 
	 */
	public void testGetRuntimes() throws IOException {
		SeamRuntime[] rtms = manager.getRuntimes();
		assertTrue("Seam runtime 'Seam 1.2.0' is not created", rtms.length==1);
		assertTrue("Seam runtime 'Seam 1.2.0' is not created", rtms[0].getName().equals("Seam 1.2.0"));
	}

	/**
	 * Test method for {@link org.jboss.tools.seam.core.project.facet.SeamRuntimeManager#getRuntimes(org.jboss.tools.seam.core.project.facet.SeamVersion)}.
	 */
	public void testGetRuntimesSeamVersion() {
		SeamRuntimeManager manager = SeamRuntimeManager.getInstance();
		SeamRuntime[] rtms = manager.getRuntimes(SeamVersion.SEAM_1_2);
		assertTrue("Error in obtaining seam runtimes list for Seam 1.2", rtms.length==1);
	}

	/**
	 * Test method for {@link org.jboss.tools.seam.core.project.facet.SeamRuntimeManager#addRuntime(org.jboss.tools.seam.core.project.facet.SeamRuntime)}.
	 * @throws IOException 
	 */
	public void testAddRuntimeSeamRuntime() throws IOException {
		Bundle seamCoreTest = Platform.getBundle("org.jboss.tools.seam.core.test");
		URL seamUrl = FileLocator.resolve(seamCoreTest.getEntry("/seam/seam-1.2.1"));
		File folder = new File(seamUrl.getPath());
		manager.addRuntime("Seam 1.2.1", folder.getAbsolutePath(), SeamVersion.SEAM_1_2, true);
	}

	/**
	 * Test method for {@link org.jboss.tools.seam.core.project.facet.SeamRuntimeManager#findRuntimeByName(java.lang.String)}.
	 */
	public void testFindRuntimeByName() {
		SeamRuntime srt = manager.findRuntimeByName("Seam 1.2.1");
		assertNotNull("Cannot find runtime 'Seam 1.2.1'",srt);
		srt = manager.findRuntimeByName("Seam 1.2.0");
		assertNotNull("Cannot find runtime 'Seam 1.2.0'",srt);
	}

	public void testMatchedRuntimes() {
		assertTrue(SeamUtil.areSeamVersionsMatched("2.1", "2.1.1.GA"));
		assertFalse(SeamUtil.areSeamVersionsMatched("1.2", "2.1.1.GA"));
		assertFalse(SeamUtil.areSeamVersionsMatched("2.0", "2.1.1.GA"));
		assertTrue(SeamUtil.areSeamVersionsMatched("2.0", "2.0.1.SP1"));
		assertTrue(SeamUtil.areSeamVersionsMatched("1.2", "1.2.1.GA"));
		assertTrue(SeamUtil.areSeamVersionsMatched("1.2", "1.2.0"));
		assertTrue(SeamUtil.areSeamVersionsMatched("2", "2.0.0"));
		assertFalse(SeamUtil.areSeamVersionsMatched("2.1", "2.0.0"));
	}

	/**
	 * Test method for {@link org.jboss.tools.seam.core.project.facet.SeamRuntimeManager#getDefaultRuntime()}.
	 */
	public void testGetDefaultRuntime() {
		assertNotNull("Cannot obtain default runtime 'Seam 1.2.0'",manager.getDefaultRuntime(SeamVersion.SEAM_1_2));
		assertNotNull("Cannot obtain default runtime 'Seam 1.2.0'",manager.getDefaultRuntime(SeamVersion.SEAM_1_2).getName().equals("Seam 1.2.0"));
	}
}
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
		manager = SeamRuntimeManager.getInstance();
		assertNotNull("Cannot obtainSeamRuntimeManager instance", manager);
	}

	/**
	 * Test method for {@link org.jboss.tools.seam.core.project.facet.SeamRuntimeManager#getRuntimes()}.
	 * @throws IOException 
	 */
	public void testGetRuntimes() throws IOException {
		Bundle seamCoreTest = Platform.getBundle("org.jboss.tools.seam.core.test");
		URL seamUrl = FileLocator.resolve(seamCoreTest.getEntry("/seam/seam-1.2.0"));
		File folder = new File(seamUrl.getPath());
		SeamRuntime runtime = null;
		try {
			runtime = manager.addRuntime("Seam 1.2.0", folder.getAbsolutePath(), SeamVersion.SEAM_1_2, true);
			SeamRuntime[] rts = manager.getRuntimes();
			assertTrue("Seam runtime 'Seam 1.2.0' is not created", rts.length>0);
		} finally {
			if(runtime!=null) {
				manager.removeRuntime(runtime);
			}
		}
	}

	/**
	 * Test method for {@link org.jboss.tools.seam.core.project.facet.SeamRuntimeManager#getRuntimes(org.jboss.tools.seam.core.project.facet.SeamVersion)}.
	 */
	public void testGetRuntimesSeamVersion() throws IOException {
		Bundle seamCoreTest = Platform.getBundle("org.jboss.tools.seam.core.test");
		URL seamUrl = FileLocator.resolve(seamCoreTest.getEntry("/seam/seam-1.2.0"));
		File folder = new File(seamUrl.getPath());
		SeamRuntime runtime = null;
		try {
			runtime = manager.addRuntime("Seam 1.2.0", folder.getAbsolutePath(), SeamVersion.SEAM_1_2, true);
			SeamRuntime[] rtms = manager.getRuntimes(SeamVersion.SEAM_1_2);
			assertEquals("Error in obtaining seam runtimes list for Seam 1.2", 1, rtms.length);
		} finally {
			if(runtime!=null) {
				manager.removeRuntime(runtime);
			}
		}
	}

	/**
	 * Test method for {@link org.jboss.tools.seam.core.project.facet.SeamRuntimeManager#addRuntime(org.jboss.tools.seam.core.project.facet.SeamRuntime)}.
	 * @throws IOException 
	 */
	public void testAddRuntimeSeamRuntime() throws IOException {
		Bundle seamCoreTest = Platform.getBundle("org.jboss.tools.seam.core.test");
		URL seamUrl = FileLocator.resolve(seamCoreTest.getEntry("/seam/seam-1.2.1"));
		File folder = new File(seamUrl.getPath());
		SeamRuntime runtime = null;
		try {
			runtime = manager.addRuntime("Seam 1.2.1", folder.getAbsolutePath(), SeamVersion.SEAM_1_2, true);
			assertNotNull(runtime);
		} finally {
			if(runtime!=null) {
				manager.removeRuntime(runtime);
			}
		}
	}

	/**
	 * Test method for {@link org.jboss.tools.seam.core.project.facet.SeamRuntimeManager#findRuntimeByName(java.lang.String)}.
	 */
	public void testFindRuntimeByName() throws IOException {
		Bundle seamCoreTest = Platform.getBundle("org.jboss.tools.seam.core.test");
		URL seamUrl = FileLocator.resolve(seamCoreTest.getEntry("/seam/seam-1.2.0"));
		File folder = new File(seamUrl.getPath());
		SeamRuntime runtime = null;
		try {
			manager.addRuntime("Seam 1.2.0", folder.getAbsolutePath(), SeamVersion.SEAM_1_2, true);
			runtime = manager.findRuntimeByName("Seam 1.2.0");
			assertNotNull("Can't find runtime 'Seam 1.2.0' is not created", runtime);
		} finally {
			if(runtime!=null) {
				manager.removeRuntime(runtime);
			}
		}
		seamUrl = FileLocator.resolve(seamCoreTest.getEntry("/seam/seam-1.2.1"));
		folder = new File(seamUrl.getPath());
		runtime = null;
		try {
			manager.addRuntime("Seam 1.2.1", folder.getAbsolutePath(), SeamVersion.SEAM_1_2, true);
			runtime = manager.findRuntimeByName("Seam 1.2.1");
			assertNotNull("Can't find runtime 'Seam 1.2.1' is not created", runtime);
		} finally {
			if(runtime!=null) {
				manager.removeRuntime(runtime);
			}
		}
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
	public void testGetDefaultRuntime() throws IOException {
		Bundle seamCoreTest = Platform.getBundle("org.jboss.tools.seam.core.test");
		URL seamUrl = FileLocator.resolve(seamCoreTest.getEntry("/seam/seam-1.2.0"));
		File folder = new File(seamUrl.getPath());
		SeamRuntime runtime = null;
		try {
			runtime = manager.addRuntime("Seam 1.2.0", folder.getAbsolutePath(), SeamVersion.SEAM_1_2, true);
			assertNotNull("Cannot obtain default runtime 'Seam 1.2.0'", manager.getDefaultRuntime(SeamVersion.SEAM_1_2));
		} finally {
			if(runtime!=null) {
				manager.removeRuntime(runtime);
			}
		}
	}
}
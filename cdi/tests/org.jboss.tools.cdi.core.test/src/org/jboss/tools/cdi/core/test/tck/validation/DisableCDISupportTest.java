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
package org.jboss.tools.cdi.core.test.tck.validation;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.jboss.tools.cdi.core.CDIUtil;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ResourcesUtils;

/**
 * @author Alexey Kazakov
 */
public class DisableCDISupportTest extends ValidationTest {
	private static final String CDI_CORE_VALIDATOR_PROBLEM_TYPE = "org.jboss.tools.cdi.core.cdiproblem";
	
	protected IProject tckProject;

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		tckProject = importPreparedProject();
	}

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
		deleteTestProject();
		JobUtils.waitForIdle();
		ResourcesUtils.setBuildAutomatically(saveAutoBuild);
	}

	/**
	 * https://issues.jboss.org/browse/JBIDE-9305
	 * @throws Exception
	 */
	public void testRemovingProblemMarkers() throws Exception {
		IMarker[] markers = tckProject.findMarkers(CDI_CORE_VALIDATOR_PROBLEM_TYPE, true, IResource.DEPTH_INFINITE);
		assertFalse(markers.length==0);
		CDIUtil.disableCDI(tckProject);
		markers = tckProject.findMarkers(CDI_CORE_VALIDATOR_PROBLEM_TYPE, true, IResource.DEPTH_INFINITE);
		assertEquals(0, markers.length);
	}
}
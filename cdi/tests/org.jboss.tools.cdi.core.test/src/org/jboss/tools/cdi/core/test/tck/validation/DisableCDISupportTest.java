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
import org.jboss.tools.cdi.core.test.tck.TCKTest;
import org.jboss.tools.cdi.internal.core.validation.CDICoreValidator;
import org.jboss.tools.common.base.test.validation.TestUtil;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ResourcesUtils;
import org.jboss.tools.tests.AbstractResourceMarkerTest;

/**
 * @author Alexey Kazakov
 */
public class DisableCDISupportTest extends ValidationTest {

	protected IProject tckProject;

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		tckProject = TCKTest.importPreparedProject("/");
		TestUtil._waitForValidation(tckProject);
	}

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
		tckProject.delete(true, true, null);
		JobUtils.waitForIdle();
		ResourcesUtils.setBuildAutomatically(saveAutoBuild);
	}

	/**
	 * https://issues.jboss.org/browse/JBIDE-9305
	 * @throws Exception
	 */
	public void testRemovingProblemMarkers() throws Exception {
		IMarker[] markers = tckProject.findMarkers(CDICoreValidator.PROBLEM_TYPE, true, IResource.DEPTH_INFINITE);
		assertFalse(markers.length==0);
		CDIUtil.disableCDI(tckProject);
		markers = tckProject.findMarkers(CDICoreValidator.PROBLEM_TYPE, true, IResource.DEPTH_INFINITE);
		assertEquals(0, markers.length);
	}
}
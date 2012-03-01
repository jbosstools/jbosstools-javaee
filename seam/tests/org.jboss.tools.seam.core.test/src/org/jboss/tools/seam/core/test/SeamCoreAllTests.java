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
package org.jboss.tools.seam.core.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.internal.core.JavaModelManager;
import org.eclipse.jst.jsp.core.internal.java.search.JSPSearchSupport;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.test.project.facet.SeamFacetOnExistingProjectTest;
import org.jboss.tools.seam.core.test.project.facet.SeamRuntimeListConverterTest;
import org.jboss.tools.seam.core.test.project.facet.SeamRuntimeManagerTest;
import org.jboss.tools.test.util.ProjectImportTestSetup;
import org.jboss.tools.test.util.ResourcesUtils;

/**
 * @author V.Kabanovich
 *
 */
public class SeamCoreAllTests {
	public static final String PLUGIN_ID = SeamCorePlugin.PLUGIN_ID;
	//
	public static Test suite() throws CoreException {
		// it can be done here because it is not needed to be enabled back
		JavaModelManager.getIndexManager().disable();
		JSPSearchSupport.getInstance().setCanceled(true);
		ResourcesUtils.setBuildAutomatically(false);

		TestSuite suite = new TestSuite();
		suite.setName("All tests for " + PLUGIN_ID);
		suite.addTest(new ProjectImportTestSetup(new TestSuite(ScannerTest.class),"org.jboss.tools.seam.core.test","projects/TestScanner","TestScanner"));
		suite.addTest(new ProjectImportTestSetup(new TestSuite(SeamModelStorageTest.class),"org.jboss.tools.seam.core.test","projects/TestStorage","TestStorage"));
		suite.addTestSuite(SerializationTest.class);
		suite.addTestSuite(SeamBigProjectTest.class);
		suite.addTestSuite(SeamEARTest.class);
		suite.addTestSuite(SeamRuntimeListConverterTest.class);
		suite.addTestSuite(SeamRuntimeManagerTest.class);
		suite.addTestSuite(CyclicDependingProjectsTest.class);
		suite.addTestSuite(ComponentsFromLibTest.class);
		suite.addTestSuite(Seam2ValidatorTest.class);
		suite.addTestSuite(SeamFacetOnExistingProjectTest.class);
		return suite;
	}
}
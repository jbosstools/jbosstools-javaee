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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.internal.core.JavaModelManager;
import org.eclipse.jst.jsp.core.internal.java.search.JSPSearchSupport;
import org.eclipse.wst.validation.ValidationFramework;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.test.util.JUnitUtils;
import org.jboss.tools.test.util.ProjectImportTestSetup;
import org.jboss.tools.test.util.ResourcesUtils;

/**
 * @author eskimo
 *
 */
public class SeamValidatorsAllTests {

	public static Test suite() throws CoreException {
		JavaModelManager.getIndexManager().disable();
		JSPSearchSupport.getInstance().setCanceled(true);
		ValidationFramework.getDefault().suspendAllValidation(true);
		ResourcesUtils.setBuildAutomatically(false);
		TestSuite suite = new TestSuite();
		suite.addTest(new ProjectImportTestSetup(new TestSuite(SeamValidatorsTest.class),"org.jboss.tools.seam.core.test","projects/SeamWebWarTestProject","SeamWebWarTestProject") {
			@Override
			protected void setUp() throws Exception {
				super.setUp();
				IProject project = (IProject)ResourcesPlugin.getWorkspace().getRoot().findMember("SeamWebWarTestProject");
				try {
					// Configure seam nature to switch off WTP JSF Variable resolver.
					((IProjectNature)SeamCorePlugin.getSeamProject((IProject)project, false)).configure();
				} catch (Exception e) {
					JUnitUtils.fail("Cannot configure seam nature.", e);
				}
				ResourcesUtils.setBuildAutomatically(false);
			}
			protected void tearDown() throws Exception {
				ResourcesUtils.setBuildAutomatically(true);
			};
		} );
		suite.addTest(new ProjectImportTestSetup(new TestSuite(SeamProjectPropertyValidatorTest.class),
				"org.jboss.tools.seam.core.test",
				new String[]{"projects/RefactoringTestProject-war", "projects/RefactoringTestProject-ejb", "projects/RefactoringTestProject-test"},
				new String[]{"RefactoringTestProject-war", "RefactoringTestProject-ejb", "RefactoringTestProject-test"}));
		return suite;
	}
}
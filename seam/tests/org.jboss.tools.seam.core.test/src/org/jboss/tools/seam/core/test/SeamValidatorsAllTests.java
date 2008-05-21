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

import org.jboss.tools.test.util.ProjectImportTestSetup;

/**
 * @author eskimo
 *
 */
public class SeamValidatorsAllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite();
		suite.addTest(new ProjectImportTestSetup(new TestSuite(SeamValidatorsTest.class),"org.jboss.tools.seam.core.test","projects/SeamWebWarTestProject","SeamWebWarTestProject"));
		suite.addTest(new ProjectImportTestSetup(new TestSuite(SeamProjectPropertyValidatorTest.class),
				"org.jboss.tools.seam.core.test",
				new String[]{"projects/RefactoringTestProject-war", "projects/RefactoringTestProject-ejb", "projects/RefactoringTestProject-test"},
				new String[]{"RefactoringTestProject-war", "RefactoringTestProject-ejb", "RefactoringTestProject-test"}));
		return suite;
	}
}
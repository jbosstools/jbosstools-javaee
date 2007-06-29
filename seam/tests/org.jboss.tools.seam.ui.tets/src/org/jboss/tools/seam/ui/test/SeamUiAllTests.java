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

package org.jboss.tools.seam.ui.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.jboss.tools.seam.ui.test.view.SeamComponentsViewTest;
import org.jboss.tools.seam.ui.test.wizard.SeamFormNewWizardTest;
import org.jboss.tools.seam.ui.test.wizard.SeamProjectNewWizardTest;

/**
 * @author eskimo
 *
 */
public class SeamUiAllTests {
	
	public static Test suite() {
		TestSuite suite = new TestSuite(SeamUiAllTests.class.getName());
		suite.addTestSuite(SeamComponentsViewTest.class);
		suite.addTestSuite(SeamProjectNewWizardTest.class);
		suite.addTestSuite(SeamFormNewWizardTest.class);
		suite.addTestSuite(SeamFormNewWizardTest.class);
		return suite;
	}
}

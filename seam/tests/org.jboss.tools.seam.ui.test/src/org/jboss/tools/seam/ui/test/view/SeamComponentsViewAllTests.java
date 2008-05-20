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
package org.jboss.tools.seam.ui.test.view;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author eskimo
 *
 */
public class SeamComponentsViewAllTests {
	public static Test suite() {
		TestSuite suite = new TestSuite("Seam Components View tests");
		suite.addTest(new SeamComponentsViewTestSetup(new TestSuite(SeamComponentsViewTest.class)));
		return suite;
	}
}

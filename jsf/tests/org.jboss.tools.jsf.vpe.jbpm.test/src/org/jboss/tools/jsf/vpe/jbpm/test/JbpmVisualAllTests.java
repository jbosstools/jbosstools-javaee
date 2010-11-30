/******************************************************************************* 
 * Copyright (c) 2007-2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.jbpm.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.jboss.tools.vpe.base.test.VpeTestSetup;

/**
 * 
 * @author yzhishko
 *
 */

public class JbpmVisualAllTests {

	public static final String JBPM_TEST_PROJECT = "jBPMTestProject"; //$NON-NLS-1$
	
	public static Test suite(){
		TestSuite suite = new TestSuite("Tests for Vpe jBPM components"); //$NON-NLS-1$
		suite.addTestSuite(JBPMComponentsTest.class);
		return new VpeTestSetup(suite);
	}
	
}

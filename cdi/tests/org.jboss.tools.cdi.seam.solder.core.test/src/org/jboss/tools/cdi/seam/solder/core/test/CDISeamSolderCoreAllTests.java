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
package org.jboss.tools.cdi.seam.solder.core.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.jdt.internal.core.JavaModelManager;
/**
 * @author Viacheslav Kabanovich
 */
public class CDISeamSolderCoreAllTests {

	public static Test suite() {
		// it could be done here because it is not needed to be enabled back
		JavaModelManager.getIndexManager().disable();

		TestSuite suiteAll = new TestSuite("CDI Solder Core Tests");

		suiteAll.addTestSuite(BeanNamingTest.class);
		suiteAll.addTestSuite(VetoTest.class);
		suiteAll.addTestSuite(ExactTest.class);
		suiteAll.addTestSuite(MessageLoggerTest.class);

		SeamSolderTestSetup suite = new SeamSolderTestSetup(suiteAll);

		return suite;
	}
}

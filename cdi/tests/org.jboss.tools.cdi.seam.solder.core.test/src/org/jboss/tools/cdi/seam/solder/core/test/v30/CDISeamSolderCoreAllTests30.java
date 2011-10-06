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
package org.jboss.tools.cdi.seam.solder.core.test.v30;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Viacheslav Kabanovich
 */
public class CDISeamSolderCoreAllTests30 {

	public static Test suite() {
		TestSuite suiteAll = new TestSuite("CDI Solder Core 3.0 Tests");
		SeamSolderTestSetup suite = new SeamSolderTestSetup(suiteAll);

		suiteAll.addTestSuite(GenericBeanTest.class);
		suiteAll.addTestSuite(GenericBeanValidationTest.class);
		suiteAll.addTestSuite(BeanNamingTest.class);
		suiteAll.addTestSuite(VetoTest.class);
		suiteAll.addTestSuite(ExactTest.class);
		suiteAll.addTestSuite(MessageLoggerTest.class);
		suiteAll.addTestSuite(ServiceHandlerTest.class);
		suiteAll.addTestSuite(DefaultBeanTest.class);
		suiteAll.addTestSuite(UnwrapsTest.class);

		return suite;
	}
}
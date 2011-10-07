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
package org.jboss.tools.cdi.seam.config.core.test.v30;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.jdt.internal.core.JavaModelManager;
import org.eclipse.wst.validation.ValidationFramework;
import org.jboss.tools.common.base.test.validation.ValidationExceptionTest;

/**
 * @author Viacheslav Kabanovich
 */
public class CDISeamConfigCoreAllTests30 {

	public static Test suite() {
		TestSuite suiteAll = new TestSuite("CDI Config Core 3.0 Tests");

		TestSuite suiteCore = new TestSuite("CDI Config Model Tests");
		suiteCore.addTestSuite(ExtensionTest.class);
		suiteCore.addTestSuite(SeamDefinitionsTest.class);
		suiteCore.addTestSuite(SeamBeansTest.class);
		suiteCore.addTestSuite(ConfigBeansInjectionTest.class);
		suiteAll.addTest(new SeamConfigTestSetup(suiteCore));

		TestSuite suiteValidation = new TestSuite("CDI Config Validation Tests");
		suiteValidation.addTestSuite(SeamConfigValidationTest.class);
		suiteAll.addTest(new SeamConfigValidationTestSetup(suiteValidation));

		return suiteAll;
	}
}
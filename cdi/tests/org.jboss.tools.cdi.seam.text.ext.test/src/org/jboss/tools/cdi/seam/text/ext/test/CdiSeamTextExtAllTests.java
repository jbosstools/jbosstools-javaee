/******************************************************************************* 
 * Copyright (c) 2009 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.seam.text.ext.test;

import org.eclipse.jdt.internal.core.JavaModelManager;
import org.jboss.tools.cdi.core.test.CDICoreTestSetup;
import org.jboss.tools.cdi.seam.config.core.test.SeamConfigTestSetup;
import org.jboss.tools.cdi.seam.solder.core.test.SeamSolderTestSetup;

import junit.framework.Test;
import junit.framework.TestSuite;

public class CdiSeamTextExtAllTests {
	public static Test suite() {
		// it could be done here because it is not needed to be enabled back
		JavaModelManager.getIndexManager().disable();

		TestSuite suiteAll = new TestSuite("CDI Core Tests");

		TestSuite suite = new TestSuite("CDI Seam OpenOns Tests");
		suite.addTestSuite(CDISeamResourceLoadingHyperlinkDetectorTest.class);
		
		suiteAll.addTest(new CDICoreTestSetup(suite));

		TestSuite suiteConfig = new TestSuite("CDI Seam Config OpenOns Tests");
		suiteConfig.addTestSuite(SeamConfigTagNameHyperlinkTest.class);
		suiteConfig.addTestSuite(SeamConfigInjectedPointHyperlinkTest.class);
		
		suiteAll.addTest(new SeamConfigTestSetup(suiteConfig));
		
		TestSuite suiteSolder = new TestSuite("CDI Seam Solder Tests");
		suiteSolder.addTestSuite(InjectedPointHyperlinkTest.class);
		suiteSolder.addTestSuite(SeamGenericInjectedPointHyperlinkTest.class);
		
		suiteAll.addTest(new SeamSolderTestSetup(suiteSolder));
		
		return suiteAll;
	}
}
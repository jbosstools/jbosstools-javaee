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
package org.jboss.tools.cdi.ui.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.jdt.internal.core.JavaModelManager;
import org.eclipse.jst.jsp.core.internal.java.search.JSPIndexManager;
import org.jboss.tools.cdi.core.test.CDICoreTestSetup;
import org.jboss.tools.cdi.ui.test.marker.CDIMarkerResolutionTest;
import org.jboss.tools.cdi.ui.test.perspective.CDIPerspectiveTest;
import org.jboss.tools.cdi.ui.test.preferences.CDIPreferencePageTest;
import org.jboss.tools.cdi.ui.test.search.CDISearchParticipantTest;
import org.jboss.tools.cdi.ui.test.wizard.AddQualifiersToBeanWizardTest;
import org.jboss.tools.cdi.ui.test.wizard.NewCDIClassWizardFactoryTest;
import org.jboss.tools.cdi.ui.test.wizard.NewCDIWizardTest;

/**
 * @author Alexey Kazakov
 */
public class CDIUIAllTests {

	public static Test suite() {
		// it could be done here because it is not needed to be enabled back
		JavaModelManager.getIndexManager().shutdown();
		try {
			JSPIndexManager.getDefault().stop();
		} catch (InterruptedException e) {
			// print it and ignore it 
			e.printStackTrace();
		}
		
		TestSuite suiteAll = new TestSuite("CDI UI Tests");
		
		TestSuite suite = new TestSuite("TCK Tests");
		suite.addTestSuite(CDISearchParticipantTest.class);
		suiteAll.addTestSuite(CDIMarkerResolutionTest.class);
		
		
		suiteAll.addTestSuite(CDIPerspectiveTest.class);
		suiteAll.addTestSuite(NewCDIClassWizardFactoryTest.class);
		suiteAll.addTestSuite(CDIPreferencePageTest.class);
		suiteAll.addTestSuite(NewCDIWizardTest.class);
		suiteAll.addTestSuite(CATest.class);
		
		suiteAll.addTest(new CDICoreTestSetup(suite));
		
		suiteAll.addTestSuite(AddQualifiersToBeanWizardTest.class);

		return suiteAll;
	}
}
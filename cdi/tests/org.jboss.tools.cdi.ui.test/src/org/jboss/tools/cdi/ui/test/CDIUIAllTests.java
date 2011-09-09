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

import org.apache.xerces.impl.validation.ValidationManager;
import org.eclipse.core.internal.jobs.JobManager;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.core.JavaModelManager;
import org.eclipse.jst.jsp.core.internal.contentmodel.TaglibController;
import org.eclipse.jst.jsp.core.internal.contentproperties.JSPFContentPropertiesManager;
import org.eclipse.jst.jsp.core.internal.java.search.JSPIndexManager;
import org.eclipse.jst.jsp.core.internal.taglib.TaglibHelperManager;
import org.eclipse.jst.jsp.core.taglib.TaglibIndex;
import org.eclipse.wst.sse.core.internal.validate.ValidationMessage;
import org.eclipse.wst.validation.ValidationFramework;
import org.jboss.tools.cdi.core.test.CDICoreTestSetup;
import org.jboss.tools.cdi.core.test.tck.TCKTest;
import org.jboss.tools.cdi.ui.test.marker.CDIMarkerResolutionTest;
import org.jboss.tools.cdi.ui.test.perspective.CDIPerspectiveTest;
import org.jboss.tools.cdi.ui.test.preferences.CDIPreferencePageTest;
import org.jboss.tools.cdi.ui.test.search.CDISearchParticipantTest;
import org.jboss.tools.cdi.ui.test.search.ELReferencesQueryParticipantTest;
import org.jboss.tools.cdi.ui.test.wizard.AddQualifiersToBeanWizardTest;
import org.jboss.tools.cdi.ui.test.wizard.NewCDIClassWizardFactoryTest;
import org.jboss.tools.cdi.ui.test.wizard.NewCDIWizardTest;
import org.jboss.tools.test.util.ResourcesUtils;
import org.jboss.tools.test.util.WorkbenchUtils;

/**
 * @author Alexey Kazakov
 */
public class CDIUIAllTests {

	public static Test suite() {
		// it could be done here because it is not needed to be enabled back
		JavaModelManager.getIndexManager().shutdown();
		try {
			ResourcesUtils.setBuildAutomatically(false);
			ValidationFramework.getDefault().suspendAllValidation(true);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		TestSuite suiteAll = new TestSuite("CDI UI Tests");
		TestSuite suite = new TestSuite("TCK Tests");

		suiteAll.addTestSuite(CDIMarkerResolutionTest.class);

		suiteAll.addTestSuite(CDIPerspectiveTest.class);
		suiteAll.addTestSuite(NewCDIClassWizardFactoryTest.class);
		suiteAll.addTestSuite(CDIPreferencePageTest.class);
		suiteAll.addTestSuite(NewCDIWizardTest.class);
		suiteAll.addTestSuite(CATest.class);
		suiteAll.addTestSuite(CAELProposalFilteringTest.class);

		suite.addTestSuite(CDISearchParticipantTest.class);
		suite.addTestSuite(ELReferencesQueryParticipantTest.class);
		suiteAll.addTest(new CDICoreTestSetup(suite) {
			@Override
			protected void setUp() throws Exception {
				tckProject = TCKUITest.importPreparedProject("/");
			}
			protected void tearDown() throws Exception {
				tckProject.delete(true, true, null);
			}
		}
		);

		suiteAll.addTestSuite(AddQualifiersToBeanWizardTest.class);

		return suiteAll;
	}
}
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
package org.jboss.tools.cdi.core.test.tck.validation;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.jboss.tools.cdi.internal.core.validation.CDIValidationMessages;
import org.jboss.tools.jst.jsp.test.TestUtil;
import org.jboss.tools.jst.web.kb.internal.validation.ValidatorManager;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ResourcesUtils;
import org.jboss.tools.tests.AbstractResourceMarkerTest;

/**
 * @author Alexey Kazakov
 */
public class IncrementalValidationTest extends ValidationTest {

	/**
	 * See https://issues.jboss.org/browse/JBIDE-8325
	 * @throws Exception
	 */
	public void testInjectionPointRevalidation() throws Exception {
		boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
		JobUtils.waitForIdle();

		IFile testInjection = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/inject/revalidation/TestBeanBroken.java");
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(testInjection, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS, 7);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(testInjection, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS, 7);

		IFile testBean = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/inject/revalidation/TestBeanImpl2.java");
		IFile testBeanImpl = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/inject/revalidation/TestBeanImpl2.validation");
		testBean.setContents(testBeanImpl.getContents(), IFile.FORCE, new NullProgressMonitor());
		JobUtils.waitForIdle(1000);
		tckProject.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, new NullProgressMonitor());
		JobUtils.waitForIdle(1000);

		AbstractResourceMarkerTest.assertMarkerIsCreated(testInjection, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS, 7);

		testBeanImpl = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/inject/revalidation/TestBeanImpl2.java");
		testBean = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/inject/revalidation/TestBeanImpl2Original.validation");
		testBeanImpl.setContents(testBean.getContents(), IFile.FORCE, new NullProgressMonitor());
		JobUtils.waitForIdle(1000);
		tckProject.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, new NullProgressMonitor());
		JobUtils.waitForIdle(1000);

		AbstractResourceMarkerTest.assertMarkerIsNotCreated(testInjection, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS, 7);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(testInjection, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS, 7);

		ResourcesUtils.setBuildAutomatically(saveAutoBuild);
		JobUtils.waitForIdle();
	}

	/**
	 * See https://issues.jboss.org/browse/JBIDE-9071
	 * @throws Exception
	 */
	public void testInjectionPointResolvedToProducerRevalidation() throws Exception {
		boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
		JobUtils.waitForIdle();

		IFile testInjection = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/inject/revalidation/TestBeanForProducerBroken.java");
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(testInjection, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS, 7);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(testInjection, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS, 7);

		IFile testBean = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/inject/revalidation/MarketPlace.java");
		IFile testBeanImpl = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/inject/revalidation/MarketPlace.validation");
		ValidatorManager.setStatus("TESTING");
		testBean.setContents(testBeanImpl.getContents(), IFile.FORCE, new NullProgressMonitor());
		tckProject.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, new NullProgressMonitor());
		TestUtil.waitForValidation();

		AbstractResourceMarkerTest.assertMarkerIsCreated(testInjection, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS, 7);

		testBeanImpl = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/inject/revalidation/MarketPlace.java");
		testBean = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/inject/revalidation/MarketPlaceOriginal.validation");
		ValidatorManager.setStatus("TESTING");
		testBeanImpl.setContents(testBean.getContents(), IFile.FORCE, new NullProgressMonitor());
		tckProject.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, new NullProgressMonitor());
		TestUtil.waitForValidation();

		AbstractResourceMarkerTest.assertMarkerIsNotCreated(testInjection, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS, 7);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(testInjection, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS, 7);

		ResourcesUtils.setBuildAutomatically(saveAutoBuild);
		JobUtils.waitForIdle();
	}

	/**
	 * See https://issues.jboss.org/browse/JBIDE-9306
	 * @throws Exception
	 */
	public void testAlternativesInBeansXml() throws Exception {
		boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
		JobUtils.waitForIdle();

		try {
			IFile bean = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/beansxml/incremental/Test3.java");
			AbstractResourceMarkerTest.assertMarkerIsNotCreated(bean, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS, 8);

			IFile beansXml = tckProject.getFile("JavaSource/META-INF/beans.xml");
			IFile emptyBeansXml = tckProject.getFile("JavaSource/META-INF/beans.xml.empty");

			ValidatorManager.setStatus("TESTING ALTERNATIVES 1");
			beansXml.setContents(emptyBeansXml.getContents(), IFile.FORCE, new NullProgressMonitor());
			tckProject.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, new NullProgressMonitor());
			TestUtil.waitForValidation();
			AbstractResourceMarkerTest.assertMarkerIsCreated(bean, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS, 8);

			IFile beansXmlWithAlternative = tckProject.getFile("JavaSource/META-INF/beans.xml.with.alternative");

			ValidatorManager.setStatus("TESTING ALTERNATIVES 2");
			beansXml.setContents(beansXmlWithAlternative.getContents(), IFile.FORCE, new NullProgressMonitor());
			tckProject.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, new NullProgressMonitor());
			TestUtil.waitForValidation();
	
			AbstractResourceMarkerTest.assertMarkerIsNotCreated(bean, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS, 8);
		} finally {
			IFile beansXml = tckProject.getFile("JavaSource/META-INF/beans.xml");
			IFile beansXmlWithAlternative = tckProject.getFile("JavaSource/META-INF/beans.xml.with.alternative");
			beansXml.setContents(beansXmlWithAlternative.getContents(), IFile.FORCE, new NullProgressMonitor());
			ResourcesUtils.setBuildAutomatically(saveAutoBuild);
			JobUtils.waitForIdle();
		}
	}
}
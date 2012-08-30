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
import org.eclipse.core.runtime.NullProgressMonitor;
import org.jboss.tools.cdi.internal.core.validation.CDIValidationMessages;
import org.jboss.tools.common.base.test.validation.TestUtil;
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

		IFile testInjection = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/inject/revalidation/TestBeanBroken.java");
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(testInjection, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS, 7);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(testInjection, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS, 7);

		IFile testBean = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/inject/revalidation/TestBeanImpl2.java");
		IFile testBeanImpl = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/inject/revalidation/TestBeanImpl2.validation");
		testBean.setContents(testBeanImpl.getContents(), IFile.FORCE, new NullProgressMonitor());

		TestUtil.validate(testBean);

		AbstractResourceMarkerTest.assertMarkerIsCreated(testInjection, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS, 7);

		testBeanImpl = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/inject/revalidation/TestBeanImpl2.java");
		testBean = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/inject/revalidation/TestBeanImpl2Original.validation");
		testBeanImpl.setContents(testBean.getContents(), IFile.FORCE, new NullProgressMonitor());
		TestUtil.validate(testBeanImpl);

		AbstractResourceMarkerTest.assertMarkerIsNotCreated(testInjection, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS, 7);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(testInjection, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS, 7);

		ResourcesUtils.setBuildAutomatically(saveAutoBuild);
	}

	/**
	 * See https://issues.jboss.org/browse/JBIDE-9071
	 * @throws Exception
	 */
	public void testInjectionPointResolvedToProducerRevalidation() throws Exception {
		boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);

		IFile testInjection = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/inject/revalidation/TestBeanForProducerBroken.java");
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(testInjection, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS, 7);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(testInjection, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS, 7);

		IFile testBean = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/inject/revalidation/MarketPlace.java");
		IFile testBeanImpl = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/inject/revalidation/MarketPlace.validation");

		testBean.setContents(testBeanImpl.getContents(), IFile.FORCE, new NullProgressMonitor());
		TestUtil.validate(testBean);

		AbstractResourceMarkerTest.assertMarkerIsCreated(testInjection, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS, 7);

		testBeanImpl = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/inject/revalidation/MarketPlace.java");
		testBean = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/inject/revalidation/MarketPlaceOriginal.validation");

		testBeanImpl.setContents(testBean.getContents(), IFile.FORCE, new NullProgressMonitor());
		TestUtil.validate(testBeanImpl);

		AbstractResourceMarkerTest.assertMarkerIsNotCreated(testInjection, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS, 7);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(testInjection, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS, 7);

		ResourcesUtils.setBuildAutomatically(saveAutoBuild);
	}

	/**
	 * See https://issues.jboss.org/browse/JBIDE-9306
	 * @throws Exception
	 */
	public void testAlternativesInBeansXml() throws Exception {
		boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);

		try {
			IFile bean = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/beansxml/incremental/Test3.java");
			AbstractResourceMarkerTest.assertMarkerIsNotCreated(bean, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS, 8);

			IFile beansXml = tckProject.getFile("JavaSource/META-INF/beans.xml");
			IFile emptyBeansXml = tckProject.getFile("JavaSource/META-INF/beans.xml.empty");

			beansXml.setContents(emptyBeansXml.getContents(), IFile.FORCE, new NullProgressMonitor());
			TestUtil.validate(beansXml);
			AbstractResourceMarkerTest.assertMarkerIsCreated(bean, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS, 8);

			IFile beansXmlWithAlternative = tckProject.getFile("JavaSource/META-INF/beans.xml.with.alternative");

			beansXml.setContents(beansXmlWithAlternative.getContents(), IFile.FORCE, new NullProgressMonitor());
			TestUtil.validate(beansXml);
	
			AbstractResourceMarkerTest.assertMarkerIsNotCreated(bean, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS, 8);
		} finally {
			IFile beansXml = tckProject.getFile("JavaSource/META-INF/beans.xml");
			IFile beansXmlWithAlternative = tckProject.getFile("JavaSource/META-INF/beans.xml.with.alternative");
			beansXml.setContents(beansXmlWithAlternative.getContents(), IFile.FORCE, new NullProgressMonitor());
			TestUtil.validate(beansXml);
			ResourcesUtils.setBuildAutomatically(saveAutoBuild);
		}
	}

	public void testRestrictedType() throws Exception {
		boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
		try {
			IFile bean = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/typed/NewBean.java");
			AbstractResourceMarkerTest.assertMarkerIsCreated(bean, CDIValidationMessages.ILLEGAL_TYPE_IN_TYPED_DECLARATION, 5);
			IFile interfaceFile = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/typed/LocalExtendedBean.java");
			IFile modifiedFile = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/typed/LocalExtendedBean.changed");
			interfaceFile.setContents(modifiedFile.getContents(), IFile.FORCE, new NullProgressMonitor());
			TestUtil.validate(bean);
			AbstractResourceMarkerTest.assertMarkerIsNotCreated(bean, CDIValidationMessages.ILLEGAL_TYPE_IN_TYPED_DECLARATION, 5);

			IFile originalFile = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/typed/LocalExtendedBean.original");
			interfaceFile.setContents(originalFile.getContents(), IFile.FORCE, new NullProgressMonitor());
			TestUtil.validate(bean);
			AbstractResourceMarkerTest.assertMarkerIsCreated(bean, CDIValidationMessages.ILLEGAL_TYPE_IN_TYPED_DECLARATION, 5);
		} finally {
			ResourcesUtils.setBuildAutomatically(saveAutoBuild);
		}
	}

	public void testInjections() throws Exception {
		boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
		try {
			IFile bean = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/inject/incremental/ExtensionManager.java");
			AbstractResourceMarkerTest.assertMarkerIsNotCreated(bean, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS, 7);
			IFile interfaceFile = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/inject/incremental/IExtension.java");
			IFile modifiedFile = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/inject/incremental/IExtension.broken");
			interfaceFile.setContents(modifiedFile.getContents(), IFile.FORCE, new NullProgressMonitor());
			TestUtil.validate(bean);
			AbstractResourceMarkerTest.assertMarkerIsCreated(bean, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS, 7);

			IFile originalFile = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/inject/incremental/IExtension.original");
			interfaceFile.setContents(originalFile.getContents(), IFile.FORCE, new NullProgressMonitor());
			TestUtil.validate(bean);
			AbstractResourceMarkerTest.assertMarkerIsNotCreated(bean, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS, 7);
		} finally {
			ResourcesUtils.setBuildAutomatically(saveAutoBuild);
		}
	}
}
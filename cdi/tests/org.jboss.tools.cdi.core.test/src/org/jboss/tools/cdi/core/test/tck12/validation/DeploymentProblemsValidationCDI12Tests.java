/******************************************************************************* 
 * Copyright (c) 2014 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/

package org.jboss.tools.cdi.core.test.tck12.validation;

import java.text.MessageFormat;

import org.eclipse.core.resources.IFile;
import org.jboss.tools.cdi.core.test.tck.ITCKProjectNameProvider;
import org.jboss.tools.cdi.core.test.tck.validation.DeploymentProblemsValidationTests;
import org.jboss.tools.cdi.core.test.tck12.TCK12ProjectNameProvider;
import org.jboss.tools.cdi.internal.core.validation.CDIValidationMessages;

/**
 * @author Alexey Kazakov
 */
public class DeploymentProblemsValidationCDI12Tests extends DeploymentProblemsValidationTests {

	@Override
	public ITCKProjectNameProvider getProjectNameProvider() {
		return new TCK12ProjectNameProvider();
	}

	/**
	 * The defined in CDI 1.0 '5.2.4. Primitive types and null values' 
	 * prohibition for an injection point of primitive type to be resolved to 
	 * a bean that may have null values is removed from CDI 1.1 (container lets 
	 * an injection point to have default value when resolved bean returns null 
	 * value).
	 * 
	 * removed from CDI 1.1.
	 */
	@Override
	public void testPrimitiveInjectionPointResolvedToNonPrimitiveProducerMethod() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/inject/GameBroken.java");
		getAnnotationTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.INJECT_RESOLVES_TO_NULLABLE_BEAN[getVersionIndex()], 7);
		getAnnotationTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.INJECT_RESOLVES_TO_NULLABLE_BEAN[getVersionIndex()], 19);
		getAnnotationTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.INJECT_RESOLVES_TO_NULLABLE_BEAN[getVersionIndex()], 9);
		getAnnotationTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.INJECT_RESOLVES_TO_NULLABLE_BEAN[getVersionIndex()], 10);
		getAnnotationTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.INJECT_RESOLVES_TO_NULLABLE_BEAN[getVersionIndex()], 11);
		getAnnotationTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.INJECT_RESOLVES_TO_NULLABLE_BEAN[getVersionIndex()], 20);
		getAnnotationTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.INJECT_RESOLVES_TO_NULLABLE_BEAN[getVersionIndex()], 21);
		getAnnotationTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.INJECT_RESOLVES_TO_NULLABLE_BEAN[getVersionIndex()], 22);
	}

	/**
	 * 	The defined in CDI 1.0 '5.4.1. Unproxyable bean types'
	 *  prohibition for a proxyable bean to have final methods is relaxed to
	 *  non-static, final methods with public, protected or default visibility
	 *  since CDI 1.1
	 * 
	 * @throws Exception
	 */
	public void testClassWithPrivateFinalMethodCannotBeProxied() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/lookup/clientProxy/unproxyable/privateFinalMethod/FishFarm.java");
		getAnnotationTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.UNPROXYABLE_BEAN_TYPE_WITH_FM[getVersionIndex()], 23);
		getAnnotationTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.UNPROXYABLE_BEAN_TYPE_WITH_FM[getVersionIndex()].substring(0, 0) + ".*", 25);

		file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/lookup/clientProxy/unproxyable/privateFinalMethod/Tuna_Broken.java");
		getAnnotationTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.UNPROXYABLE_BEAN_TYPE_WITH_FM_2[getVersionIndex()], 21);
	}

	/**
	 * 	The defined in CDI 1.0 '5.4.1. Unproxyable bean types'
	 *  prohibition for a proxyable bean to have final methods is relaxed to
	 *  non-static, final methods with public, protected or default visibility
	 *  since CDI 1.1
	 * 
	 * @throws Exception
	 */
	public void testClassWithStaticFinalMethodCannotBeProxied() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/lookup/clientProxy/unproxyable/staticFinalMethod/FishFarm.java");
		getAnnotationTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.UNPROXYABLE_BEAN_TYPE_WITH_FM[getVersionIndex()], 23);
		getAnnotationTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.UNPROXYABLE_BEAN_TYPE_WITH_FM[getVersionIndex()].substring(0, 0) + ".*", 25);

		file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/lookup/clientProxy/unproxyable/staticFinalMethod/Tuna_Broken.java");
		getAnnotationTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.UNPROXYABLE_BEAN_TYPE_WITH_FM_2[getVersionIndex()], 21);
	}

}
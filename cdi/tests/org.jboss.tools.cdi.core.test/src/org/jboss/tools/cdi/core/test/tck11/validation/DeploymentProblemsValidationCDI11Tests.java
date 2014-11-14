/******************************************************************************* 
 * Copyright (c) 2013 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/

package org.jboss.tools.cdi.core.test.tck11.validation;

import org.eclipse.core.resources.IFile;
import org.jboss.tools.cdi.core.test.tck.ITCKProjectNameProvider;
import org.jboss.tools.cdi.core.test.tck.validation.DeploymentProblemsValidationTests;
import org.jboss.tools.cdi.core.test.tck11.TCK11ProjectNameProvider;
import org.jboss.tools.cdi.internal.core.validation.CDIValidationMessages;

/**
 * @author Alexey Kazakov
 */
public class DeploymentProblemsValidationCDI11Tests extends DeploymentProblemsValidationTests {

	@Override
	public ITCKProjectNameProvider getProjectNameProvider() {
		return new TCK11ProjectNameProvider();
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

}
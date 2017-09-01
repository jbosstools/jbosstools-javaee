/******************************************************************************* 
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.core.test.tck20.validation;

import org.eclipse.core.resources.IFile;
import org.jboss.tools.cdi.core.test.tck.ITCKProjectNameProvider;
import org.jboss.tools.cdi.core.test.tck.validation.DefenitionErrorsValidationTest;
import org.jboss.tools.cdi.core.test.tck20.TCK20ProjectNameProvider;
import org.jboss.tools.cdi.internal.core.validation.CDIValidationMessages;


public class DefenitionErrorsValidationCDI20Test extends DefenitionErrorsValidationTest {

	@Override
	public ITCKProjectNameProvider getProjectNameProvider() {
		return new TCK20ProjectNameProvider();
	}

	/**
	 * 3.3. Producer methods
	 *  - producer method return type is a type variable
	 * 
	 * 2.2.1 - Legal bean types
	 *  - a type variable is not a legal bean type
	 *  
	 * @throws Exception
	 */
	public void testParameterizedType() throws Exception {
		//TODO change message for array of variable
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/implementation/producer/method/broken/parameterizedTypeWithTypeParameter2/TProducer.java");
		getAnnotationTest().assertAnnotationIsCreated(file, CDIValidationMessages.PRODUCER_METHOD_RETURN_TYPE_IS_VARIABLE[getVersionIndex()], 25);
		file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/producers/SpiderProducerVariableType_Broken.java");
		getAnnotationTest().assertAnnotationIsCreated(file, CDIValidationMessages.PRODUCER_METHOD_RETURN_TYPE_IS_VARIABLE[getVersionIndex()], 13);
	}

	/**
	 * 3.5.3. Disposer method resolution
	 *  - there are multiple disposer methods for a single producer method or producer field
	 *  
	 * @throws Exception
	 */
	public void testMultipleDisposersForProducerField() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/disposers/TimestampLogger_Broken2.java");
		getAnnotationTest().assertAnnotationIsCreated(file, CDIValidationMessages.MULTIPLE_DISPOSERS_FOR_PRODUCER[getVersionIndex()], 11, 14);
	}

	/**
	 * 3.5.3. Disposer method resolution
	 *  - there is no producer method or producer field declared by the (same) bean class that is assignable to the disposed parameter of a disposer method
	 *  
	 * @throws Exception
	 */
	public void testUnresolvedDisposalMethod2() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/implementation/disposal/method/definition/broken/unresolvedMethod/FlyProducer_Broken.java");
		getAnnotationTest().assertAnnotationIsCreated(file, CDIValidationMessages.NO_PRODUCER_MATCHING_DISPOSER[getVersionIndex()], 33);
		getAnnotationTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.NO_PRODUCER_MATCHING_DISPOSER[getVersionIndex()], 29);
	}

}
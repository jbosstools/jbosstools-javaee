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

import org.eclipse.core.resources.IFile;
import org.jboss.tools.cdi.core.test.tck.ITCKProjectNameProvider;
import org.jboss.tools.cdi.core.test.tck.validation.DefenitionErrorsValidationTest;
import org.jboss.tools.cdi.core.test.tck12.TCK12ProjectNameProvider;
import org.jboss.tools.cdi.internal.core.validation.CDIValidationMessages;

/**
 * @author Alexey Kazakov
 */
public class DefenitionErrorsValidationCDI12Test extends DefenitionErrorsValidationTest {

	@Override
	public ITCKProjectNameProvider getProjectNameProvider() {
		return new TCK12ProjectNameProvider();
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
		getAnnotationTest().assertAnnotationIsCreated(file, CDIValidationMessages.PRODUCER_METHOD_RETURN_TYPE_IS_VARIABLE[getVersionIndex()], 25, 31);
		file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/producers/SpiderProducerVariableType_Broken.java");
		getAnnotationTest().assertAnnotationIsCreated(file, CDIValidationMessages.PRODUCER_METHOD_RETURN_TYPE_IS_VARIABLE[getVersionIndex()], 13);
	}
}
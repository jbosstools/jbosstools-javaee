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
import org.jboss.tools.cdi.core.test.tck.validation.DefenitionErrorsValidationTest;
import org.jboss.tools.cdi.core.test.tck11.TCK11ProjectNameProvider;
import org.jboss.tools.cdi.internal.core.validation.CDIValidationMessages;

/**
 * @author Alexey Kazakov
 */
public class DefenitionErrorsValidationCDI11Test extends DefenitionErrorsValidationTest {

	@Override
	public ITCKProjectNameProvider getProjectNameProvider() {
		return new TCK11ProjectNameProvider();
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
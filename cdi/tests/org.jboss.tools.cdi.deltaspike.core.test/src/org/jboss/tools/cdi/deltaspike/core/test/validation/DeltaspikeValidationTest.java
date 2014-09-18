/******************************************************************************* 
 * Copyright (c) 2012 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.deltaspike.core.test.validation;

import org.eclipse.core.resources.IFile;
import org.eclipse.osgi.util.NLS;
import org.jboss.tools.cdi.deltaspike.core.test.DeltaspikeCoreTest;
import org.jboss.tools.cdi.deltaspike.core.validation.DeltaspikeValidationMessages;

/**
 * @author Alexey Kazakov
 */
public class DeltaspikeValidationTest extends DeltaspikeCoreTest {

	public void testHandlerValidation() throws Exception {
		IFile file = getTestProject().getFile("src/deltaspike/handler/MyHandlers.java"); //$NON-NLS-1$

		getAnnotationTest().assertAnnotationIsNotCreated(file, DeltaspikeValidationMessages.INVALID_HANDLER_TYPE, 15);
		getAnnotationTest().assertAnnotationIsCreated(file, DeltaspikeValidationMessages.INVALID_HANDLER_TYPE, 18);
	}

	public void testSecurityValidation() throws Exception {
		IFile file = getTestProject().getFile("src/deltaspike/security/CustomAuthorizer.java"); //$NON-NLS-1$

		String message = NLS.bind(DeltaspikeValidationMessages.INVALID_AUTHORIZER_NOT_BOOLEAN, "check3"); //$NON-NLS-1$
		getAnnotationTest().assertAnnotationIsCreated(file, message, 27);

		message = NLS.bind(DeltaspikeValidationMessages.INVALID_AUTHORIZER_NO_BINDINGS, "check4"); //$NON-NLS-1$
		getAnnotationTest().assertAnnotationIsCreated(file, message, 32);

		file = getTestProject().getFile("src/deltaspike/security/SecuredBean1.java"); //$NON-NLS-1$
		message = NLS.bind(DeltaspikeValidationMessages.AMBIGUOUS_AUTHORIZER, "deltaspike.security.CustomSecurityBinding", "doSomething2"); //$NON-NLS-1$ //$NON-NLS-2$
		getAnnotationTest().assertAnnotationIsCreated(file, message, 26);

		message = NLS.bind(DeltaspikeValidationMessages.UNRESOLVED_AUTHORIZER, "deltaspike.security.CustomSecurityBinding", "doSomething3"); //$NON-NLS-1$ //$NON-NLS-2$
		getAnnotationTest().assertAnnotationIsCreated(file, message, 31);
	}

	public void testPartialbeanValidation() throws Exception {
		IFile file = getTestProject().getFile("src/deltaspike/partialbean/BeanA1.java"); //$NON-NLS-1$
		String message = NLS.bind(DeltaspikeValidationMessages.ILLEGAL_PARTIAL_BEAN, "deltaspike.partialbean.BindingA"); //$NON-NLS-1$
		getAnnotationTest().assertAnnotationIsCreated(file, message, 5);

		file = getTestProject().getFile("src/deltaspike/partialbean/BeanA2.java"); //$NON-NLS-1$
		message = NLS.bind(DeltaspikeValidationMessages.MULTIPLE_PARTIAL_BEAN_BINDINGS, "deltaspike.partialbean.BindingA", "deltaspike.partialbean.BindingB"); //$NON-NLS-1$
		getAnnotationTest().assertAnnotationIsCreated(file, message, 8);

		file = getTestProject().getFile("src/deltaspike/partialbean/HandlerB.java"); //$NON-NLS-1$
		message = NLS.bind(DeltaspikeValidationMessages.INVALID_PARTIAL_BEAN_HANDLER, new String[0]); //$NON-NLS-1$
		getAnnotationTest().assertAnnotationIsCreated(file, message, 8);

		file = getTestProject().getFile("src/deltaspike/partialbean/HandlerB1.java"); //$NON-NLS-1$
		message = NLS.bind(DeltaspikeValidationMessages.MULTIPLE_PARTIAL_BEAN_HANDLERS, "deltaspike.partialbean.BindingB"); //$NON-NLS-1$
		getAnnotationTest().assertAnnotationIsCreated(file, message, 8);

		file = getTestProject().getFile("src/deltaspike/partialbean/BeanC.java"); //$NON-NLS-1$
		message = NLS.bind(DeltaspikeValidationMessages.MISSING_PARTIAL_BEAN_HANDLER, "deltaspike.partialbean.BeanC", "deltaspike.partialbean.BindingC"); //$NON-NLS-1$
		getAnnotationTest().assertAnnotationIsCreated(file, message, 5);

		//interface BeanD
		file = getTestProject().getFile("src/deltaspike/partialbean/BeanD.java"); //$NON-NLS-1$
		message = NLS.bind(DeltaspikeValidationMessages.MISSING_PARTIAL_BEAN_HANDLER, "deltaspike.partialbean.BeanD", "deltaspike.partialbean.BindingD"); //$NON-NLS-1$
		getAnnotationTest().assertAnnotationIsCreated(file, message, 5);

	}


}
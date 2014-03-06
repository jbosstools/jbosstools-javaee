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

		getAnnotationTest().assertAnnotationIsNotCreated(file, DeltaspikeValidationMessages.INVALID_HANDLER_TYPE, 13);
		getAnnotationTest().assertAnnotationIsCreated(file, DeltaspikeValidationMessages.INVALID_HANDLER_TYPE, 16);
	}

	public void testSecurityValidation() throws Exception {
		IFile file = getTestProject().getFile("src/deltaspike/security/CustomAuthorizer.java"); //$NON-NLS-1$

		String message = NLS.bind(DeltaspikeValidationMessages.INVALID_AUTHORIZER_NOT_BOOLEAN, "check3"); //$NON-NLS-1$
		getAnnotationTest().assertAnnotationIsCreated(file, message, 25);

		message = NLS.bind(DeltaspikeValidationMessages.INVALID_AUTHORIZER_NO_BINDINGS, "check4"); //$NON-NLS-1$
		getAnnotationTest().assertAnnotationIsCreated(file, message, 30);

		file = getTestProject().getFile("src/deltaspike/security/SecuredBean1.java"); //$NON-NLS-1$
		message = NLS.bind(DeltaspikeValidationMessages.AMBIGUOUS_AUTHORIZER, "deltaspike.security.CustomSecurityBinding", "doSomething2"); //$NON-NLS-1$ //$NON-NLS-2$
		getAnnotationTest().assertAnnotationIsCreated(file, message, 24);

		message = NLS.bind(DeltaspikeValidationMessages.UNRESOLVED_AUTHORIZER, "deltaspike.security.CustomSecurityBinding", "doSomething3"); //$NON-NLS-1$ //$NON-NLS-2$
		getAnnotationTest().assertAnnotationIsCreated(file, message, 29);
	}

	public void testPartialbeanValidation() throws Exception {
		IFile file = getTestProject().getFile("src/deltaspike/partialbean/BeanA1.java"); //$NON-NLS-1$
		String message = NLS.bind(DeltaspikeValidationMessages.ILLEGAL_PARTIAL_BEAN, "deltaspike.partialbean.BindingA"); //$NON-NLS-1$
		getAnnotationTest().assertAnnotationIsCreated(file, message, 3);

		file = getTestProject().getFile("src/deltaspike/partialbean/BeanA2.java"); //$NON-NLS-1$
		message = NLS.bind(DeltaspikeValidationMessages.MULTIPLE_PARTIAL_BEAN_BINDINGS, "deltaspike.partialbean.BindingA", "deltaspike.partialbean.BindingB"); //$NON-NLS-1$
		getAnnotationTest().assertAnnotationIsCreated(file, message, 6);

		file = getTestProject().getFile("src/deltaspike/partialbean/HandlerB.java"); //$NON-NLS-1$
		message = NLS.bind(DeltaspikeValidationMessages.INVALID_PARTIAL_BEAN_HANDLER, new String[0]); //$NON-NLS-1$
		getAnnotationTest().assertAnnotationIsCreated(file, message, 6);

		file = getTestProject().getFile("src/deltaspike/partialbean/HandlerB1.java"); //$NON-NLS-1$
		message = NLS.bind(DeltaspikeValidationMessages.MULTIPLE_PARTIAL_BEAN_HANDLERS, "deltaspike.partialbean.BindingB"); //$NON-NLS-1$
		getAnnotationTest().assertAnnotationIsCreated(file, message, 8);

		file = getTestProject().getFile("src/deltaspike/partialbean/BeanC.java"); //$NON-NLS-1$
		message = NLS.bind(DeltaspikeValidationMessages.MISSING_PARTIAL_BEAN_HANDLER, "deltaspike.partialbean.BeanC", "deltaspike.partialbean.BindingC"); //$NON-NLS-1$
		getAnnotationTest().assertAnnotationIsCreated(file, message, 3);

	}


}
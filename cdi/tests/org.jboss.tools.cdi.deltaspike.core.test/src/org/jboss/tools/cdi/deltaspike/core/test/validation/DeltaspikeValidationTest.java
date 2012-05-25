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
import org.jboss.tools.cdi.internal.core.validation.CDIValidationMessages;
import org.jboss.tools.tests.AbstractResourceMarkerTest;

/**
 * @author Alexey Kazakov
 */
public class DeltaspikeValidationTest extends DeltaspikeCoreTest {

	public void testValidation() throws Exception {
		// TODO
	}

	public void testConfigPropertyValidation() throws Exception {
		IFile file = getTestProject().getFile("src/deltaspike/config/SettingsBean.java"); //$NON-NLS-1$
		
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS, 8);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS, 12);
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS, 16);
	}

	public void testHandlerValidation() throws Exception {
		IFile file = getTestProject().getFile("src/deltaspike/handler/MyHandlers.java"); //$NON-NLS-1$
		
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, DeltaspikeValidationMessages.INVALID_HANDLER_TYPE, 13);
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, DeltaspikeValidationMessages.INVALID_HANDLER_TYPE, 16);
	}

	public void testSecurityValidation() throws Exception {
		IFile file = getTestProject().getFile("src/deltaspike/security/CustomAuthorizer.java"); //$NON-NLS-1$

		String message = NLS.bind(DeltaspikeValidationMessages.INVALID_AUTHORIZER_NOT_BOOLEAN, "check3"); //$NON-NLS-1$
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, message, 25);

		message = NLS.bind(DeltaspikeValidationMessages.INVALID_AUTHORIZER_NO_BINDINGS, "check4"); //$NON-NLS-1$
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, message, 30);

		file = getTestProject().getFile("src/deltaspike/security/SecuredBean1.java"); //$NON-NLS-1$
		message = NLS.bind(DeltaspikeValidationMessages.AMBIGUOUS_AUTHORIZER, "deltaspike.security.CustomSecurityBinding", "doSomething2"); //$NON-NLS-1$ //$NON-NLS-2$
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, message, 24);

		message = NLS.bind(DeltaspikeValidationMessages.UNRESOLVED_AUTHORIZER, "deltaspike.security.CustomSecurityBinding", "doSomething3"); //$NON-NLS-1$ //$NON-NLS-2$
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, message, 29);
	}
}
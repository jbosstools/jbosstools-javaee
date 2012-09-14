/*******************************************************************************
 * Copyright (c) 2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.deltaspike.core.test.validation;

import org.eclipse.core.resources.IFile;
import org.jboss.tools.cdi.deltaspike.core.test.DeltaspikeCoreTest;
import org.jboss.tools.cdi.internal.core.validation.CDIValidationMessages;

/**
 * @author Alexey Kazakov
 */
public class DeltaspikeInjectionValidationTest extends DeltaspikeCoreTest {

	public void testConfigPropertyValidation() throws Exception {
		IFile file = getTestProject().getFile("src/deltaspike/config/SettingsBean.java"); //$NON-NLS-1$

		getAnnotationTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS, 8);
		getAnnotationTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS, 12);
		getAnnotationTest().assertAnnotationIsCreated(file, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS, 16);
	}
}
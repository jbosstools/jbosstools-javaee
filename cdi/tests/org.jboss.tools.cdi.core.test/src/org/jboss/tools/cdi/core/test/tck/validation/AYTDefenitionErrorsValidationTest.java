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
package org.jboss.tools.cdi.core.test.tck.validation;

import org.eclipse.core.resources.IFile;
import org.jboss.tools.tests.IAnnotationTest;

/**
 * @author Alexey Kazakov
 */
public class AYTDefenitionErrorsValidationTest extends DefenitionErrorsValidationTest {

	private CDIAnnotationTest annotationTest = new CDIAnnotationTest();

	protected IAnnotationTest getAnnotationTest() {
		return annotationTest;
	}

	@Override
	public void testBeanDoesNotHaveSomeTypeOfSpecializedBean() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/specialization/MissingTypeBeanBroken.java");
		annotationTest.assertAnnotationsEqualToMarkers(file);
	}
}
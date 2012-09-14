/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.seam.solder.core.test;

import org.eclipse.core.resources.IFile;
import org.jboss.tools.cdi.internal.core.validation.CDIValidationMessages;

/**
 * @author Alexey Kazakov
 */
public class SeamServletValidationTest extends SeamSolderTest {

	/**
	 * CDI validator should ignore injection points annotated @RequestParam/@HeaderParam/@CookieParam
	 * See https://issues.jboss.org/browse/JBIDE-9389
	 * @throws Exception
	 */
	public void testInjectionValidationForField() throws Exception {
		IFile file = getTestProject().getFile("src/org/jboss/servlet/Validation.java");
		for (int i = 12; i < 30; i++) {
			getAnnotationTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS, i);
			getAnnotationTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS, i);
		}
		getAnnotationTest().assertAnnotationIsCreated(file, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS, 40, 42);
		getAnnotationTest().assertAnnotationIsCreated(file, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS, 41, 43);
	}

	/**
	 * CDI validator should ignore injection points annotated @RequestParam/@HeaderParam/@CookieParam
	 * See https://issues.jboss.org/browse/JBIDE-9389
	 * @throws Exception
	 */
	public void testInjectionValidationForParam() throws Exception {
		IFile file = getTestProject().getFile("src/org/jboss/servlet/Validation.java");
		for (int i = 30; i < 39; i++) {
			getAnnotationTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS, i);
			getAnnotationTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS, i);
		}
	}
}
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
import org.jboss.tools.cdi.internal.core.validation.CDIValidationMessages;

/**
 * @author Alexey Kazakov
 */
public class WeldValidationTest extends ValidationTest {

	/**
	 * https://issues.jboss.org/browse/JBIDE-12644
	 * Weld extension: @Inject @Parameters List<String> parameters;
	 * @throws Exception
	 */
	public void testParameters() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/weld/WeldBean.java");
		getAnnotationTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS, 11);
		getAnnotationTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS, 12);
	}
}
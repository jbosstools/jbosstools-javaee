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
package org.jboss.tools.cdi.seam.core.test.jms;

import org.eclipse.core.resources.IFile;
import org.jboss.tools.cdi.internal.core.validation.CDIValidationMessages;
import org.jboss.tools.cdi.seam.core.test.international.SeamCoreTest;
import org.jboss.tools.tests.AbstractResourceMarkerTest;

/**
 * @author Alexey Kazakov
 */
public class SeamJmsValidationTest extends SeamCoreTest {

	/**
	 * See https://issues.jboss.org/browse/JBIDE-9685
	 * 
	 * @throws Exception
	 */
	public void testJmsResourceInjection() throws Exception {
		IFile file = getTestProject().getFile("src/org/jboss/tools/seam/jms/validation/test/JmsResourceInjection.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS, 17);
		assertNoError(file, 19, 20, 22, 23, 25, 26, 28, 29, 31, 32, 34, 35, 37, 38, 40, 41);
	}

	private void assertNoError(IFile file, Integer... integers) throws Exception {
		for (Integer integer : integers) {
			AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS, integer);
			AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS, integer);
		}
	}
}
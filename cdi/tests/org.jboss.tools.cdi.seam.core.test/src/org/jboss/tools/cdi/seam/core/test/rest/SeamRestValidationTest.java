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
package org.jboss.tools.cdi.seam.core.test.rest;

import org.eclipse.core.resources.IFile;
import org.jboss.tools.cdi.internal.core.validation.CDIValidationMessages;
import org.jboss.tools.cdi.seam.core.test.SeamCoreTest;
import org.jboss.tools.tests.AbstractResourceMarkerTest;

/**
 * @author Alexey Kazakov
 */
public class SeamRestValidationTest extends SeamCoreTest {

	/**
	 * See https://issues.jboss.org/browse/JBIDE-9734
	 * 
	 * @throws Exception
	 */
	public void testRestClientInjection() throws Exception {
		IFile file = getTestProject().getFile("src/org/jboss/tools/seam/rest/validation/test/RestClientInjection.java");
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS, 9);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS, 14);
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS, 17);
	}
}
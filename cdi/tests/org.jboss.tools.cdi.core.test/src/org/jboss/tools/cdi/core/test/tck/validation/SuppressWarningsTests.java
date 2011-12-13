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
package org.jboss.tools.cdi.core.test.tck.validation;

import java.text.MessageFormat;

import org.eclipse.core.resources.IFile;
import org.jboss.tools.cdi.internal.core.validation.CDIValidationMessages;
import org.jboss.tools.tests.AbstractResourceMarkerTest;

/**
 * @author Alexey Kazakov
 * See https://issues.jboss.org/browse/JBIDE-10187
 */
public class SuppressWarningsTests extends ValidationTest {

	public void testClass() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/suppresswarnings/Fish.java");
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, MessageFormat.format(CDIValidationMessages.DUPLCICATE_EL_NAME, ".*"), 8);

		file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/suppresswarnings/AnotherFish.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, MessageFormat.format(CDIValidationMessages.DUPLCICATE_EL_NAME, ".*"), 7, 12);
	}

	public void testFieldWithSuppressInParentElement() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/suppresswarnings/Fish.java");
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, MessageFormat.format(CDIValidationMessages.DUPLCICATE_EL_NAME, ".*"), 13);
	}

	public void testField() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/suppresswarnings/Fish.java");
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, CDIValidationMessages.PRODUCER_ANNOTATED_INJECT, 17);
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, CDIValidationMessages.PRODUCER_ANNOTATED_INJECT, 19);

		file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/suppresswarnings/AnotherFish.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, CDIValidationMessages.PRODUCER_ANNOTATED_INJECT, 15, 17, 23);
	}

	public void testParam() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/suppresswarnings/Fish.java");
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS, 22);
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS, 31);

		file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/suppresswarnings/AnotherFish.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS, 20, 24, 28);
	}

	public void testMultipleSuppress() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/suppresswarnings/Fish.java");
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS, 27);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, CDIValidationMessages.PRODUCER_ANNOTATED_INJECT, 26);
	}
}
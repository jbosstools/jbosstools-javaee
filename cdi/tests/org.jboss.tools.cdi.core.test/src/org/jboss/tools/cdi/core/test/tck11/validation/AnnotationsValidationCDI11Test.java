/******************************************************************************* 
 * Copyright (c) 2013 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.core.test.tck11.validation;

import org.eclipse.core.resources.IFile;
import org.jboss.tools.cdi.core.test.tck.ITCKProjectNameProvider;
import org.jboss.tools.cdi.core.test.tck.validation.AnnotationsValidationTest;
import org.jboss.tools.cdi.core.test.tck11.TCK11ProjectNameProvider;
import org.jboss.tools.cdi.internal.core.validation.CDIValidationMessages;

/**
 * @author Alexey Kazakov
 */
public class AnnotationsValidationCDI11Test extends AnnotationsValidationTest {

	@Override
	public ITCKProjectNameProvider getProjectNameProvider() {
		return new TCK11ProjectNameProvider();
	}

	public void testQualifierWithMissingTarget() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/annotations/qualifier/broken/Hairy_MissingTarget.java");
		getAnnotationTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.MISSING_TARGET_ANNOTATION_IN_QUALIFIER_TYPE.substring(0, 56) + ".*", 36);
	}

	public void testQualifierWithWrongTarget() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/annotations/qualifier/broken/Hairy_WrongTarget.java");
		getAnnotationTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.MISSING_TARGET_ANNOTATION_IN_QUALIFIER_TYPE.substring(0, 56) + ".*", 32);
	}

	public void testQualifierWithTarget11Ok() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/annotations/qualifier/HairyTarget11Ok.java");
		getAnnotationTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.MISSING_TARGET_ANNOTATION_IN_QUALIFIER_TYPE.substring(0, 56) + ".*", 32);
	}

}
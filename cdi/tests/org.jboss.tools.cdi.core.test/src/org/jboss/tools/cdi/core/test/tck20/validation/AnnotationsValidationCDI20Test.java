/******************************************************************************* 
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.core.test.tck20.validation;

import org.eclipse.core.resources.IFile;
import org.jboss.tools.cdi.core.test.tck.ITCKProjectNameProvider;
import org.jboss.tools.cdi.core.test.tck.validation.AnnotationsValidationTest;
import org.jboss.tools.cdi.core.test.tck20.TCK20ProjectNameProvider;
import org.jboss.tools.cdi.internal.core.validation.CDIValidationMessages;


public class AnnotationsValidationCDI20Test extends AnnotationsValidationTest {

	@Override
	public ITCKProjectNameProvider getProjectNameProvider() {
		return new TCK20ProjectNameProvider();
	}

	public void testQualifierWithMissingTarget() throws Exception {
		assertNull(CDIValidationMessages.MISSING_TARGET_ANNOTATION_IN_QUALIFIER_TYPE[getVersionIndex()]);
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/annotations/qualifier/broken/Hairy_MissingTarget.java");
		getAnnotationTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.MISSING_TARGET_ANNOTATION_IN_QUALIFIER_TYPE[0].substring(0, 56) + ".*", 36);
	}

	public void testQualifierWithWrongTarget() throws Exception {
		assertNull(CDIValidationMessages.MISSING_TARGET_ANNOTATION_IN_QUALIFIER_TYPE[getVersionIndex()]);
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/annotations/qualifier/broken/Hairy_WrongTarget.java");
		getAnnotationTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.MISSING_TARGET_ANNOTATION_IN_QUALIFIER_TYPE[0].substring(0, 56) + ".*", 32);
	}

	public void testQualifierWithTarget11Ok() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/annotations/qualifier/HairyTarget11Ok.java");
		getAnnotationTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.MISSING_TARGET_ANNOTATION_IN_QUALIFIER_TYPE[0].substring(0, 56) + ".*", 32);
	}

	public void testStereotypeWithMissingTarget() throws Exception {
		assertNull(CDIValidationMessages.MISSING_TARGET_ANNOTATION_IN_STEREOTYPE_TYPE[getVersionIndex()]);
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/annotations/stereotype/broken/FishStereotype_MissingTarget.java");
		getAnnotationTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.MISSING_TARGET_ANNOTATION_IN_STEREOTYPE_TYPE[0].substring(0, 56) + ".*", 19);
	}

	public void testStereotypeWithWrongTarget() throws Exception {
		assertNull(CDIValidationMessages.MISSING_TARGET_ANNOTATION_IN_STEREOTYPE_TYPE[getVersionIndex()]);
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/annotations/stereotype/broken/FishStereotype_WrongTarget.java");
		getAnnotationTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.MISSING_TARGET_ANNOTATION_IN_STEREOTYPE_TYPE[0].substring(0, 56) + ".*", 15);
	}

	public void testScopeWithWrongTarget() throws Exception {
		assertNull(CDIValidationMessages.MISSING_TARGET_ANNOTATION_IN_SCOPE_TYPE[getVersionIndex()]);
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/annotations/scope/broken/FooScoped_WrongTarget.java");
		getAnnotationTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.MISSING_TARGET_ANNOTATION_IN_SCOPE_TYPE[0].substring(0, 52) + ".*", 30);
	}

	public void testScopeWithMissingTarget() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/annotations/scope/broken/FooScoped_MissingTarget.java");
		getAnnotationTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.MISSING_TARGET_ANNOTATION_IN_SCOPE_TYPE[0].substring(0, 52) + ".*", 33);
	}

}
/******************************************************************************* 
 * Copyright (c) 2010 Red Hat, Inc. 
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

/**
 * Wrong targets in CDI annotation types.
 * 
 * @author Alexey Kazakov
 */
public class AnnotationsValidationTest extends ValidationTest {

	public void testQualifierWithMissingTarget() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/annotations/qualifier/broken/Hairy_MissingTarget.java");
		getAnnotationTest().assertAnnotationIsCreated(file, CDIValidationMessages.MISSING_TARGET_ANNOTATION_IN_QUALIFIER_TYPE[0].substring(0, 56) + ".*", 36);
	}

	public void testQualifierWithMissingRetention() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/annotations/qualifier/broken/Hairy_MissingRetention.java");
		getAnnotationTest().assertAnnotationIsCreated(file, CDIValidationMessages.MISSING_RETENTION_ANNOTATION_IN_QUALIFIER_TYPE[getVersionIndex()], 36);
	}

	public void testQualifierWithWrongTarget() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/annotations/qualifier/broken/Hairy_WrongTarget.java");
		getAnnotationTest().assertAnnotationIsCreated(file, CDIValidationMessages.MISSING_TARGET_ANNOTATION_IN_QUALIFIER_TYPE[0].substring(0, 56) + ".*", 32);
	}

	public void testQualifierWithTargetOk() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/annotations/qualifier/HairyTargetOk.java");
		getAnnotationTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.MISSING_TARGET_ANNOTATION_IN_QUALIFIER_TYPE[0].substring(0, 56) + ".*", 14);
	}

	public void testStereotypeWithMissingTarget() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/annotations/stereotype/broken/FishStereotype_MissingTarget.java");
		getAnnotationTest().assertAnnotationIsCreated(file, CDIValidationMessages.MISSING_TARGET_ANNOTATION_IN_STEREOTYPE_TYPE[0].substring(0, 56) + ".*", 19);
	}

	public void testStereotypeWithMissingRetention() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/annotations/stereotype/broken/FishStereotype_MissingRetention.java");
		getAnnotationTest().assertAnnotationIsCreated(file, CDIValidationMessages.MISSING_RETENTION_ANNOTATION_IN_STEREOTYPE_TYPE[getVersionIndex()], 19);
	}

	public void testStereotypeWithWrongTarget() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/annotations/stereotype/broken/FishStereotype_WrongTarget.java");
		getAnnotationTest().assertAnnotationIsCreated(file, CDIValidationMessages.MISSING_TARGET_ANNOTATION_IN_STEREOTYPE_TYPE[0].substring(0, 56) + ".*", 15);
	}

	public void testScopeWithMissingTarget() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/annotations/scope/broken/FooScoped_MissingTarget.java");
		getAnnotationTest().assertAnnotationIsCreated(file, CDIValidationMessages.MISSING_TARGET_ANNOTATION_IN_SCOPE_TYPE[0].substring(0, 52) + ".*", 33);
	}

	public void testScopeWithMissingRetention() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/annotations/scope/broken/FooScoped_MissingRetention.java");
		getAnnotationTest().assertAnnotationIsCreated(file, CDIValidationMessages.MISSING_RETENTION_ANNOTATION_IN_SCOPE_TYPE[getVersionIndex()], 33);
	}

	public void testScopeWithWrongTarget() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/annotations/scope/broken/FooScoped_WrongTarget.java");
		getAnnotationTest().assertAnnotationIsCreated(file, CDIValidationMessages.MISSING_TARGET_ANNOTATION_IN_SCOPE_TYPE[0].substring(0, 52) + ".*", 30);
	}

	public void testStereotypesWithAdditionalStereotypesWithTMFTarget() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/target/StereotypeTMFBroken.java");
		getAnnotationTest().assertAnnotationIsCreated(file, MessageFormat.format(CDIValidationMessages.ILLEGAL_TARGET_IN_STEREOTYPE_TYPE_TMF[getVersionIndex()], "StereotypeWTypeTarget", "StereotypeTMFBroken"), 15);
	}

	public void testStereotypesWithAdditionalStereotypesWithMTarget() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/target/StereotypeMBroken.java");
		getAnnotationTest().assertAnnotationIsCreated(file, MessageFormat.format(CDIValidationMessages.ILLEGAL_TARGET_IN_STEREOTYPE_TYPE_M[getVersionIndex()], "StereotypeWTypeTarget", "StereotypeMBroken"), 13);
	}

	public void testStereotypesWithAdditionalStereotypesWithFTarget() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/target/StereotypeFBroken.java");
		getAnnotationTest().assertAnnotationIsCreated(file, MessageFormat.format(CDIValidationMessages.ILLEGAL_TARGET_IN_STEREOTYPE_TYPE_F[getVersionIndex()], "StereotypeWTypeTarget", "StereotypeFBroken"), 13);
	}

	public void testStereotypesWithAdditionalStereotypesWithMFTarget() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/target/StereotypeMFBroken.java");
		getAnnotationTest().assertAnnotationIsCreated(file, MessageFormat.format(CDIValidationMessages.ILLEGAL_TARGET_IN_STEREOTYPE_TYPE_MF[getVersionIndex()], "StereotypeWTypeTarget", "StereotypeMFBroken"), 14);
	}

	public void testStereotypesWithAdditionalStereotypesOk() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/target/StereotypeOk.java");
		getAnnotationTest().assertAnnotationIsNotCreated(file, MessageFormat.format(CDIValidationMessages.ILLEGAL_TARGET_IN_STEREOTYPE_TYPE_TMF[getVersionIndex()], "StereotypeWTypeTarget", "StereotypeOk"), 13);
		getAnnotationTest().assertAnnotationIsNotCreated(file, MessageFormat.format(CDIValidationMessages.ILLEGAL_TARGET_IN_STEREOTYPE_TYPE_M[getVersionIndex()], "StereotypeWTypeTarget", "StereotypeOk"), 13);
		getAnnotationTest().assertAnnotationIsNotCreated(file, MessageFormat.format(CDIValidationMessages.ILLEGAL_TARGET_IN_STEREOTYPE_TYPE_F[getVersionIndex()], "StereotypeWTypeTarget", "StereotypeOk"), 13);
		getAnnotationTest().assertAnnotationIsNotCreated(file, MessageFormat.format(CDIValidationMessages.ILLEGAL_TARGET_IN_STEREOTYPE_TYPE_MF[getVersionIndex()], "StereotypeWTypeTarget", "StereotypeOk"), 13);
	}

	public void testInterceptorBindingWithAdditionalInterceptorBindings() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/target/InterceptorBindingBroken.java");
		getAnnotationTest().assertAnnotationIsCreated(file, MessageFormat.format(CDIValidationMessages.ILLEGAL_TARGET_IN_INTERCEPTOR_BINDING_TYPE[getVersionIndex()], "InterceptorBindingWTypeTarget", "InterceptorBindingBroken"), 16);
	}

	public void testInterceptorBindingWithAdditionalInterceptorBindingsOk() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/target/InterceptorBindingOk.java");
		getAnnotationTest().assertAnnotationIsNotCreated(file, MessageFormat.format(CDIValidationMessages.ILLEGAL_TARGET_IN_INTERCEPTOR_BINDING_TYPE[getVersionIndex()], "InterceptorBindingWTypeTarget", "InterceptorBindingOk"), 15);
	}

	public void testInterceptorBindingsForStereotypes() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/target/StereotypeWithInterceptorBindingBroken.java");
		getAnnotationTest().assertAnnotationIsCreated(file, MessageFormat.format(CDIValidationMessages.ILLEGAL_TARGET_IN_INTERCEPTOR_BINDING_TYPE_FOR_STEREOTYPE[getVersionIndex()], "StereotypeWithInterceptorBindingBroken", "InterceptorBindingWTypeTarget"), 16);
	}

	public void testInterceptorBindingsForStereotypesOk() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/target/StereotypeOk.java");
		getAnnotationTest().assertAnnotationIsNotCreated(file, MessageFormat.format(CDIValidationMessages.ILLEGAL_TARGET_IN_INTERCEPTOR_BINDING_TYPE_FOR_STEREOTYPE[getVersionIndex()], "StereotypeOk", "InterceptorBindingWTypeTarget"), 15);
	}
}
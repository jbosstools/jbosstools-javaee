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
		assertMarkerIsCreated(file, CDIValidationMessages.MISSING_TARGET_ANNOTATION_IN_QUALIFIER_TYPE.substring(0, 56) + ".*", 36);
	}

	public void testQualifierWithMissingRetention() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/annotations/qualifier/broken/Hairy_MissingRetention.java");
		assertMarkerIsCreated(file, CDIValidationMessages.MISSING_RETENTION_ANNOTATION_IN_QUALIFIER_TYPE, 36);
	}

	public void testQualifierWithWrongTarget() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/annotations/qualifier/broken/Hairy_WrongTarget.java");
		assertMarkerIsCreated(file, CDIValidationMessages.MISSING_TARGET_ANNOTATION_IN_QUALIFIER_TYPE.substring(0, 56) + ".*", 32);
	}

	public void testQualifierWithTargetOk() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/annotations/qualifier/HairyTargetOk.java");
		assertMarkerIsNotCreated(file, CDIValidationMessages.MISSING_TARGET_ANNOTATION_IN_QUALIFIER_TYPE.substring(0, 56) + ".*", 14);
	}

	public void testStereotypeWithMissingTarget() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/annotations/stereotype/broken/FishStereotype_MissingTarget.java");
		assertMarkerIsCreated(file, CDIValidationMessages.MISSING_TARGET_ANNOTATION_IN_STEREOTYPE_TYPE.substring(0, 56) + ".*", 19);
	}

	public void testStereotypeWithMissingRetention() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/annotations/stereotype/broken/FishStereotype_MissingRetention.java");
		assertMarkerIsCreated(file, CDIValidationMessages.MISSING_RETENTION_ANNOTATION_IN_STEREOTYPE_TYPE, 19);
	}

	public void testStereotypeWithWrongTarget() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/annotations/stereotype/broken/FishStereotype_WrongTarget.java");
		assertMarkerIsCreated(file, CDIValidationMessages.MISSING_TARGET_ANNOTATION_IN_STEREOTYPE_TYPE.substring(0, 56) + ".*", 15);
	}

	public void testScopeWithMissingTarget() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/annotations/scope/broken/FooScoped_MissingTarget.java");
		assertMarkerIsCreated(file, CDIValidationMessages.MISSING_TARGET_ANNOTATION_IN_SCOPE_TYPE.substring(0, 52) + ".*", 33);
	}

	public void testScopeWithMissingRetention() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/annotations/scope/broken/FooScoped_MissingRetention.java");
		assertMarkerIsCreated(file, CDIValidationMessages.MISSING_RETENTION_ANNOTATION_IN_SCOPE_TYPE, 33);
	}

	public void testScopeWithWrongTarget() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/annotations/scope/broken/FooScoped_WrongTarget.java");
		assertMarkerIsCreated(file, CDIValidationMessages.MISSING_TARGET_ANNOTATION_IN_SCOPE_TYPE.substring(0, 52) + ".*", 30);
	}
}
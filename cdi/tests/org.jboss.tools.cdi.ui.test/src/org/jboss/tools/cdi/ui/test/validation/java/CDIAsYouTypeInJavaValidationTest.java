/*******************************************************************************
 * Copyright (c) 2012-2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.ui.test.validation.java;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.source.Annotation;
import org.jboss.tools.cdi.core.test.tck.TCKTest;
import org.jboss.tools.cdi.internal.core.validation.CDIValidationMessages;
import org.jboss.tools.common.base.test.validation.AbstractAsYouTypeValidationTest;
import org.jboss.tools.common.base.test.validation.java.BaseAsYouTypeInJavaValidationTest;

/**
 * 
 * @author Victor V. Rubezhny
 *
 */
public class CDIAsYouTypeInJavaValidationTest extends TCKTest {
	private static final String PAGE_NAME = "JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/NPEValidation.java";
	private static final String CDI_CORE_VALIDATOR_PROBLEM_TYPE = "org.jboss.tools.cdi.core.cdiproblem";

	private BaseAsYouTypeInJavaValidationTest baseTest = null;
	protected IProject project;

	private static final String [][] ANNOTATIONS2VALIDATE = 
		{ 
			{"@Inject", CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS}, 
			{"@Produces", CDIValidationMessages.PRODUCER_IN_DECORATOR}
		};

	@Override
	public void setUp() throws Exception {
		project = TCKTest.findTestProject();
		if (baseTest == null) {
			baseTest = new BaseAsYouTypeInJavaValidationTest(project, CDI_CORE_VALIDATOR_PROBLEM_TYPE);
		}
	}

	public void testAsYouTypeInJavaValidation() throws BadLocationException, CoreException {
 		assertNotNull("Test project '" + TCKTest.MAIN_PROJECT_NAME + "' is not prepared", project);
			for (int i = 0; i < ANNOTATIONS2VALIDATE.length; i++) {
				baseTest.openEditor(PAGE_NAME);
				try {
					doAsYouTypeValidationMarkerAnnotationsRemovalTest(ANNOTATIONS2VALIDATE[i][0], ANNOTATIONS2VALIDATE[i][1]);
				} finally {
					baseTest.closeEditor();
				}
			}
	}

	/**
	 * The test procedure steps:
	 * - Find EL by a given number
	 * - Set up a broken EL and save the document => see problem marker appearance on that EL
	 * - Set up a another broken EL => see annotation appearance on that EL instead of a problem marker 
	 *   (an old problem marker has to disappear)
	 * - Set up a good EL again => see annotation to disappear on that EL
	 * 
	 * @param goodEL
	 * @param elToValidate
	 * @param errorMessage
	 * @param numberOfRegionToTest
	 * @throws BadLocationException
	 * @throws CoreException 
	 */
	public void doAsYouTypeValidationMarkerAnnotationsRemovalTest(String annotation, String errorMessage) throws BadLocationException, CoreException {

		//============================
		// The test procedure steps:
		// - Find wrong annotation
		//============================

		String documentContent = baseTest.getDocument().get();

		int start = (documentContent == null ? -1 : documentContent
					.indexOf(annotation, 0));
		assertFalse("No annotation " + annotation + " found in document", (start == -1));
		int length = annotation.length();

		// do check marker and marker annotation appeared here
		int line = baseTest.getDocument().getLineOfOffset(start);
		baseTest.assertResourceMarkerIsCreated(baseTest.getFile(), toRegex(errorMessage), line + 1);

		Annotation problemAnnotation = baseTest.waitForAnnotation(
				start, start + length, errorMessage, AbstractAsYouTypeValidationTest.MAX_SECONDS_TO_WAIT, true, true);
		assertNotNull("Problem Marker Annotation for " + annotation + " not found!", problemAnnotation);

		String message = problemAnnotation.getText();
		assertEquals(
				"Not expected error message found in ProblemAnnotation. Expected: ["
						+ errorMessage + "], Found: [" + message + "]",
				errorMessage, message);

		//=================================================================================================
		// - Remove broken Annotation => see error annotation to disappear 
		//   (an old problem marker annotation has to disappear)
		//=================================================================================================

		baseTest.getDocument().replace(start, length, "");

		problemAnnotation = baseTest.waitForAnnotation(
				start, start + length, null, AbstractAsYouTypeValidationTest.MAX_SECONDS_TO_WAIT, true, false); // Still use the same length (Just to have a place to look in)
		assertNull("Problem Marker Annotation has not disappeared!", problemAnnotation);

		//=================================================================================================
		// - Restore broken Annotation => see error annotation appearance 
		//=================================================================================================

		baseTest.getDocument().replace(start, 0, annotation);

		problemAnnotation = baseTest.waitForAnnotation(
				start, start + length, errorMessage, AbstractAsYouTypeValidationTest.MAX_SECONDS_TO_WAIT, false, true);

		assertNotNull("Couldn\'t find the following problem annotation for " + annotation + ": Text: " + errorMessage + 
				" at position:" + start + "(start), " + (start + length) + "(end).",
				problemAnnotation);
		
		message = problemAnnotation.getText();
		assertEquals(
				"Not expected error message found in ProblemAnnotation. Expected: ["
						+ errorMessage + "], Found: [" + message + "]",
				errorMessage, message);

		assertNotNull("No Problem Annotation found for wrong annotation " + annotation + "!", problemAnnotation);
	}

	private String toRegex(String text) {
		StringBuilder result = new StringBuilder(text);

		int i = -1;
		while ((i = result.indexOf("[", i+1)) != -1) {
			result.insert(i++, '\\');
		}
		i = -1;
		while ((i = result.indexOf("]", i+1)) != -1) {
			result.insert(i++, '\\');
		}

		return result.toString();
	}
}
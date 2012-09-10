/*******************************************************************************
 * Copyright (c) 2012 Red Hat, Inc.
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
import org.jboss.tools.cdi.internal.core.validation.CDICoreValidator;
import org.jboss.tools.cdi.internal.core.validation.CDIValidationMessages;
import org.jboss.tools.common.base.test.validation.AbstractAsYouTypeValidationTest;
import org.jboss.tools.common.base.test.validation.java.BaseAsYouTypeInJavaValidationTest;

/**
 * 
 * @author Victor V. Rubezhny
 *
 */
public class CDIAsYouTypeInJavaSupressWarningsTest extends TCKTest {
	private static final String PAGE_NAME = "JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/TestNamed.java";

	private BaseAsYouTypeInJavaValidationTest baseTest = null;
	protected IProject project;

	private static final String SUPPRESSWARNINGS_NAME = "cdi-ambiguous-dependency";
	private static final String INJECT_ANNOTATION_NAME = "@Inject";
	private static final String ERROR_MESSAGE = CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS;

	@Override
	public void setUp() throws Exception {
		project = TCKTest.findTestProject();
		if (baseTest == null) {
			baseTest = new BaseAsYouTypeInJavaValidationTest(project, CDICoreValidator.PROBLEM_TYPE);
		}
	}

	public void testAsYouTypeInJavaValidation() throws BadLocationException, CoreException {
 		assertNotNull("Test project '" + TCKTest.MAIN_PROJECT_NAME + "' is not prepared", project);
		baseTest.openEditor(PAGE_NAME);
		try {
			doAsYouTypeValidationMarkerAnnotationsRemovalTest();
		} finally {
			baseTest.closeEditor();
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
	public void doAsYouTypeValidationMarkerAnnotationsRemovalTest() throws BadLocationException, CoreException {

		//============================
		// The test procedure steps:
		// - Find annotation:	@SuppressWarnings("cdi-ambiguous-dependency")
		// - Find MarkerAnnotation in line: @Inject String s; // Ambiguous 
		//============================

		String documentContent = baseTest.getDocument().get();

		int start = (documentContent == null ? -1 : documentContent
					.indexOf(SUPPRESSWARNINGS_NAME, 0));
		assertFalse("No annotation for \'" + SUPPRESSWARNINGS_NAME + "\' found in document", (start == -1));
		int length = SUPPRESSWARNINGS_NAME.length();

		int injectStart = (documentContent == null ? -1 : documentContent
				.indexOf(INJECT_ANNOTATION_NAME, start));
		int injectLength = INJECT_ANNOTATION_NAME.length();
		
		// do check marker and marker annotation are absent at line of @Inject annotation 
		int line = baseTest.getDocument().getLineOfOffset(injectStart);
		baseTest.assertNoResourceMarkerIsCreated(baseTest.getFile(), toRegex(ERROR_MESSAGE), line + 1);

		Annotation problemAnnotation = baseTest.waitForAnnotation(
				start, start + length, ERROR_MESSAGE, AbstractAsYouTypeValidationTest.MAX_SECONDS_TO_WAIT, true, false);
		assertNull("Problem Marker Annotation found for \'" + INJECT_ANNOTATION_NAME + "\'!", problemAnnotation);

		//=================================================================================================
		// - Remove some chars in @SuppressWarning Annotation Type => see error annotation to appear 
		//=================================================================================================

		baseTest.getDocument().replace(start, length, "xyz");

		problemAnnotation = baseTest.waitForAnnotation(
				start, start + length, null, AbstractAsYouTypeValidationTest.MAX_SECONDS_TO_WAIT, false, true); // Still use the same length (Just to have a place to look in)
		assertNotNull("Problem Annotation didn't appeare!", problemAnnotation);

		String message = problemAnnotation.getText();
		assertEquals(
				"Not expected error message found in ProblemAnnotation. Expected: ["
						+ ERROR_MESSAGE + "], Found: [" + message + "]",
				ERROR_MESSAGE, message);

		//=================================================================================================
		// - Restore broken @SupressWarnings Annotation Type => see error annotation disappearance 
		//=================================================================================================

		baseTest.getDocument().replace(start, 3, SUPPRESSWARNINGS_NAME);

		problemAnnotation = baseTest.waitForAnnotation(
				start, start + length, ERROR_MESSAGE, AbstractAsYouTypeValidationTest.MAX_SECONDS_TO_WAIT, false, false);

		assertNull("Problem Annotation didn't disappeare!", problemAnnotation);
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
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
import org.jboss.tools.common.base.test.validation.AbstractAsYouTypeValidationTest;
import org.jboss.tools.common.base.test.validation.java.BaseAsYouTypeInJavaValidationTest;
import org.jboss.tools.jst.web.kb.internal.validation.ELValidator;

/**
 * 
 * @author Victor V. Rubezhny
 *
 */
public class CDIAsYouTypeCDIAndELValidatorsMassagesProcessingTest extends TCKTest {
	private static final String PAGE_NAME = "JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/Bean_Broken.java";

	private BaseAsYouTypeInJavaValidationTest baseTest = null;
	protected IProject project;

	private static final String TEXT_LINE_TO_REPLACE = "String s2 = \"#{string.ss}\";";
	private static final int SHIFT_FROM_THE_END = -2;
	private static final String TEXT_TO_INSERT = " ";
	private static final String TEXT_LINE_TO_CHECK = "String s = \"#{string.ss}\";";
	private static final String TEXT_METHOD_BODY = "return \"\";";
	
	private static final String ERROR_MESSAGE = "\"ss\" cannot be resolved";

	
	@Override
	public void setUp() throws Exception {
		project = TCKTest.findTestProject();
		if (baseTest == null) {
			// Since the test is about EL errors we're using ELValidator.PROBLEM_TYPE type of problem
			baseTest = new BaseAsYouTypeInJavaValidationTest(project, ELValidator.PROBLEM_TYPE);
		}
	}

	
	/*
	 * Test case for the following issues: JBIDE-12418, JBIDE-12539
	 */
	public void testAsYouTypeCDIAndELValidatorsMassagesProcessing() throws BadLocationException, CoreException {
 		assertNotNull("Test project '" + TCKTest.MAIN_PROJECT_NAME + "' is not prepared", project);
		baseTest.openEditor(PAGE_NAME);
		try {
			//============================
			// The test procedure steps:
			// - Find line for TEXT_LINE_TO_REPLACE
			//============================

			String documentContent = baseTest.getDocument().get();

			int start = (documentContent == null ? -1 : documentContent
						.indexOf(TEXT_LINE_TO_REPLACE, 0));
			int length = TEXT_LINE_TO_REPLACE.length();
			
			assertFalse("No line found for line '" + TEXT_LINE_TO_REPLACE + "'", (start == -1));

			// do check marker and marker annotation appeared here
			int line = baseTest.getDocument().getLineOfOffset(start);
			baseTest.assertResourceMarkerIsCreated(baseTest.getFile(), toRegex(ERROR_MESSAGE), line + 1);

			Annotation problemAnnotation = baseTest.waitForAnnotation(
					start, start + length, ERROR_MESSAGE, AbstractAsYouTypeValidationTest.MAX_SECONDS_TO_WAIT, true, true);
			assertNotNull("Problem Marker Annotation for line '" + TEXT_LINE_TO_REPLACE + "' not found!", problemAnnotation);

			String message = problemAnnotation.getText();
			assertEquals(
					"Not expected error message found in ProblemAnnotation. Expected: ["
							+ ERROR_MESSAGE + "], Found: [" + message + "]",
					ERROR_MESSAGE, message);

			//=================================================================================================
			// - Remove broken Annotation => see error annotation to disappear 
			//   (an old problem marker annotation has to disappear)
			//=================================================================================================

			baseTest.getDocument().replace(start, length, "");

			problemAnnotation = baseTest.waitForAnnotation(
					start, start + length, null, AbstractAsYouTypeValidationTest.MAX_SECONDS_TO_WAIT, true, false); // Still use the same length (Just to have a place to look in)
			assertNull("Problem Annotation has not disappeared!", problemAnnotation);

			//=================================================================================================
			// - Restore broken Annotation => see error annotation appearance 
			//=================================================================================================

			baseTest.getDocument().replace(start, 0, TEXT_LINE_TO_REPLACE);

			checkProblemAnnotationExists(start, length, ERROR_MESSAGE);

			//=================================================================================================
			// - Modify string in problematic line => see that error annotation hasn't disappeared 
			//=================================================================================================

			baseTest.getDocument().replace(start + length + SHIFT_FROM_THE_END, 0, TEXT_TO_INSERT);

			checkProblemAnnotationExists(start, length + TEXT_TO_INSERT.length(), ERROR_MESSAGE);
			
			//=================================================================================================
			// - Check that error annotation exists in another line 
			//=================================================================================================

			documentContent = baseTest.getDocument().get();

			int start2 = (documentContent == null ? -1 : documentContent
						.indexOf(TEXT_LINE_TO_CHECK, 0));
			int length2 = TEXT_LINE_TO_CHECK.length();

			checkProblemAnnotationExists(start2, length2, ERROR_MESSAGE);
			
			//=================================================================================================
			// - Modify string in a method body => see that error annotations hasn't disappeared 
			//=================================================================================================

			int start3 = (documentContent == null ? -1 : documentContent
					.indexOf(TEXT_METHOD_BODY, 0));
			int length3 = TEXT_METHOD_BODY.length();
			
			baseTest.getDocument().replace(start + length, 0, TEXT_TO_INSERT);
			
			documentContent = baseTest.getDocument().get();

			checkProblemAnnotationExists(start, length + TEXT_TO_INSERT.length(), ERROR_MESSAGE);
			checkProblemAnnotationExists(start2, length2, ERROR_MESSAGE);
		} finally {
			baseTest.closeEditor();
		}
	}

	private void checkProblemAnnotationExists(int start, int length, String errorMessage) {
		Annotation problemAnnotation = baseTest.waitForAnnotation(
				start, start + length + TEXT_TO_INSERT.length(), errorMessage, AbstractAsYouTypeValidationTest.MAX_SECONDS_TO_WAIT, false, true);

		assertNotNull("No Problem Annotation found for problematic line '" + TEXT_LINE_TO_REPLACE + "'!", problemAnnotation);

		String message = problemAnnotation.getText();
		assertEquals(
				"Not expected error message found in ProblemAnnotation. Expected: ["
						+ errorMessage + "], Found: [" + message + "]",
						errorMessage, message);
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
/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.batch.core.itest;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.source.Annotation;
import org.jboss.tools.common.base.test.validation.AbstractAsYouTypeValidationTest;
import org.jboss.tools.common.base.test.validation.TestUtil;
import org.jboss.tools.test.util.ProjectImportTestSetup;

import junit.framework.TestCase;

/**
 * @author Viacheslav Kabanovich
 */
public class BatchAsYouTypeValidationTest  extends TestCase {
	public static String PROJECT_NAME = "BatchTestProject"; //$NON-NLS-1$
	private IProject project;

	BaseAsYouTypeInJobXMLValidationTest baseTest = null;

	private static final String FILE_NAME = "src/META-INF/batch-jobs/job11.xml";
	private static final String TEXT_TO_REPLACE = "\"batchlet2\"";
	private static final String TEXT_TO_INSERT = "\"batchlet1\"";
	private static final String ERROR_MESSAGE = "Batchlet \"batchlet2\" is not found.";

	public BatchAsYouTypeValidationTest() {}

	@Override
	protected void setUp() throws Exception {
		project = ProjectImportTestSetup.loadProject(PROJECT_NAME);
		if (baseTest == null) {
			baseTest = new BaseAsYouTypeInJobXMLValidationTest(project, BaseAsYouTypeInJobXMLValidationTest.RESOURCE_MARKER_TYPE);
		}
	}

	public void testAsYouTypeInBatchJobMessagesProcessing() throws BadLocationException, CoreException {
 		assertNotNull("Test project '" + PROJECT_NAME + "' is not prepared", project);
 		IFile f = project.getFile(FILE_NAME);
 		assertTrue("Cannot find file " + FILE_NAME, f.exists());
 		TestUtil.validate(f);
		baseTest.openEditor(FILE_NAME);
		try {
			//============================
			// The test procedure steps:
			// - Find line for TEXT_TO_REPLACE
			//============================

			String documentContent = baseTest.getDocument().get();

			int start = (documentContent == null ? -1 : documentContent.indexOf(TEXT_TO_REPLACE, 0));
			int length = TEXT_TO_REPLACE.length();
			
			assertFalse("Text not found '" + TEXT_TO_REPLACE + "'", (start == -1));

			// do check marker and marker annotation appeared here
			int line = baseTest.getDocument().getLineOfOffset(start);
			baseTest.assertResourceMarkerIsCreated(baseTest.getFile(), toRegex(ERROR_MESSAGE), line + 1);

			Annotation problemAnnotation = baseTest.waitForAnnotation(
					start, start + length, ERROR_MESSAGE, AbstractAsYouTypeValidationTest.MAX_SECONDS_TO_WAIT, true, true);
			assertNotNull("Problem Marker Annotation for line '" + TEXT_TO_REPLACE + "' not found!", problemAnnotation);

			String message = problemAnnotation.getText();
			assertEquals(
					"Not expected error message found in ProblemAnnotation. Expected: ["
							+ ERROR_MESSAGE + "], Found: [" + message + "]",
					ERROR_MESSAGE, message);

			//=================================================================================================
			// - Remove broken Annotation => see error annotation to disappear 
			//   (an old problem marker annotation has to disappear)
			//=================================================================================================

			baseTest.getDocument().replace(start, length, "\"\"");

			problemAnnotation = baseTest.waitForAnnotation(
					start, start + 2, null, AbstractAsYouTypeValidationTest.MAX_SECONDS_TO_WAIT, true, false); // Still use the same length (Just to have a place to look in)
			assertNull("Problem Annotation has not disappeared!", problemAnnotation);

			//=================================================================================================
			// - Restore broken Annotation => see error annotation appearance 
			//=================================================================================================

			baseTest.getDocument().replace(start, 2, TEXT_TO_REPLACE);

			checkProblemAnnotationExists(start, length, ERROR_MESSAGE);

			//=================================================================================================
			// - Modify string in problematic line => see that error annotation disappeared 
			//=================================================================================================

			baseTest.getDocument().replace(start, length, TEXT_TO_INSERT);

			problemAnnotation = baseTest.waitForAnnotation(
					start, start + length, null, AbstractAsYouTypeValidationTest.MAX_SECONDS_TO_WAIT, true, false); // Still use the same length (Just to have a place to look in)
			assertNull("Problem Annotation has not disappeared!", problemAnnotation);
			
			//=================================================================================================
			// - Check that error annotation exists in another line 
			//=================================================================================================

			documentContent = baseTest.getDocument().get();

			int start2 = (documentContent == null ? -1 : documentContent
						.indexOf(TEXT_TO_REPLACE, 0));
			int length2 = TEXT_TO_REPLACE.length();

			checkProblemAnnotationExists(start2, length2, ERROR_MESSAGE);
			
		} finally {
			baseTest.closeEditor();
		}
	}
	
	private void checkProblemAnnotationExists(int start, int length, String errorMessage) throws BadLocationException {
		Annotation problemAnnotation = baseTest.waitForAnnotation(
				start, start + length, errorMessage, AbstractAsYouTypeValidationTest.MAX_SECONDS_TO_WAIT, false, true);

		assertNotNull("No Problem Annotation found for problematic line '" + baseTest.getDocument().get(start, length) + "'!", problemAnnotation);

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

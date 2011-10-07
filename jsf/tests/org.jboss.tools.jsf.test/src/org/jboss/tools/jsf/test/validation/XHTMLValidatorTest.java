/******************************************************************************* 
 * Copyright (c) 2009-2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.jsf.test.validation;

import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationState;
import org.eclipse.wst.validation.internal.core.Message;
import org.jboss.tools.common.base.test.validation.TestUtil;
import org.jboss.tools.jsf.web.validation.JSFValidationMessage;
import org.jboss.tools.jsf.web.validation.XHTMLValidator;
import org.jboss.tools.test.util.ResourcesUtils;
import org.jboss.tools.tests.AbstractResourceMarkerTest;

/**
 * The JUnit test case for JBIDE-9828 and JBIDE-9846 issues
 * 
 * @author Victor V. Rubezhny
 *
 */
public class XHTMLValidatorTest extends AbstractResourceMarkerTest {

	protected static String PLUGIN_ID = "org.jboss.tools.jsf.test";
	protected static String PROJECT_NAME = "jsf2pr";
	protected static String PROJECT_PATH = "/projects/jsf2pr";
	
	protected static final String FILE_NAME = "WebContent/XHTMLValidatorTest.xhtml";
	protected static Set<String> LOCALIZED_ERROR_MESSAGES = new HashSet<String>();
	static {
		LOCALIZED_ERROR_MESSAGES.add(MessageFormat.format(JSFValidationMessage.XHTML_VALIDATION_NO_END_TAG, 
				XHTMLValidationTestMessages.XHTML_GOOD_TAGNAME));
		LOCALIZED_ERROR_MESSAGES.add(MessageFormat.format(JSFValidationMessage.XHTML_VALIDATION_NO_START_TAG, 
				XHTMLValidationTestMessages.XHTML_WRONG_TAGNAME));
	}
	protected static Set<String> LOCALIZED_LARGE_ERROR_MESSAGES = new HashSet<String>();
	static {
		LOCALIZED_LARGE_ERROR_MESSAGES.add(MessageFormat.format(JSFValidationMessage.XHTML_VALIDATION_NO_END_TAG, 
				XHTMLValidationTestMessages.XHTML_LARGE_GOOD_TAGNAME));
		LOCALIZED_LARGE_ERROR_MESSAGES.add(MessageFormat.format(JSFValidationMessage.XHTML_VALIDATION_NO_START_TAG, 
				XHTMLValidationTestMessages.XHTML_LARGE_WRONG_TAGNAME));
	}
	protected static String LOCALIZED_BROKEN_ERROR_MESSAGE = XHTMLValidationTestMessages.XHTML_MARKUP_IS_BROKEN_ERROR;
	
	// "Bad" file validation time should be not greater than "Good" file validation time multiplied by 10  
	protected static final double NOT_BAD_DIFF_PERCENTAGE = 1000.0;
	
	// Each validation session should take less that 1 second (1000ms)
	protected static final long MAX_VALIDATION_TIME = 1000;

	IProject project;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		project = ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT_NAME);
		if(!project.exists()) {
			project = ResourcesUtils.importProject(PLUGIN_ID, PROJECT_PATH);
		}
		TestUtil._waitForValidation(project);
	}

	public void testWebContentValidation() throws CoreException {
		XHTMLValidator validator = new XHTMLValidator();
		ValidationState state = new ValidationState();
		try {
			// Validate file with good DOCTYPE declaration and no XHTML Syntax errors 
			IFile file = createTestFile(XHTMLValidationTestMessages.XHTML_CONTENT_TEMPLATE, 
					XHTMLValidationTestMessages.XHTML_GOOD_PUBLIC_ID, XHTMLValidationTestMessages.XHTML_GOOD_URI,
					XHTMLValidationTestMessages.XHTML_GOOD_TAGNAME, XHTMLValidationTestMessages.XHTML_GOOD_TAGNAME);
			long start = System.currentTimeMillis();
			ValidationResult result = validator.validate(file, IResourceDelta.CHANGED, state, new NullProgressMonitor());
			long goodValidationTime = System.currentTimeMillis() - start;
			System.out.println("XHTML file with good DOCTYPE declaration and no XHTML Syntax errors validation time: " + goodValidationTime + " ms");
			assertTrue("XHTML file with good DOCTYPE declaration and no XHTML Syntax errors validation takes too much time (more than " + MAX_VALIDATION_TIME + " ms)", (goodValidationTime < MAX_VALIDATION_TIME));
			assertNotNull("No validation result is returned", result);
			assertNotNull("No validation result is returned", result.getReporter(null));
			List messages = result.getReporter(null).getMessages();
			assertEquals("Wrong number of error messages reported", 0, messages == null ? 0 : messages.size());
			
			// Validate file with bad DOCTYPE declaration and no XHTML Syntax errors 
			file = createTestFile(XHTMLValidationTestMessages.XHTML_CONTENT_TEMPLATE, 
					XHTMLValidationTestMessages.XHTML_WRONG_PUBLIC_ID, XHTMLValidationTestMessages.XHTML_WRONG_URI,
					XHTMLValidationTestMessages.XHTML_GOOD_TAGNAME, XHTMLValidationTestMessages.XHTML_GOOD_TAGNAME);
			start = System.currentTimeMillis();
			result = validator.validate(file, IResourceDelta.CHANGED, state, new NullProgressMonitor());
			long badValidationTime = System.currentTimeMillis() - start;
			System.out.println("XHTML file with bad DOCTYPE declaration and no XHTML Syntax errors validation time: " + badValidationTime + " ms");
			assertTrue("XHTML file with bad DOCTYPE declaration and no XHTML Syntax errors validation takes too much time (more than " + MAX_VALIDATION_TIME + " ms)", (badValidationTime < MAX_VALIDATION_TIME));
			assertNotNull("No validation result is returned", result);
			assertNotNull("No validation result is returned", result.getReporter(null));
			messages = result.getReporter(null).getMessages();
			assertEquals("Wrong number of error messages reported", 0, messages == null ? 0 : messages.size());
	
			// Check that the difference between good and bad files validation time is not greater that NOT_BAD_DIFF_PERCENTAGE (%) of a good value
			double diff = 100*badValidationTime/goodValidationTime;
			System.out.println("(With no errors) Validation time difference: " + diff + "%");
			assertTrue("Validation time difference between good and wrong content is greater than " + NOT_BAD_DIFF_PERCENTAGE + "%", (diff < NOT_BAD_DIFF_PERCENTAGE));
			
			// Validate file with good DOCTYPE declaration and XHTML Syntax errors 
			file = createTestFile(XHTMLValidationTestMessages.XHTML_CONTENT_TEMPLATE, 
						XHTMLValidationTestMessages.XHTML_GOOD_PUBLIC_ID, XHTMLValidationTestMessages.XHTML_GOOD_URI,
						XHTMLValidationTestMessages.XHTML_GOOD_TAGNAME, XHTMLValidationTestMessages.XHTML_WRONG_TAGNAME);
			start = System.currentTimeMillis();
			result = validator.validate(file, IResourceDelta.CHANGED, state, new NullProgressMonitor());
			goodValidationTime = System.currentTimeMillis() - start;
			System.out.println("XHTML file with good DOCTYPE declaration and XHTML Syntax errors validation time: " + goodValidationTime + " ms");
			assertTrue("XHTML file with good DOCTYPE declaration and XHTML Syntax errors validation takes too much time (more than " + MAX_VALIDATION_TIME + " ms)", (goodValidationTime < MAX_VALIDATION_TIME));
			assertNotNull("No validation result is returned", result);
			assertNotNull("No validation result is returned", result.getReporter(null));
			messages = result.getReporter(null).getMessages();
			assertEquals("Wrong number of error messages reported", 2, messages == null ? 0 : messages.size());
			for (Object m : messages) {
				assertTrue("Wrong type of validation message is returned", (m instanceof Message));
				Message message = (Message)m;
				assertTrue("Unexpected error message found: " + message.getText(), LOCALIZED_ERROR_MESSAGES.contains(message.getText()));
			}
	
			// Validate file with bad DOCTYPE declaration and XHTML Syntax errors 
			file = createTestFile(XHTMLValidationTestMessages.XHTML_CONTENT_TEMPLATE, 
					XHTMLValidationTestMessages.XHTML_WRONG_PUBLIC_ID, XHTMLValidationTestMessages.XHTML_WRONG_URI,
					XHTMLValidationTestMessages.XHTML_GOOD_TAGNAME, XHTMLValidationTestMessages.XHTML_WRONG_TAGNAME);
			start = System.currentTimeMillis();
			result = validator.validate(file, IResourceDelta.CHANGED, state, new NullProgressMonitor());
			badValidationTime = System.currentTimeMillis() - start;
			System.out.println("XHTML file with bad DOCTYPE declaration and XHTML Syntax errors validation time: " + badValidationTime + " ms");
			assertTrue("XHTML file with bad DOCTYPE declaration and XHTML Syntax errors validation takes too much time (more than " + MAX_VALIDATION_TIME + " ms)", (badValidationTime < MAX_VALIDATION_TIME));
			assertNotNull("No validation result is returned", result);
			assertNotNull("No validation result is returned", result.getReporter(null));
			messages = result.getReporter(null).getMessages();
			assertEquals("Wrong number of error messages reported", 2, messages == null ? 0 : messages.size());
			for (Object m : messages) {
				assertTrue("Wrong type of validation message is returned", (m instanceof Message));
				Message message = (Message)m;
				assertTrue("Unexpected error message found: " + message.getText(), LOCALIZED_ERROR_MESSAGES.contains(message.getText()));
			}
			// Check that the difference between good and bad files validation time is not greater that NOT_BAD_DIFF_PERCENTAGE (%) of a good value
			diff = 100*badValidationTime/goodValidationTime;
			System.out.println("(With errors) Validation time difference: " + diff + "%");
			assertTrue("Validation time difference between good and wrong content is greater than " + NOT_BAD_DIFF_PERCENTAGE + "%", (diff < NOT_BAD_DIFF_PERCENTAGE));
		} finally {
			removeTestFile();
		}
	}

	public void testBrokenWebContentValidation() throws CoreException {
		XHTMLValidator validator = new XHTMLValidator();
		ValidationState state = new ValidationState();
		try {
			// Validate file with good DOCTYPE declaration and broken content
			IFile file = createBrokenTestFile(XHTMLValidationTestMessages.XHTML_BROKEN_CONTENT_TEMPLATE, 
					XHTMLValidationTestMessages.XHTML_GOOD_PUBLIC_ID, XHTMLValidationTestMessages.XHTML_GOOD_URI);
			long start = System.currentTimeMillis();
			ValidationResult result = validator.validate(file, IResourceDelta.CHANGED, state, new NullProgressMonitor());
			long goodValidationTime = System.currentTimeMillis() - start;
			System.out.println("XHTML file with good DOCTYPE declaration and broken content validation time: " + goodValidationTime + " ms");
			assertTrue("XHTML file with good DOCTYPE declaration and broken content validation takes too much time (more than " + MAX_VALIDATION_TIME + " ms)", (goodValidationTime < MAX_VALIDATION_TIME));
			assertNotNull("No validation result is returned", result);
			assertNotNull("No validation result is returned", result.getReporter(null));
			List messages = result.getReporter(null).getMessages();
			assertEquals("Wrong number of error messages reported", 1, messages == null ? 0 : messages.size());
			for (Object m : messages) {
				assertTrue("Wrong type of validation message is returned", (m instanceof Message));
				Message message = (Message)m;
//				System.out.println("Error Message: " + message.getText());
				assertTrue("Unexpected error message found: " + message.getText(), LOCALIZED_BROKEN_ERROR_MESSAGE.contains(message.getText()));
			}
			
			// Validate file with DOCTYPE declaration and broken content 
			file = createBrokenTestFile(XHTMLValidationTestMessages.XHTML_BROKEN_CONTENT_TEMPLATE, 
					XHTMLValidationTestMessages.XHTML_WRONG_PUBLIC_ID, XHTMLValidationTestMessages.XHTML_WRONG_URI);
			start = System.currentTimeMillis();
			result = validator.validate(file, IResourceDelta.CHANGED, state, new NullProgressMonitor());
			long badValidationTime = System.currentTimeMillis() - start;
			System.out.println("XHTML file with bad DOCTYPE declaration and broken content validation time: " + badValidationTime + " ms");
			assertTrue("XHTML file with bad DOCTYPE declaration and broken content validation takes too much time (more than " + MAX_VALIDATION_TIME + " ms)", (badValidationTime < MAX_VALIDATION_TIME));
			assertNotNull("No validation result is returned", result);
			assertNotNull("No validation result is returned", result.getReporter(null));
			messages = result.getReporter(null).getMessages();
			assertEquals("Wrong number of error messages reported", 1, messages == null ? 0 : messages.size());
			for (Object m : messages) {
				assertTrue("Wrong type of validation message is returned", (m instanceof Message));
				Message message = (Message)m;
//				System.out.println("Error Message: " + message.getText());
				assertTrue("Unexpected error message found: " + message.getText(), LOCALIZED_BROKEN_ERROR_MESSAGE.contains(message.getText()));
			}
	
			// Check that the difference between good and bad files validation time is not greater that NOT_BAD_DIFF_PERCENTAGE (%) of a good value
			double diff = 100*badValidationTime/goodValidationTime;
			System.out.println("(With broken content) Validation time difference: " + diff + "%");
			assertTrue("Validation time difference between good and wrong DOCTYPE declaration is greater than " + NOT_BAD_DIFF_PERCENTAGE + "%", (diff < NOT_BAD_DIFF_PERCENTAGE));
		} finally {
			removeTestFile();
		}
	}

	public void testLargeWebContentValidation() throws CoreException {
		XHTMLValidator validator = new XHTMLValidator();
		ValidationState state = new ValidationState();
		try {
			// Validate file with good DOCTYPE declaration and no XHTML Syntax errors 
			IFile file = createTestFile(XHTMLValidationTestMessages.XHTML_LARGE_CONTENT_TEMPLATE, 
					XHTMLValidationTestMessages.XHTML_GOOD_PUBLIC_ID, XHTMLValidationTestMessages.XHTML_GOOD_URI,
					XHTMLValidationTestMessages.XHTML_LARGE_GOOD_TAGNAME, XHTMLValidationTestMessages.XHTML_LARGE_GOOD_TAGNAME);
			long start = System.currentTimeMillis();
			ValidationResult result = validator.validate(file, IResourceDelta.CHANGED, state, new NullProgressMonitor());
			long goodValidationTime = System.currentTimeMillis() - start;
			System.out.println("Large XHTML file with good DOCTYPE declaration and no XHTML Syntax errors validation time: " + goodValidationTime + " ms");
			assertTrue("Large XHTML file with good DOCTYPE declaration and no XHTML Syntax errors validation takes too much time (more than " + MAX_VALIDATION_TIME + " ms)", (goodValidationTime < MAX_VALIDATION_TIME));
			assertNotNull("No validation result is returned", result);
			assertNotNull("No validation result is returned", result.getReporter(null));
			List messages = result.getReporter(null).getMessages();
			assertEquals("Wrong number of error messages reported", 0, messages == null ? 0 : messages.size());
			
			// Validate file with bad DOCTYPE declaration and no XHTML Syntax errors 
			file = createTestFile(XHTMLValidationTestMessages.XHTML_LARGE_CONTENT_TEMPLATE, 
					XHTMLValidationTestMessages.XHTML_WRONG_PUBLIC_ID, XHTMLValidationTestMessages.XHTML_WRONG_URI,
					XHTMLValidationTestMessages.XHTML_LARGE_GOOD_TAGNAME, XHTMLValidationTestMessages.XHTML_LARGE_GOOD_TAGNAME);
			start = System.currentTimeMillis();
			result = validator.validate(file, IResourceDelta.CHANGED, state, new NullProgressMonitor());
			long badValidationTime = System.currentTimeMillis() - start;
			System.out.println("Large XHTML file with bad DOCTYPE declaration and no XHTML Syntax errors validation time: " + badValidationTime + " ms");
			assertTrue("Large XHTML file with bad DOCTYPE declaration and no XHTML Syntax errors validation takes too much time (more than " + MAX_VALIDATION_TIME + " ms)", (badValidationTime < MAX_VALIDATION_TIME));
			assertNotNull("No validation result is returned", result);
			assertNotNull("No validation result is returned", result.getReporter(null));
			messages = result.getReporter(null).getMessages();
			assertEquals("Wrong number of error messages reported", 0, messages == null ? 0 : messages.size());
	
			// Check that the difference between good and bad files validation time is not greater that NOT_BAD_DIFF_PERCENTAGE (%) of a good value
			double diff = 100*badValidationTime/goodValidationTime;
			System.out.println("(Large With no errors) Validation time difference: " + diff + "%");
			assertTrue("Validation time difference between good and wrong content is greater than " + NOT_BAD_DIFF_PERCENTAGE + "%", (diff < NOT_BAD_DIFF_PERCENTAGE));
			
			// Validate file with good DOCTYPE declaration and XHTML Syntax errors 
			file = createTestFile(XHTMLValidationTestMessages.XHTML_LARGE_CONTENT_TEMPLATE, 
						XHTMLValidationTestMessages.XHTML_GOOD_PUBLIC_ID, XHTMLValidationTestMessages.XHTML_GOOD_URI,
						XHTMLValidationTestMessages.XHTML_LARGE_GOOD_TAGNAME, XHTMLValidationTestMessages.XHTML_LARGE_WRONG_TAGNAME);
			start = System.currentTimeMillis();
			result = validator.validate(file, IResourceDelta.CHANGED, state, new NullProgressMonitor());
			goodValidationTime = System.currentTimeMillis() - start;
			System.out.println("Large XHTML file with good DOCTYPE declaration and XHTML Syntax errors validation time: " + goodValidationTime + " ms");
			assertTrue("Large XHTML file with good DOCTYPE declaration and XHTML Syntax errors validation takes too much time (more than " + MAX_VALIDATION_TIME + " ms)", (goodValidationTime < MAX_VALIDATION_TIME));
			assertNotNull("No validation result is returned", result);
			assertNotNull("No validation result is returned", result.getReporter(null));
			messages = result.getReporter(null).getMessages();
			assertEquals("Wrong number of error messages reported", 180, messages == null ? 0 : messages.size());
			for (Object m : messages) {
				assertTrue("Wrong type of validation message is returned", (m instanceof Message));
				Message message = (Message)m;
//				System.out.println("Error Message: " + message.getText());
				assertTrue("Unexpected error message found: " + message.getText(), LOCALIZED_LARGE_ERROR_MESSAGES.contains(message.getText()));
			}
	
			// Validate file with bad DOCTYPE declaration and XHTML Syntax errors 
			file = createTestFile(XHTMLValidationTestMessages.XHTML_LARGE_CONTENT_TEMPLATE, 
					XHTMLValidationTestMessages.XHTML_WRONG_PUBLIC_ID, XHTMLValidationTestMessages.XHTML_WRONG_URI,
					XHTMLValidationTestMessages.XHTML_LARGE_GOOD_TAGNAME, XHTMLValidationTestMessages.XHTML_LARGE_WRONG_TAGNAME);
			start = System.currentTimeMillis();
			result = validator.validate(file, IResourceDelta.CHANGED, state, new NullProgressMonitor());
			badValidationTime = System.currentTimeMillis() - start;
			System.out.println("Large XHTML file with bad DOCTYPE declaration and XHTML Syntax errors validation time: " + badValidationTime + " ms");
			assertTrue("Large XHTML file with bad DOCTYPE declaration and XHTML Syntax errors validation takes too much time (more than " + MAX_VALIDATION_TIME + " ms)", (badValidationTime < MAX_VALIDATION_TIME));
			assertNotNull("No validation result is returned", result);
			assertNotNull("No validation result is returned", result.getReporter(null));
			messages = result.getReporter(null).getMessages();
			assertEquals("Wrong number of error messages reported", 180, messages == null ? 0 : messages.size());
			for (Object m : messages) {
				assertTrue("Wrong type of validation message is returned", (m instanceof Message));
				Message message = (Message)m;
//				System.out.println("Error Message: " + message.getText());
				assertTrue("Unexpected error message found: " + message.getText(), LOCALIZED_LARGE_ERROR_MESSAGES.contains(message.getText()));
			}
			// Check that the difference between good and bad files validation time is not greater that NOT_BAD_DIFF_PERCENTAGE (%) of a good value
			diff = 100*badValidationTime/goodValidationTime;
			System.out.println("(Large With errors) Validation time difference: " + diff + "%");
			assertTrue("Validation time difference between good and wrong content is greater than " + NOT_BAD_DIFF_PERCENTAGE + "%", (diff < NOT_BAD_DIFF_PERCENTAGE));
		} finally {
			removeTestFile();
		}
	}

	public void testBrokenEntityWebContentValidation() throws CoreException {
		XHTMLValidator validator = new XHTMLValidator();
		ValidationState state = new ValidationState();
		try {
			// Validate file with good DOCTYPE declaration and no XHTML Syntax errors 
			IFile file = createTestFile(XHTMLValidationTestMessages.XHTML_LARGE_CONTENT_TEMPLATE, 
					XHTMLValidationTestMessages.XHTML_GOOD_PUBLIC_ID, XHTMLValidationTestMessages.XHTML_GOOD_URI,
					XHTMLValidationTestMessages.XHTML_LARGE_GOOD_TAGNAME, XHTMLValidationTestMessages.XHTML_LARGE_GOOD_TAGNAME);
			ValidationResult result = validator.validate(file, IResourceDelta.CHANGED, state, new NullProgressMonitor());
		} catch (NullPointerException e) {
			fail("Validation on XHTML file with a 'broken' entity has failed with NullPointerException: " + e.getLocalizedMessage());
		} catch (ArrayIndexOutOfBoundsException e) {
			fail("Validation on XHTML file with a 'broken' entity has failed with ArrayIndexOutOfBoundsException: " + e.getLocalizedMessage());
		} finally {
			removeTestFile();
		}
	}

	private IFile createTestFile(String template, String publicId, String uri, String openingTagName, String closingTagName) {
		IFile testFile = project.getFile(FILE_NAME);
		String content = MessageFormat.format(template, publicId, uri, openingTagName, closingTagName);
		InputStream source = new StringBufferInputStream(content);
		try {
			if (testFile.exists()) {
				testFile.setContents(source, true, false, new NullProgressMonitor());
			} else {
				testFile.create(source, true, new NullProgressMonitor());
			}
		} catch (CoreException e) {
			fail(e.getLocalizedMessage());
		}
		return testFile;
	}

	private IFile createBrokenTestFile(String template, String publicId, String uri) {
		IFile testFile = project.getFile(FILE_NAME);
		String content = MessageFormat.format(template, publicId, uri);
		InputStream source = new StringBufferInputStream(content);
		try {
			if (testFile.exists()) {
				testFile.setContents(source, true, false, new NullProgressMonitor());
			} else {
				testFile.create(source, true, new NullProgressMonitor());
			}
		} catch (CoreException e) {
			fail(e.getLocalizedMessage());
		}
		return testFile;
	}
	

	private void removeTestFile() {
		IFile testFile = project.getFile(FILE_NAME);
		if (testFile.exists()) {
			try {
				testFile.delete(true, new NullProgressMonitor());
			} catch (CoreException e) {
				// Do not throw the exception
				e.printStackTrace();
			}
		}
	}
}
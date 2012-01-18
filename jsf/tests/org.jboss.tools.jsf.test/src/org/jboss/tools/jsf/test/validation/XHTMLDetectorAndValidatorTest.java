package org.jboss.tools.jsf.test.validation;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationState;
import org.eclipse.wst.validation.internal.core.Message;
import org.jboss.tools.common.base.test.validation.TestUtil;
import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.jsf.JSFModelPlugin;
import org.jboss.tools.jsf.web.validation.XHTMLValidator;
import org.jboss.tools.test.util.ResourcesUtils;
import org.jboss.tools.tests.AbstractResourceMarkerTest;
import org.osgi.framework.Bundle;

public class XHTMLDetectorAndValidatorTest extends AbstractResourceMarkerTest {

	protected static String PLUGIN_ID = "org.jboss.tools.jsf.test";
	protected static String PROJECT_NAME = "jsf2pr";
	protected static String PROJECT_PATH = "/projects/jsf2pr";
	
	protected static final String XHTML_FILE_NAME = "/XHTMLDetectorAndValidatorTest.xhtml";
	protected static final String HTML_FILE_NAME = "/XHTMLDetectorAndValidatorTest.html";

	// "Bad" file validation time should be not greater than "Good" file validation time multiplied by 10  
	protected static final double NOT_BAD_DIFF_PERCENTAGE = 1000.0;
	
	// Each validation session should take less that 1 second (1000ms)
	protected static final long MAX_VALIDATION_TIME = 1000;

	protected static final String[] XHTML_FILE_LIST = new String[] {
		"/xhtml_page_0.xhtml",
		"/xhtml_page_1.xhtml",
		"/xhtml_page_2.xhtml"
	};
	protected static final String[] HTML_FILE_LIST = new String[] {
		"/html_page_0.html",
		"/html_page_1.html",
		"/html_page_2.html"
	};
	
	
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

	public void testXHTMLDetectorAndValidator() throws CoreException {
		XHTMLValidator validator = new XHTMLValidator();
		ValidationState state = new ValidationState();
		try {
			// Detect and Validate on XHTML Content
			for (String source : XHTML_FILE_LIST) {
				IFile file = createTestFile(source, XHTML_FILE_NAME);
				IDocument document = getDocument(file);
				
				long start = System.currentTimeMillis();
				boolean xhtmlDetected = validator.isXHTML(document);
				long total = System.currentTimeMillis() - start;
				System.out.println("XHTML file [Source: " + source + "] detection time: " + total + " ms");
				assertTrue("XHTML file [Source: " + source + "] detection FAILED", xhtmlDetected);
				
				start = System.currentTimeMillis();
				ValidationResult result = validator.validate(file, IResourceDelta.CHANGED, state, new NullProgressMonitor());
				total = System.currentTimeMillis() - start;
				System.out.println("XHTML file [Source: " + source + "] validation time: " + total + " ms");
				assertTrue("XHTML file [Source: " + source + "] validation takes too much time (more than " + MAX_VALIDATION_TIME + " ms)", (total < MAX_VALIDATION_TIME));
				assertNotNull("No validation result is returned", result);
				assertNotNull("No validation result is returned", result.getReporter(null));
				List messages = result.getReporter(null).getMessages();
				System.out.println("Total error messages reported by XHTML file [Source: " + source + "] validation: " + (messages == null ? 0 : messages.size()));
				int i = 0;
				for (Object m : messages) {
					assertTrue("Wrong type of validation message is returned", (m instanceof Message));
					Message message = (Message)m;
					System.out.println("Message #" + (++i) + ": " + message.getText());
				}
			}
		} finally {
			removeTestFile(XHTML_FILE_NAME);
		}

		try {
			// Detect and Validate on NON-XHTML Content
			for (String source : HTML_FILE_LIST) {
				IFile file = createTestFile(source, HTML_FILE_NAME);
				IDocument document = getDocument(file);
				
				long start = System.currentTimeMillis();
				boolean xhtmlDetected = validator.isXHTML(document);
				long total = System.currentTimeMillis() - start;
				System.out.println("Non-XHTML file [Source: " + source + "] detection time: " + total + " ms");
				assertFalse("Non-XHTML file [Source: " + source + "] detection FAILED", xhtmlDetected);
				
				start = System.currentTimeMillis();
				ValidationResult result = validator.validate(file, IResourceDelta.CHANGED, state, new NullProgressMonitor());
				total = System.currentTimeMillis() - start;
				System.out.println("Non-XHTML file [Source: " + source + "] validation time: " + total + " ms");
				assertTrue("Non-XHTML file [Source: " + source + "] validation takes too much time (more than " + MAX_VALIDATION_TIME + " ms)", (total < MAX_VALIDATION_TIME));
				assertNotNull("No validation result is returned", result);
				assertNotNull("No validation result is returned", result.getReporter(null));
				List messages = result.getReporter(null).getMessages();
				System.out.println("Total error messages reported by Non-XHTML file [Source: " + source + "] validation: " + (messages == null ? 0 : messages.size()));
				assertEquals("Wrong number of error messages reported", 0, messages == null ? 0 : messages.size());
			}
		} finally {
			removeTestFile(HTML_FILE_NAME);
		}
	}

	private static final String SOURCE_FOLDER = "/resources/pages2validate";
	private static final String WEB_CONTENT_SUFFIX = "/WebContent";

	private IFile createTestFile(String sourceFile, String destinationFile) {
		IFile testFile = project.getFile(WEB_CONTENT_SUFFIX + destinationFile);

		try {
			Bundle b = Platform.getBundle(PLUGIN_ID);
			IProject p = ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT_NAME);
			String projectPath = p.getLocation().toOSString();
			String resourcePath = FileLocator.resolve(b.getEntry(SOURCE_FOLDER)).getFile();
	
			File from = new File(resourcePath + sourceFile);
			File to = new File(projectPath + WEB_CONTENT_SUFFIX + destinationFile);
			FileUtil.clear(to);
			if (!FileUtil.copyFile(from, to)) {
				return null; 
			}
			p.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
			TestUtil._waitForValidation(p);
		} catch (IOException e) {
			fail(e.getLocalizedMessage());
		} catch (CoreException e) {
			fail(e.getLocalizedMessage());
		}
		return testFile;
	}

	private void removeTestFile(String destinationFile) {
		IFile testFile = project.getFile(WEB_CONTENT_SUFFIX + destinationFile);
		if (testFile.exists()) {
			try {
				testFile.delete(true, new NullProgressMonitor());
			} catch (CoreException e) {
				// Do not throw the exception
				e.printStackTrace();
			}
		}
	}

	private IDocument getDocument(IFile file) {
		if (file == null) {
			return null;
		}

		String content;
		try {
			content = FileUtil.readStream(file);
		} catch (CoreException e) {
			JSFModelPlugin.getDefault().logError(e);
			return null;
		}

		return (content == null ? null : new Document(content));
	}

	
}

package org.jboss.tools.jsf.test.validation;

import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.xerces.impl.dv.dtd.NOTATIONDatatypeValidator;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationState;
import org.eclipse.wst.validation.internal.core.Message;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.xml.core.internal.validation.core.AbstractNestedValidator;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationReport;
import org.jboss.tools.common.base.test.validation.TestUtil;
import org.jboss.tools.jsf.web.validation.JSFValidationMessage;
import org.jboss.tools.jsf.web.validation.XHTMLValidator;
import org.jboss.tools.jsf.web.validation.i18n.I18nValidationComponent;
import org.jboss.tools.jst.web.kb.preferences.ELSeverityPreferences;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ResourcesUtils;
import org.jboss.tools.tests.AbstractResourceMarkerTest;

public class XHTMLValidatorTest extends AbstractResourceMarkerTest {

	protected static String PLUGIN_ID = "org.jboss.tools.jsf.test";
	protected static String PROJECT_NAME = "jsf2pr";
	protected static String PROJECT_PATH = "/projects/jsf2pr";
	
	protected static final String FILE_NAME = "WebContent/XHTMLValidatorTest.xhtml";
	protected static final double NOT_BAD_DIFF_PERCENTAGE = 200.0;

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

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
		project.delete(true, true, null);
		JobUtils.waitForIdle();
		ResourcesUtils.setBuildAutomatically(saveAutoBuild);
	}

	public void testWebContentValidation() throws CoreException {
		XHTMLValidator validator = new XHTMLValidator();
		ValidationState state = new ValidationState();
		try {
			// Validate good file with no XHTML Syntax errors 
			IFile file = createTestFile(XHTMLValidationTestMessages.XHTML_CONTENT_TEMPLATE, 
					XHTMLValidationTestMessages.XHTML_GOOD_PUBLIC_ID, XHTMLValidationTestMessages.XHTML_GOOD_URI,
					XHTMLValidationTestMessages.XHTML_GOOD_TAGNAME, XHTMLValidationTestMessages.XHTML_GOOD_TAGNAME);
			long start = System.currentTimeMillis();
			ValidationResult result = validator.validate(file, IResourceDelta.CHANGED, state, new NullProgressMonitor());
			long goodValidationTime = System.currentTimeMillis() - start;
			System.out.println("Good XHTML file with no XHTML Syntax errors validation time: " + goodValidationTime + " ms");
			assertNotNull("No validation result is returned", result);
			assertNotNull("No validation result is returned", result.getReporter(null));
			List messages = result.getReporter(null).getMessages();
			assertEquals("Wrong number of error messages reported", 0, messages == null ? 0 : messages.size());
			
			// Validate bad file with no XHTML Syntax errors 
			file = createTestFile(XHTMLValidationTestMessages.XHTML_CONTENT_TEMPLATE, 
					XHTMLValidationTestMessages.XHTML_WRONG_PUBLIC_ID, XHTMLValidationTestMessages.XHTML_WRONG_URI,
					XHTMLValidationTestMessages.XHTML_GOOD_TAGNAME, XHTMLValidationTestMessages.XHTML_GOOD_TAGNAME);
			start = System.currentTimeMillis();
			result = validator.validate(file, IResourceDelta.CHANGED, state, new NullProgressMonitor());
			long badValidationTime = System.currentTimeMillis() - start;
			System.out.println("Bad XHTML file with no XHTML Syntax errors validation time: " + badValidationTime + " ms");
			assertNotNull("No validation result is returned", result);
			assertNotNull("No validation result is returned", result.getReporter(null));
			messages = result.getReporter(null).getMessages();
			assertEquals("Wrong number of error messages reported", 0, messages == null ? 0 : messages.size());
	
			// Check that the difference between good and bad files validation time is not greater that NOT_BAD_DIFF_PERCENTAGE (%) of a good value
			double diff = 100*(badValidationTime - goodValidationTime)/goodValidationTime;
			System.out.println("(With no errors) Validation time difference: " + diff + "%");
			assertTrue("Validation time difference between good and wrong content is greater than " + NOT_BAD_DIFF_PERCENTAGE + "%", (diff < NOT_BAD_DIFF_PERCENTAGE));
			
			// Validate good file with XHTML Syntax errors 
			file = createTestFile(XHTMLValidationTestMessages.XHTML_CONTENT_TEMPLATE, 
						XHTMLValidationTestMessages.XHTML_GOOD_PUBLIC_ID, XHTMLValidationTestMessages.XHTML_GOOD_URI,
						XHTMLValidationTestMessages.XHTML_GOOD_TAGNAME, XHTMLValidationTestMessages.XHTML_WRONG_TAGNAME);
			start = System.currentTimeMillis();
			result = validator.validate(file, IResourceDelta.CHANGED, state, new NullProgressMonitor());
			goodValidationTime = System.currentTimeMillis() - start;
			System.out.println("Good XHTML file with XHTML Syntax errors validation time: " + goodValidationTime + " ms");
			assertNotNull("No validation result is returned", result);
			assertNotNull("No validation result is returned", result.getReporter(null));
			messages = result.getReporter(null).getMessages();
			assertEquals("Wrong number of error messages reported", 2, messages == null ? 0 : messages.size());
			Set<String> localizedErrorMessages = new HashSet<String>();
			localizedErrorMessages.add(MessageFormat.format(JSFValidationMessage.XHTML_VALIDATION_NO_END_TAG, 
					XHTMLValidationTestMessages.XHTML_GOOD_TAGNAME));
			localizedErrorMessages.add(MessageFormat.format(JSFValidationMessage.XHTML_VALIDATION_NO_START_TAG, 
					XHTMLValidationTestMessages.XHTML_WRONG_TAGNAME));
			for (Object m : messages) {
				assertTrue("Wrong type of validation message is returned", (m instanceof Message));
				Message message = (Message)m;
				assertTrue("Unexpected error message found: " + message.getText(), localizedErrorMessages.contains(message.getText()));
			}
	
			// Validate bad file with XHTML Syntax errors 
			file = createTestFile(XHTMLValidationTestMessages.XHTML_CONTENT_TEMPLATE, 
					XHTMLValidationTestMessages.XHTML_WRONG_PUBLIC_ID, XHTMLValidationTestMessages.XHTML_WRONG_URI,
					XHTMLValidationTestMessages.XHTML_GOOD_TAGNAME, XHTMLValidationTestMessages.XHTML_WRONG_TAGNAME);
			start = System.currentTimeMillis();
			result = validator.validate(file, IResourceDelta.CHANGED, state, new NullProgressMonitor());
			badValidationTime = System.currentTimeMillis() - start;
			System.out.println("Bad XHTML file with XHTML Syntax errors validation time: " + badValidationTime + " ms");
			assertNotNull("No validation result is returned", result);
			assertNotNull("No validation result is returned", result.getReporter(null));
			messages = result.getReporter(null).getMessages();
			assertEquals("Wrong number of error messages reported", 2, messages == null ? 0 : messages.size());
			localizedErrorMessages = new HashSet<String>();
			localizedErrorMessages.add(MessageFormat.format(JSFValidationMessage.XHTML_VALIDATION_NO_END_TAG, 
					XHTMLValidationTestMessages.XHTML_GOOD_TAGNAME));
			localizedErrorMessages.add(MessageFormat.format(JSFValidationMessage.XHTML_VALIDATION_NO_START_TAG, 
					XHTMLValidationTestMessages.XHTML_WRONG_TAGNAME));
			for (Object m : messages) {
				assertTrue("Wrong type of validation message is returned", (m instanceof Message));
				Message message = (Message)m;
				assertTrue("Unexpected error message found: " + message.getText(), localizedErrorMessages.contains(message.getText()));
			}
			// Check that the difference between good and bad files validation time is not greater that NOT_BAD_DIFF_PERCENTAGE (%) of a good value
			diff = 100*(badValidationTime - goodValidationTime)/goodValidationTime;
			System.out.println("(With errors) Validation time difference: " + diff + "%");
			assertTrue("Validation time difference between good and wrong content is greater than " + NOT_BAD_DIFF_PERCENTAGE + "%", (diff < NOT_BAD_DIFF_PERCENTAGE));
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
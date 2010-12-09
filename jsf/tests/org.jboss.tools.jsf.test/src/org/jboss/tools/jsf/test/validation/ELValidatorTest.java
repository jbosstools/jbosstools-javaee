package org.jboss.tools.jsf.test.validation;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.wst.validation.ValidationFramework;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.operations.WorkbenchReporter;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.jboss.tools.common.preferences.SeverityPreferences;
import org.jboss.tools.jsf.JSFModelPlugin;
import org.jboss.tools.jsf.preferences.JSFSeverityPreferences;
import org.jboss.tools.jsf.web.validation.ELValidator;
import org.jboss.tools.jsf.web.validation.JSFValidationMessages;
import org.jboss.tools.jst.web.kb.internal.validation.ContextValidationHelper;
import org.jboss.tools.jst.web.kb.internal.validation.ValidationErrorManager;
import org.jboss.tools.jst.web.kb.internal.validation.ValidatorManager;
import org.jboss.tools.jst.web.kb.validation.IValidator;
import org.jboss.tools.tests.AbstractResourceMarkerTest;

public class ELValidatorTest extends AbstractResourceMarkerTest{
	public static final String MARKER_TYPE = "org.eclipse.wst.validation.problemmarker";

	protected void setUp() throws Exception {
//		JobUtils.waitForIdle();
		project = ResourcesPlugin.getWorkspace().getRoot().getProject("JSFKickStartOldFormat");
//		this.project.build(IncrementalProjectBuilder.CLEAN_BUILD,
//				new NullProgressMonitor());
//		
//		JobUtils.waitForIdle();
	}

	public void testUnknownELVariable() throws CoreException, ValidationException {
		IPreferenceStore store = JSFModelPlugin.getDefault().getPreferenceStore();
		store.setValue(JSFSeverityPreferences.RE_VALIDATE_UNRESOLVED_EL, JSFSeverityPreferences.ENABLE);
		store.setValue(JSFSeverityPreferences.UNKNOWN_EL_VARIABLE_NAME, JSFSeverityPreferences.ERROR);

		try {
			copyContentsFile("WebContent/WEB-INF/faces-config.xml", "WebContent/WEB-INF/faces-config.1");

			assertMarkerIsCreatedForLine(
					"WebContent/testElRevalidation.xhtml",
					JSFValidationMessages.UNKNOWN_EL_VARIABLE_PROPERTY_NAME,
					new Object[] {"user"},
					14);

			// Check if the validator was not invoked.
			copyContentsFile("WebContent/WEB-INF/faces-config.xml", "WebContent/WEB-INF/faces-config.original");

			assertMarkerIsNotCreatedForLine(
					"WebContent/testElRevalidation.xhtml",
					JSFValidationMessages.UNKNOWN_EL_VARIABLE_PROPERTY_NAME,
					new Object[] {"user"},
					14);
		} finally {
			store.setValue(JSFSeverityPreferences.RE_VALIDATE_UNRESOLVED_EL, JSFSeverityPreferences.ENABLE);
			store.setValue(JSFSeverityPreferences.UNKNOWN_EL_VARIABLE_NAME, JSFSeverityPreferences.IGNORE);
		}
	}

	public void testRevalidationUnresolvedELs() throws CoreException, ValidationException{
		IPreferenceStore store = JSFModelPlugin.getDefault().getPreferenceStore();
		store.setValue(JSFSeverityPreferences.RE_VALIDATE_UNRESOLVED_EL, JSFSeverityPreferences.DISABLE);
		store.setValue(JSFSeverityPreferences.UNKNOWN_EL_VARIABLE_NAME, JSFSeverityPreferences.ERROR);

		try {
			copyContentsFile("WebContent/WEB-INF/faces-config.xml", "WebContent/WEB-INF/faces-config.1");

			assertMarkerIsCreatedForLine(
					"WebContent/testElRevalidation.xhtml",
					JSFValidationMessages.UNKNOWN_EL_VARIABLE_PROPERTY_NAME,
					new Object[] {"user"},
					14);

			IFile file = project.getFile("WebContent/testElRevalidation.xhtml");
			file.deleteMarkers(ELValidator.PROBLEM_TYPE, true, IResource.DEPTH_ZERO);

			assertMarkerIsNotCreatedForLine(
					"WebContent/testElRevalidation.xhtml",
					JSFValidationMessages.UNKNOWN_EL_VARIABLE_PROPERTY_NAME,
					new Object[] {"user"},
					14, false);

			// Check if the validator was not invoked.
			copyContentsFile("WebContent/WEB-INF/faces-config.xml", "WebContent/WEB-INF/faces-config.original");

			file = project.getFile("WebContent/WEB-INF/faces-config.xml");
			ValidationFramework.getDefault().validate(file, new NullProgressMonitor());

			assertMarkerIsNotCreatedForLine(
					"WebContent/testElRevalidation.xhtml",
					JSFValidationMessages.UNKNOWN_EL_VARIABLE_PROPERTY_NAME,
					new Object[] {"user"},
					14, false);
		} finally {
			store.setValue(JSFSeverityPreferences.RE_VALIDATE_UNRESOLVED_EL, JSFSeverityPreferences.ENABLE);
			store.setValue(JSFSeverityPreferences.UNKNOWN_EL_VARIABLE_NAME, JSFSeverityPreferences.IGNORE);
		}
	}

	/**
	 * See https://jira.jboss.org/browse/JBIDE-7067 
	 * @throws CoreException
	 * @throws ValidationException 
	 */
	public void testELValidationEnablement() throws CoreException, ValidationException {
		JSFModelPlugin.getDefault().getPreferenceStore().setValue(JSFSeverityPreferences.ENABLE_BLOCK_PREFERENCE_NAME, false);

		try {
			IFile file = project.getFile("WebContent/pages/el.jsp");

			file.deleteMarkers(IValidator.KB_PROBLEM_MARKER_TYPE, true, IResource.DEPTH_ZERO);
			validateFile("WebContent/pages/el.jsp", 0);

			int number = getMarkersNumberByGroupName(IValidator.KB_PROBLEM_MARKER_TYPE, file, IValidator.MARKED_RESOURCE_MESSAGE_GROUP);
			assertEquals("Problem marker was found.", 0, number);

			JSFModelPlugin.getDefault().getPreferenceStore().setValue(JSFSeverityPreferences.ENABLE_BLOCK_PREFERENCE_NAME, true);
			validateFile("WebContent/pages/el.jsp", 0);

			number = getMarkersNumberByGroupName(IValidator.KB_PROBLEM_MARKER_TYPE, file, IValidator.MARKED_RESOURCE_MESSAGE_GROUP);
			assertEquals("Problem marker was not found.", 1, number);
		} finally {
			JSFModelPlugin.getDefault().getPreferenceStore().setValue(JSFSeverityPreferences.ENABLE_BLOCK_PREFERENCE_NAME, true);
		}
	}

	/**
	 * See https://jira.jboss.org/browse/JBIDE-7147
	 * @throws CoreException
	 */
	public void testMaxNumberOfMarkersPerFileLesThanDefault() throws CoreException {
		IPreferenceStore store = JSFModelPlugin.getDefault().getPreferenceStore();
		int max = store.getInt(SeverityPreferences.MAX_NUMBER_OF_MARKERS_PREFERENCE_NAME);
		store.setValue(SeverityPreferences.MAX_NUMBER_OF_MARKERS_PREFERENCE_NAME, 1);
		String errorSeverity = store.getString(JSFSeverityPreferences.UNKNOWN_EL_VARIABLE_NAME);
		store.setValue(JSFSeverityPreferences.UNKNOWN_EL_VARIABLE_NAME, JSFSeverityPreferences.ERROR);

		try {
			assertMarkerIsCreatedForLine(
					"WebContent/pages/maxNumberOfMarkers.jsp",
					JSFValidationMessages.UNKNOWN_EL_VARIABLE_NAME,
					new Object[] {"wrongUserName"},
					3);
			assertMarkerIsNotCreatedForLine(
					"WebContent/pages/maxNumberOfMarkers.jsp",
					JSFValidationMessages.UNKNOWN_EL_VARIABLE_NAME,
					new Object[] {"wrongUserName2"},
					4);

			store.setValue(SeverityPreferences.MAX_NUMBER_OF_MARKERS_PREFERENCE_NAME, max);

			assertMarkerIsCreatedForLine(
					"WebContent/pages/maxNumberOfMarkers.jsp",
					JSFValidationMessages.UNKNOWN_EL_VARIABLE_NAME,
					new Object[] {"wrongUserName"},
					3);
			assertMarkerIsCreatedForLine(
					"WebContent/pages/maxNumberOfMarkers.jsp",
					JSFValidationMessages.UNKNOWN_EL_VARIABLE_NAME,
					new Object[] {"wrongUserName2"},
					4);
		} finally {
			store.setValue(SeverityPreferences.MAX_NUMBER_OF_MARKERS_PREFERENCE_NAME, max);
			store.setValue(JSFSeverityPreferences.UNKNOWN_EL_VARIABLE_NAME, errorSeverity);
		}
	}

	/**
	 * See https://jira.jboss.org/browse/JBIDE-7147
	 * @throws CoreException
	 */
	public void testMaxNumberOfMarkersPerFileMoreThanDefault() throws CoreException, ValidationException {
		IPreferenceStore store = JSFModelPlugin.getDefault().getPreferenceStore();
		store.setValue(JSFSeverityPreferences.UNKNOWN_EL_VARIABLE_NAME, JSFSeverityPreferences.ERROR);

		try {
			IFile file = project.getFile("WebContent/pages/lineNumbers.xhtml");
			String messagePattern = MessageFormat.format(JSFValidationMessages.UNKNOWN_EL_VARIABLE_NAME, new Object[] {"wrongUserName"});

			long time = validateFile("WebContent/pages/lineNumbers.xhtml", 100);
			System.out.println("Validation time: " + time);
			int[] lines = new int[100]; 
			for (int i = 8; i < 108; i++) {
				lines[i-8]=i;
			}
			assertMarkerIsCreated(file, ELValidator.PROBLEM_TYPE, messagePattern, lines);
			time = validateFile("WebContent/pages/lineNumbers.xhtml", 100);
			System.out.println("Validation time: " + time);
		} finally {
			store.setValue(JSFSeverityPreferences.UNKNOWN_EL_VARIABLE_NAME, JSFSeverityPreferences.IGNORE);
		}
	}

	/**
	 * See https://jira.jboss.org/browse/JBIDE-7264
	 * @throws CoreException 
	 * @throws ValidationException 
	 */
	public void testPerformanceOfCalculatingLineNumbers() throws CoreException, ValidationException {
		IPreferenceStore store = JSFModelPlugin.getDefault().getPreferenceStore();
		store.setValue(JSFSeverityPreferences.UNKNOWN_EL_VARIABLE_NAME, JSFSeverityPreferences.ERROR);
		try {
			IFile file = project.getFile("/pagesOutsideWebContent/lineCalculating.xhtml");
			assertTrue("Test xhtml file is not accessible.", file.isAccessible());
			ELValidator validator = getElValidator(file.getFullPath().toString());
			List<IMarker> markers = new ArrayList<IMarker>();
			long withoutLineNumber = System.currentTimeMillis();
			for (int i = 8; i < 108; i++) {
				IMarker marker = ValidationErrorManager.addError("test error", IMessage.HIGH_SEVERITY, new Object[0], -1, 1, 79397 + i, file, validator.getDocumentProvider(), "testMarkerId", this.getClass(), 100, "testMarkerType");
				assertNotNull("Marker has not been created.", marker);
				assertTrue("Wrong line number", marker.getAttribute(IMarker.LINE_NUMBER, -1)>1807);
				markers.add(marker);
			}
			withoutLineNumber = System.currentTimeMillis() - withoutLineNumber;
			for (IMarker marker : markers) {
				marker.delete();
			}

			markers.clear();
			long withLineNumber = System.currentTimeMillis();
			for (int i = 8; i < 108; i++) {
				IMarker marker = ValidationErrorManager.addError("test error", IMessage.HIGH_SEVERITY, new Object[0], i, 1, 79397 + i, file, validator.getDocumentProvider(), "testMarkerId", this.getClass(), 100, "testMarkerType");
				assertNotNull("Marker has not been created.", marker);
				assertEquals("Wrong line number", i, marker.getAttribute(IMarker.LINE_NUMBER, -1));
				markers.add(marker);
			}
			withLineNumber = System.currentTimeMillis() - withLineNumber;
			System.out.println("IMarker creation time with line calculating via IDocument: " + withoutLineNumber);
			System.out.println("IMarker creation time without line calculating: " + withLineNumber);
			assertTrue("", withLineNumber<withoutLineNumber);
		} finally {
			store.setValue(JSFSeverityPreferences.UNKNOWN_EL_VARIABLE_NAME, JSFSeverityPreferences.IGNORE);
		}
	}

	private long validateFile(String fileName, int numberOfMarkers) throws ValidationException {
		Set<String> files = new HashSet<String>();
		files.add(fileName);
		return validateFile(files, numberOfMarkers);
	}

	private long validateFile(Set<String> fileNames, int numberOfMarkers) throws ValidationException {
		IPreferenceStore store = JSFModelPlugin.getDefault().getPreferenceStore();
		int max = store.getInt(SeverityPreferences.MAX_NUMBER_OF_MARKERS_PREFERENCE_NAME);
		if(numberOfMarkers>0) {
			store.setValue(SeverityPreferences.MAX_NUMBER_OF_MARKERS_PREFERENCE_NAME, numberOfMarkers);
		}
		String errorSeverity = store.getString(JSFSeverityPreferences.UNKNOWN_EL_VARIABLE_NAME);
		store.setValue(JSFSeverityPreferences.UNKNOWN_EL_VARIABLE_NAME, JSFSeverityPreferences.ERROR);

		try {
			ELValidator validator = new ELValidator();

			ContextValidationHelper helper = new ContextValidationHelper();
			helper.setProject(project);
			helper.initialize();
			Set<IFile> files = new HashSet<IFile>();
			for (String fileName : fileNames) {
				IFile file = project.getFile(fileName);
				helper.registerResource(file);
				files.add(file);
			}
			ValidatorManager manager = new ValidatorManager();
			WorkbenchReporter reporter = new WorkbenchReporter(project, new NullProgressMonitor());

			long current = System.currentTimeMillis();
			validator.validate(files, project, helper, manager, reporter);
			long result = System.currentTimeMillis() - current;
			return result;
		} finally {
			store.setValue(SeverityPreferences.MAX_NUMBER_OF_MARKERS_PREFERENCE_NAME, max);
			store.setValue(JSFSeverityPreferences.UNKNOWN_EL_VARIABLE_NAME, errorSeverity);
		}
	}

	private ELValidator getElValidator(String fileName) throws ValidationException {
		Set<String> files = new HashSet<String>();
		files.add(fileName);
		return getElValidator(files);
	}

	private ELValidator getElValidator(Set<String> fileNames) {
		ELValidator validator = new ELValidator();

		ValidatorManager manager = new ValidatorManager();
		WorkbenchReporter reporter = new WorkbenchReporter(project, new NullProgressMonitor());
		validator.init(project, getHelper(fileNames), manager, reporter);
		return validator;
	}

	private ContextValidationHelper getHelper(Set<String> fileNames) {
		ContextValidationHelper helper = new ContextValidationHelper();
		helper.setProject(project);
		helper.initialize();
		Set<IFile> files = new HashSet<IFile>();
		for (String fileName : fileNames) {
			IFile file = project.getFile(fileName);
			helper.registerResource(file);
			files.add(file);
		}
		return helper;
	}

	private void assertMarkerIsCreatedForLine(String fileName, String template, Object[] parameters, int lineNumber) throws CoreException{
		assertMarkerIsCreatedForLine(fileName, template, parameters, lineNumber, true);
	}

	private void assertMarkerIsCreatedForLine(String fileName, String template, Object[] parameters, int lineNumber, boolean validate) throws CoreException{
		String messagePattern = MessageFormat.format(template, parameters);
		IFile file = project.getFile(fileName);

		if(validate) {
			ValidationFramework.getDefault().validate(file, new NullProgressMonitor());
		}

		IMarker[] markers = file.findMarkers(null, true, IResource.DEPTH_INFINITE);
		for (int i = 0; i < markers.length; i++) {
			String message = markers[i].getAttribute(IMarker.MESSAGE, ""); //$NON-NLS-1$
			int line = markers[i].getAttribute(IMarker.LINE_NUMBER, -1); //$NON-NLS-1$
			if(message.equals(messagePattern) && line == lineNumber)
				return;
		}
		fail("Marker "+messagePattern+" for line - "+lineNumber+" not found");
	}

	private void assertMarkerIsNotCreatedForLine(String fileName, String template, Object[] parameters, int lineNumber) throws CoreException{
		assertMarkerIsNotCreatedForLine(fileName, template, parameters, lineNumber, true);
	}

	private void assertMarkerIsNotCreatedForLine(String fileName, String template, Object[] parameters, int lineNumber, boolean validate) throws CoreException{
		String messagePattern = MessageFormat.format(template, parameters);
		IFile file = project.getFile(fileName);

		if(validate) {
			ValidationFramework.getDefault().validate(file, new NullProgressMonitor());
		}

		IMarker[] markers = file.findMarkers(null, true, IResource.DEPTH_INFINITE);
		for (int i = 0; i < markers.length; i++) {
			String message = markers[i].getAttribute(IMarker.MESSAGE, ""); //$NON-NLS-1$
			int line = markers[i].getAttribute(IMarker.LINE_NUMBER, -1); //$NON-NLS-1$
			if(message.equals(messagePattern) && line == lineNumber){
				fail("Marker "+messagePattern+" for line - "+lineNumber+" has been found");
			}
		}
	}
}
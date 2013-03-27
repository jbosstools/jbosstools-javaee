/*******************************************************************************
 * Copyright (c) 2007-2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.test.validation;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.wst.validation.ValidationFramework;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.operations.WorkbenchReporter;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.jboss.tools.common.base.test.validation.TestUtil;
import org.jboss.tools.common.el.core.ElCoreMessages;
import org.jboss.tools.common.preferences.SeverityPreferences;
import org.jboss.tools.common.validation.ContextValidationHelper;
import org.jboss.tools.common.validation.IProjectValidationContext;
import org.jboss.tools.common.validation.IValidator;
import org.jboss.tools.common.validation.ValidationErrorManager;
import org.jboss.tools.common.validation.ValidatorManager;
import org.jboss.tools.common.validation.internal.SimpleValidatingProjectTree;
import org.jboss.tools.jst.web.kb.WebKbPlugin;
import org.jboss.tools.jst.web.kb.internal.validation.ELValidationMessages;
import org.jboss.tools.jst.web.kb.internal.validation.ELValidator;
import org.jboss.tools.jst.web.kb.preferences.ELSeverityPreferences;
import org.jboss.tools.test.util.ProjectImportTestSetup;
import org.jboss.tools.tests.AbstractResourceMarkerTest;

public class ELValidatorTest extends AbstractResourceMarkerTest{
	public static final String MARKER_TYPE = "org.eclipse.wst.validation.problemmarker";
	public static final String EL_VALIDATOR_MARKER_TYPE = "org.jboss.tools.jst.web.kb.elproblem";

	protected void setUp() throws Exception {
//		JobUtils.waitForIdle();
		project = ProjectImportTestSetup.loadProject("JSFKickStartOldFormat");

//		this.project.build(IncrementalProjectBuilder.CLEAN_BUILD,
//				new NullProgressMonitor());
//		
//		JobUtils.waitForIdle();
	}

	public void testPropertyInBrackets() throws CoreException, ValidationException {
		MarkerAssertUtil.assertMarkerIsCreatedForLine(project,
				"WebContent/pages/inputname.jsp",
				ELValidationMessages.UNKNOWN_EL_VARIABLE_PROPERTY_NAME,
				new Object[] {"'age1'"},
				20);
		MarkerAssertUtil.assertMarkerIsNotCreatedForLine(project,
				"WebContent/pages/inputname.jsp",
				ELValidationMessages.UNKNOWN_EL_VARIABLE_PROPERTY_NAME,
				new Object[] {"'age'"},
				19);
	}

	public void testUnknownELVariable() throws CoreException, ValidationException {
		IPreferenceStore store = WebKbPlugin.getDefault().getPreferenceStore();
		store.setValue(ELSeverityPreferences.RE_VALIDATE_UNRESOLVED_EL, ELSeverityPreferences.ENABLE);
		store.setValue(ELSeverityPreferences.UNKNOWN_EL_VARIABLE_NAME, ELSeverityPreferences.ERROR);

		try {
			copyContentsFile("WebContent/WEB-INF/faces-config.xml", "WebContent/WEB-INF/faces-config.1");

			MarkerAssertUtil.assertMarkerIsCreatedForLine(project,
					"WebContent/testElRevalidation.xhtml",
					ELValidationMessages.UNKNOWN_EL_VARIABLE_PROPERTY_NAME,
					new Object[] {"user"},
					14);

			// Check if the validator was not invoked.
			copyContentsFile("WebContent/WEB-INF/faces-config.xml", "WebContent/WEB-INF/faces-config.original");

			MarkerAssertUtil.assertMarkerIsNotCreatedForLine(project,
					"WebContent/testElRevalidation.xhtml",
					ELValidationMessages.UNKNOWN_EL_VARIABLE_PROPERTY_NAME,
					new Object[] {"user"},
					14);
		} finally {
			copyContentsFile("WebContent/WEB-INF/faces-config.xml", "WebContent/WEB-INF/faces-config.original");
			store.setValue(ELSeverityPreferences.RE_VALIDATE_UNRESOLVED_EL, ELSeverityPreferences.ENABLE);
			store.setValue(ELSeverityPreferences.UNKNOWN_EL_VARIABLE_NAME, ELSeverityPreferences.IGNORE);
		}
	}

	public void _testRevalidationUnresolvedELs() throws CoreException, ValidationException{
		IPreferenceStore store = WebKbPlugin.getDefault().getPreferenceStore();
		store.setValue(ELSeverityPreferences.RE_VALIDATE_UNRESOLVED_EL, ELSeverityPreferences.DISABLE);
		store.setValue(ELSeverityPreferences.UNKNOWN_EL_VARIABLE_NAME, ELSeverityPreferences.ERROR);

		try {
			copyContentsFile("WebContent/WEB-INF/faces-config.xml", "WebContent/WEB-INF/faces-config.1");

			MarkerAssertUtil.assertMarkerIsCreatedForLine(project,
					"WebContent/testElRevalidation.xhtml",
					ELValidationMessages.UNKNOWN_EL_VARIABLE_PROPERTY_NAME,
					new Object[] {"user"},
					14);

			IFile file = project.getFile("WebContent/testElRevalidation.xhtml");
			file.deleteMarkers(EL_VALIDATOR_MARKER_TYPE, true, IResource.DEPTH_ZERO);

			MarkerAssertUtil.assertMarkerIsNotCreatedForLine(project,
					"WebContent/testElRevalidation.xhtml",
					ELValidationMessages.UNKNOWN_EL_VARIABLE_PROPERTY_NAME,
					new Object[] {"user"},
					14, false);

			// Check if the validator was not invoked.
			copyContentsFile("WebContent/WEB-INF/faces-config.xml", "WebContent/WEB-INF/faces-config.original");

			file = project.getFile("WebContent/WEB-INF/faces-config.xml");
			ValidationFramework.getDefault().validate(file, new NullProgressMonitor());

			MarkerAssertUtil.assertMarkerIsNotCreatedForLine(project,
					"WebContent/testElRevalidation.xhtml",
					ELValidationMessages.UNKNOWN_EL_VARIABLE_PROPERTY_NAME,
					new Object[] {"user"},
					14, false);
		} finally {
			store.setValue(ELSeverityPreferences.RE_VALIDATE_UNRESOLVED_EL, ELSeverityPreferences.ENABLE);
			store.setValue(ELSeverityPreferences.UNKNOWN_EL_VARIABLE_NAME, ELSeverityPreferences.IGNORE);
		}
	}

	/**
	 * See https://jira.jboss.org/browse/JBIDE-7067 
	 * @throws CoreException
	 * @throws ValidationException 
	 */
	public void testELValidationEnablement() throws CoreException, ValidationException {
		WebKbPlugin.getDefault().getPreferenceStore().setValue(ELSeverityPreferences.ENABLE_BLOCK_PREFERENCE_NAME, SeverityPreferences.DISABLE);

		try {
			IFile file = project.getFile("WebContent/pages/el.jsp");

			file.deleteMarkers(IValidator.KB_PROBLEM_MARKER_TYPE, true, IResource.DEPTH_ZERO);
			validateFile("WebContent/pages/el.jsp", 0);

			int number = getMarkersNumberByGroupName(IValidator.KB_PROBLEM_MARKER_TYPE, file, IValidator.MARKED_RESOURCE_MESSAGE_GROUP);
			assertEquals("Problem marker was found.", 0, number);

			WebKbPlugin.getDefault().getPreferenceStore().setValue(ELSeverityPreferences.ENABLE_BLOCK_PREFERENCE_NAME, SeverityPreferences.ENABLE);
			validateFile("WebContent/pages/el.jsp", 0);

			number = getMarkersNumberByGroupName(IValidator.KB_PROBLEM_MARKER_TYPE, file, IValidator.MARKED_RESOURCE_MESSAGE_GROUP);
			assertEquals("Problem marker was not found.", 1, number);
		} finally {
			WebKbPlugin.getDefault().getPreferenceStore().setValue(ELSeverityPreferences.ENABLE_BLOCK_PREFERENCE_NAME, SeverityPreferences.ENABLE);
		}
	}

	/**
	 * See https://jira.jboss.org/browse/JBIDE-7147
	 * @throws CoreException
	 */
	public void testMaxNumberOfMarkersPerFileLesThanDefault() throws CoreException {
		IPreferenceStore store = WebKbPlugin.getDefault().getPreferenceStore();
		int max = store.getInt(SeverityPreferences.MAX_NUMBER_OF_MARKERS_PREFERENCE_NAME);
		store.setValue(SeverityPreferences.MAX_NUMBER_OF_MARKERS_PREFERENCE_NAME, 1);
		String errorSeverity = store.getString(ELSeverityPreferences.UNKNOWN_EL_VARIABLE_NAME);
		store.setValue(ELSeverityPreferences.UNKNOWN_EL_VARIABLE_NAME, ELSeverityPreferences.ERROR);

		try {
			MarkerAssertUtil.assertMarkerIsCreatedForLine(project,
					"WebContent/pages/maxNumberOfMarkers.jsp",
					ELValidationMessages.UNKNOWN_EL_VARIABLE_NAME,
					new Object[] {"wrongUserName"},
					3);
			MarkerAssertUtil.assertMarkerIsNotCreatedForLine(project,
					"WebContent/pages/maxNumberOfMarkers.jsp",
					ELValidationMessages.UNKNOWN_EL_VARIABLE_NAME,
					new Object[] {"wrongUserName2"},
					4);

			store.setValue(SeverityPreferences.MAX_NUMBER_OF_MARKERS_PREFERENCE_NAME, max);

			MarkerAssertUtil.assertMarkerIsCreatedForLine(project,
					"WebContent/pages/maxNumberOfMarkers.jsp",
					ELValidationMessages.UNKNOWN_EL_VARIABLE_NAME,
					new Object[] {"wrongUserName"},
					3);
			MarkerAssertUtil.assertMarkerIsCreatedForLine(project,
					"WebContent/pages/maxNumberOfMarkers.jsp",
					ELValidationMessages.UNKNOWN_EL_VARIABLE_NAME,
					new Object[] {"wrongUserName2"},
					4);
		} finally {
			store.setValue(SeverityPreferences.MAX_NUMBER_OF_MARKERS_PREFERENCE_NAME, max);
			store.setValue(ELSeverityPreferences.UNKNOWN_EL_VARIABLE_NAME, errorSeverity);
		}
	}

	/**
	 * See https://jira.jboss.org/browse/JBIDE-7147
	 * @throws CoreException
	 */
	public void testMaxNumberOfMarkersPerFileMoreThanDefault() throws CoreException, ValidationException {
		IPreferenceStore store = WebKbPlugin.getDefault().getPreferenceStore();
		store.setValue(ELSeverityPreferences.UNKNOWN_EL_VARIABLE_NAME, ELSeverityPreferences.ERROR);

		try {
			IFile file = project.getFile("WebContent/pages/lineNumbers.xhtml");
			String messagePattern = MessageFormat.format(ELValidationMessages.UNKNOWN_EL_VARIABLE_NAME, new Object[] {"wrongUserName"});

			long time = validateFile("WebContent/pages/lineNumbers.xhtml", 100);
			System.out.println("Validation time: " + time);
			int[] lines = new int[100]; 
			for (int i = 8; i < 108; i++) {
				lines[i-8]=i;
			}
			assertMarkerIsCreated(file, EL_VALIDATOR_MARKER_TYPE, messagePattern, lines);
			time = validateFile("WebContent/pages/lineNumbers.xhtml", 100);
			System.out.println("Validation time: " + time);
		} finally {
			store.setValue(ELSeverityPreferences.UNKNOWN_EL_VARIABLE_NAME, ELSeverityPreferences.IGNORE);
		}
	}

	/**
	 * See https://jira.jboss.org/browse/JBIDE-7264
	 * @throws CoreException 
	 * @throws ValidationException 
	 */
	public void testPerformanceOfCalculatingLineNumbers() throws CoreException, ValidationException {
		IPreferenceStore store = WebKbPlugin.getDefault().getPreferenceStore();
		store.setValue(ELSeverityPreferences.UNKNOWN_EL_VARIABLE_NAME, ELSeverityPreferences.ERROR);
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
			store.setValue(ELSeverityPreferences.UNKNOWN_EL_VARIABLE_NAME, ELSeverityPreferences.IGNORE);
		}
	}

	/**
	 * See https://jira.jboss.org/browse/JBIDE-10661 
	 * @throws CoreException
	 * @throws ValidationException 
	 */
	public void testSyntaxErrors() throws CoreException, ValidationException {
		IFile file = project.getFile("WebContent/pages/syntaxErrors.xhtml");

		TestUtil.validate(file);

		String messagePattern = MessageFormat.format(ELValidationMessages.EL_SYNTAX_ERROR, new Object[]{ElCoreMessages.ExpressionRule_ExpectingJavaName});
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, messagePattern, false, 7, 8);

		messagePattern = MessageFormat.format(ELValidationMessages.UNKNOWN_EL_VARIABLE_NAME, new Object[]{"abc."});
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, messagePattern, 7);

		messagePattern = MessageFormat.format(ELValidationMessages.UNKNOWN_EL_VARIABLE_PROPERTY_NAME, new Object[]{"broken"});
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, messagePattern, false, 8);
	}

	public void testSyntaxErrorsInXML() throws CoreException, ValidationException {
		IFile file = project.getFile("WebContent/pages/a.xml");

		TestUtil.validate(file);

		String messagePattern = MessageFormat.format(ELValidationMessages.EL_SYNTAX_ERROR, new Object[]{ElCoreMessages.OperationRule_ExpectingRBrace});
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, messagePattern, false, 9);

		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, messagePattern, 8);
	}

	private long validateFile(String fileName, int numberOfMarkers) throws ValidationException {
		Set<String> files = new HashSet<String>();
		files.add(fileName);
		return validateFile(files, numberOfMarkers);
	}

	private long validateFile(Set<String> fileNames, int numberOfMarkers) throws ValidationException {
		IPreferenceStore store = WebKbPlugin.getDefault().getPreferenceStore();
		int max = store.getInt(SeverityPreferences.MAX_NUMBER_OF_MARKERS_PREFERENCE_NAME);
		if(numberOfMarkers>0) {
			store.setValue(SeverityPreferences.MAX_NUMBER_OF_MARKERS_PREFERENCE_NAME, numberOfMarkers);
		}
		String errorSeverity = store.getString(ELSeverityPreferences.UNKNOWN_EL_VARIABLE_NAME);
		store.setValue(ELSeverityPreferences.UNKNOWN_EL_VARIABLE_NAME, ELSeverityPreferences.ERROR);

		try {
			ELValidator validator = new ELValidator();
			validator.setProblemType(EL_VALIDATOR_MARKER_TYPE);

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
			IProjectValidationContext context = new SimpleValidatingProjectTree(project).getBrunches().values().iterator().next().getRootContext();
			validator.validate(files, project, helper, context, manager, reporter);
			long result = System.currentTimeMillis() - current;
			return result;
		} finally {
			store.setValue(SeverityPreferences.MAX_NUMBER_OF_MARKERS_PREFERENCE_NAME, max);
			store.setValue(ELSeverityPreferences.UNKNOWN_EL_VARIABLE_NAME, errorSeverity);
		}
	}

	private ELValidator getElValidator(String fileName) throws ValidationException {
		Set<String> files = new HashSet<String>();
		files.add(fileName);
		return getElValidator(files);
	}

	private ELValidator getElValidator(Set<String> fileNames) {
		ELValidator validator = new ELValidator();
		validator.setProblemType(EL_VALIDATOR_MARKER_TYPE);

		ValidatorManager manager = new ValidatorManager();
		WorkbenchReporter reporter = new WorkbenchReporter(project, new NullProgressMonitor());
		IProjectValidationContext context = new SimpleValidatingProjectTree(project).getBrunches().values().iterator().next().getRootContext();
		validator.init(project, getHelper(fileNames), context, manager, reporter);
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

}
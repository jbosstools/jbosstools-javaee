package org.jboss.tools.jsf.test.validation;

import java.io.IOException;
import java.text.MessageFormat;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.wst.validation.ValidationFramework;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.jboss.tools.jsf.JSFModelPlugin;
import org.jboss.tools.jsf.preferences.JSFSeverityPreferences;
import org.jboss.tools.jsf.web.validation.JSFValidationMessages;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.tests.AbstractResourceMarkerTest;

public class ELValidatorTest extends AbstractResourceMarkerTest{
	public static final String MARKER_TYPE = "org.eclipse.wst.validation.problemmarker";
	
	protected void setUp() throws Exception {
		JobUtils.waitForIdle();
		IResource project = ResourcesPlugin.getWorkspace().getRoot().findMember("JSFKickStartOldFormat");
		this.project = project.getProject();
		
		this.project.build(IncrementalProjectBuilder.CLEAN_BUILD,
				new NullProgressMonitor());
		
		JobUtils.waitForIdle();
	}

	public void testUnknownELVariable() throws CoreException, ValidationException {
		
		IPreferenceStore store = JSFModelPlugin.getDefault().getPreferenceStore();
		store.setValue(JSFSeverityPreferences.RE_VALIDATE_UNRESOLVED_EL, JSFSeverityPreferences.ENABLE);
		store.setValue(JSFSeverityPreferences.UNKNOWN_EL_VARIABLE_NAME, JSFSeverityPreferences.ERROR);

		copyContentsFile("WebContent/WEB-INF/faces-config.xml", "WebContent/WEB-INF/faces-config.1");
		
		assertMarkerIsCreatedForLine(
				"WebContent/testElRevalidation.xhtml",
				JSFValidationMessages.UNKNOWN_EL_VARIABLE_PROPERTY_NAME,
				new Object[] {"user"},
				13);

		// Check if the validator was not invoked.
		copyContentsFile("WebContent/WEB-INF/faces-config.xml", "WebContent/WEB-INF/faces-config.original");
		
		assertMarkerIsNotCreatedForLine(
				"WebContent/testElRevalidation.xhtml",
				JSFValidationMessages.UNKNOWN_EL_VARIABLE_PROPERTY_NAME,
				new Object[] {"user"},
				13);
		
		store.setValue(JSFSeverityPreferences.RE_VALIDATE_UNRESOLVED_EL, JSFSeverityPreferences.ENABLE);
		store.setValue(JSFSeverityPreferences.UNKNOWN_EL_VARIABLE_NAME, JSFSeverityPreferences.IGNORE);
	}

	public void testRevalidationUnresolvedELs() throws CoreException, ValidationException{
		IPreferenceStore store = JSFModelPlugin.getDefault().getPreferenceStore();
		store.setValue(JSFSeverityPreferences.RE_VALIDATE_UNRESOLVED_EL, JSFSeverityPreferences.DISABLE);
		store.setValue(JSFSeverityPreferences.UNKNOWN_EL_VARIABLE_NAME, JSFSeverityPreferences.ERROR);
		
		copyContentsFile("WebContent/WEB-INF/faces-config.xml", "WebContent/WEB-INF/faces-config.1");
		
		assertMarkerIsCreatedForLine(
				"WebContent/testElRevalidation.xhtml",
				JSFValidationMessages.UNKNOWN_EL_VARIABLE_PROPERTY_NAME,
				new Object[] {"user"},
				13);

		// Check if the validator was not invoked.
		copyContentsFile("WebContent/WEB-INF/faces-config.xml", "WebContent/WEB-INF/faces-config.original");
		
		assertMarkerIsNotCreatedForLine(
				"WebContent/testElRevalidation.xhtml",
				JSFValidationMessages.UNKNOWN_EL_VARIABLE_PROPERTY_NAME,
				new Object[] {"user"},
				13);

		store.setValue(JSFSeverityPreferences.RE_VALIDATE_UNRESOLVED_EL, JSFSeverityPreferences.ENABLE);
		store.setValue(JSFSeverityPreferences.UNKNOWN_EL_VARIABLE_NAME, JSFSeverityPreferences.IGNORE);
	}
	
	private void assertMarkerIsCreatedForLine(String fileName, String template, Object[] parameters, int lineNumber) throws CoreException{
		String messagePattern = MessageFormat.format(template, parameters);
		IFile file = project.getFile(fileName);
		
		ValidationFramework.getDefault().validate(file, new NullProgressMonitor());

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
		String messagePattern = MessageFormat.format(template, parameters);
		IFile file = project.getFile(fileName);
		
		ValidationFramework.getDefault().validate(file, new NullProgressMonitor());

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

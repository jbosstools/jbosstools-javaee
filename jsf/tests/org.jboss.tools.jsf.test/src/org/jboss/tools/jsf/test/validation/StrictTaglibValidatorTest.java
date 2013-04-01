/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.test.validation;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.wst.validation.ValidationFramework;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.jboss.tools.jsf.web.validation.JSFSeverityPreferences;
import org.jboss.tools.jsf.web.validation.JSFValidationMessage;
import org.jboss.tools.test.util.ProjectImportTestSetup;
import org.jboss.tools.tests.AbstractResourceMarkerTest;

public class StrictTaglibValidatorTest extends AbstractResourceMarkerTest {
	public static final String STRICT_TAGLIB_VALIDATOR_MARKER_TYPE = "org.jboss.tools.jst.web.kb.elproblem";
	
	private static final String TEST_FILE = "WebContent/strictTaglibValidation.xhtml";
		
	protected void setUp() throws Exception {
		project = ProjectImportTestSetup.loadProject("JSFKickStartOldFormat");
	}
	
	public void testStrictTagLibValidator() throws CoreException, ValidationException {
		
		// Get the preferences values
		IEclipsePreferences prefs = JSFSeverityPreferences.getInstance().getProjectPreferences(project);
		String unknownTaglibComponentPreferenceValue = prefs.get(JSFSeverityPreferences.UNKNOWN_TAGLIB_COMPONENT, JSFSeverityPreferences.IGNORE);
		String unknownTaglibAttributePreferenceValue = prefs.get(JSFSeverityPreferences.UNKNOWN_TAGLIB_COMPONENT, JSFSeverityPreferences.IGNORE);

		IFile file = project.getFile(TEST_FILE);
		try {
			// Disable the Strict Taglib Components/Attributes validation
			prefs.put(JSFSeverityPreferences.UNKNOWN_TAGLIB_COMPONENT, JSFSeverityPreferences.IGNORE);
			prefs.put(JSFSeverityPreferences.UNKNOWN_TAGLIB_ATTRIBUTE, JSFSeverityPreferences.IGNORE);
			file.deleteMarkers(STRICT_TAGLIB_VALIDATOR_MARKER_TYPE, true, IResource.DEPTH_ZERO);
			ValidationFramework.getDefault().validate(file, new NullProgressMonitor());
			MarkerAssertUtil.assertMarkerIsNotCreatedForLine(project,
					TEST_FILE,
					JSFValidationMessage.UNKNOWN_TAGLIB_COMPONENT_NAME,
					new Object[] {"h:outputTextLine"},
					8, false);
			
			MarkerAssertUtil.assertMarkerIsNotCreatedForLine(project,
					TEST_FILE,
					JSFValidationMessage.UNKNOWN_TAGLIB_COMPONENT_ATTRIBUTE,
					new Object[] {"textLineValue", "h:outputText"},
					9, false);

			// Set the WARMING Severity for the Strict Taglib Components/Attributes validator
			prefs.put(JSFSeverityPreferences.UNKNOWN_TAGLIB_COMPONENT, JSFSeverityPreferences.WARNING);
			prefs.put(JSFSeverityPreferences.UNKNOWN_TAGLIB_ATTRIBUTE, JSFSeverityPreferences.WARNING);
			file.deleteMarkers(STRICT_TAGLIB_VALIDATOR_MARKER_TYPE, true, IResource.DEPTH_ZERO);
			ValidationFramework.getDefault().validate(file, new NullProgressMonitor());
			MarkerAssertUtil.assertMarkerIsCreatedForLine(project,
					TEST_FILE,
					JSFValidationMessage.UNKNOWN_TAGLIB_COMPONENT_NAME,
					new Object[] {"h:outputTextLine"},
					8, false);
			
			MarkerAssertUtil.assertMarkerIsCreatedForLine(project,
					TEST_FILE,
					JSFValidationMessage.UNKNOWN_TAGLIB_COMPONENT_ATTRIBUTE,
					new Object[] {"textLineValue", "h:outputText"},
					9, false);
			
			// Set the ERROR Severity for the Strict Taglib Components/Attributes validator
			prefs.put(JSFSeverityPreferences.UNKNOWN_TAGLIB_COMPONENT, JSFSeverityPreferences.ERROR);
			prefs.put(JSFSeverityPreferences.UNKNOWN_TAGLIB_ATTRIBUTE, JSFSeverityPreferences.ERROR);
			file.deleteMarkers(STRICT_TAGLIB_VALIDATOR_MARKER_TYPE, true, IResource.DEPTH_ZERO);
			ValidationFramework.getDefault().validate(file, new NullProgressMonitor());
			MarkerAssertUtil.assertMarkerIsCreatedForLine(project,
					TEST_FILE,
					JSFValidationMessage.UNKNOWN_TAGLIB_COMPONENT_NAME,
					new Object[] {"h:outputTextLine"},
					8, false);
			
			MarkerAssertUtil.assertMarkerIsCreatedForLine(project,
					TEST_FILE,
					JSFValidationMessage.UNKNOWN_TAGLIB_COMPONENT_ATTRIBUTE,
					new Object[] {"textLineValue", "h:outputText"},
					9, false);

		} finally {
			prefs.put(JSFSeverityPreferences.UNKNOWN_TAGLIB_COMPONENT, unknownTaglibComponentPreferenceValue);
			prefs.put(JSFSeverityPreferences.UNKNOWN_TAGLIB_ATTRIBUTE, unknownTaglibAttributePreferenceValue);
		}
	}
}

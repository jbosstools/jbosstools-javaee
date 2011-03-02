/*******************************************************************************
 * Copyright (c) 2007-2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.test.validation;

import java.util.Arrays;
import java.util.Comparator;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.wst.validation.ValidationFramework;
import org.jboss.tools.jsf.web.validation.i18n.I18nValidationComponent;
import org.jboss.tools.jst.web.kb.WebKbPlugin;
import org.jboss.tools.jst.web.kb.preferences.ELSeverityPreferences;
import org.jboss.tools.tests.AbstractResourceMarkerTest;

/**
 * Test class for Externalize String Validator
 * 
 * @author mareshkau
 * 
 */
public class I18nValidatorTest extends AbstractResourceMarkerTest {

	private IPreferenceStore store;
	private IFile testFile;
	private static final int NUMBER_OF_EXT_PROBLEMS_IN_FILE = 3;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		setProject(ResourcesPlugin.getWorkspace().getRoot()
				.getProject("i18nTestProject")); //$NON-NLS-1$
		store = WebKbPlugin.getDefault().getPreferenceStore();
		testFile = getProject().getFile("WebContent/externalization-validator-test-case-1.xhtml"); //$NON-NLS-1$
	}

	public void testShowErrorMarkers() throws CoreException {
		store.setValue(ELSeverityPreferences.NON_EXTERNALIZED_STRINGS, ELSeverityPreferences.WARNING);
		testFile.deleteMarkers(I18nValidationComponent.PROBLEM_ID, true, IResource.DEPTH_ZERO);
		IMarker[] elMarkers = testFile.findMarkers(I18nValidationComponent.PROBLEM_ID, true, IResource.DEPTH_ZERO);
		assertEquals("Markers should be cleaned", 0,elMarkers.length); //$NON-NLS-1$
		ValidationFramework.getDefault().validate(testFile, new NullProgressMonitor());
		elMarkers = testFile.findMarkers(I18nValidationComponent.PROBLEM_ID, true, IResource.DEPTH_ZERO);
		assertEquals("Should be "+NUMBER_OF_EXT_PROBLEMS_IN_FILE+" Markers", NUMBER_OF_EXT_PROBLEMS_IN_FILE,elMarkers.length); //$NON-NLS-1$ //$NON-NLS-2$	
	}

	public void testDefaultStateI19nValidator() {
		assertEquals(
				"By Default I18nValidator should be ignored", ELSeverityPreferences.IGNORE,//$NON-NLS-1$
				store.getDefaultString(ELSeverityPreferences.NON_EXTERNALIZED_STRINGS)); 
	}
	
	public void testChangeMarkerSeverity() throws CoreException {
		testFile.deleteMarkers(I18nValidationComponent.PROBLEM_ID, true, IResource.DEPTH_ZERO);
		IMarker[] elMarkers = testFile.findMarkers(I18nValidationComponent.PROBLEM_ID, true, IResource.DEPTH_ZERO);
		//changing severity level on ignoring(disable validator)
		store.setValue(ELSeverityPreferences.NON_EXTERNALIZED_STRINGS, ELSeverityPreferences.IGNORE);
		assertEquals("Markers should be cleaned", 0,elMarkers.length); //$NON-NLS-1$
		ValidationFramework.getDefault().validate(testFile, new NullProgressMonitor());
		elMarkers = testFile.findMarkers(I18nValidationComponent.PROBLEM_ID, true, IResource.DEPTH_ZERO);
		assertEquals("There shouldn't be any validation markers", 0,elMarkers.length); //$NON-NLS-1$
		//changing severity level on warning
		store.setValue(ELSeverityPreferences.NON_EXTERNALIZED_STRINGS, ELSeverityPreferences.WARNING);
		ValidationFramework.getDefault().validate(testFile, new NullProgressMonitor());
		elMarkers = testFile.findMarkers(I18nValidationComponent.PROBLEM_ID, true, IResource.DEPTH_ZERO);
		assertEquals("There shouldn't be "+NUMBER_OF_EXT_PROBLEMS_IN_FILE+" validation markers", NUMBER_OF_EXT_PROBLEMS_IN_FILE,elMarkers.length); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals("Marker Severity should be warning",(Integer)IMarker.SEVERITY_WARNING, (Integer)elMarkers[0].getAttribute(IMarker.SEVERITY));//$NON-NLS-1$
	
		//changing severity level on warning
		store.setValue(ELSeverityPreferences.NON_EXTERNALIZED_STRINGS, ELSeverityPreferences.ERROR);
		ValidationFramework.getDefault().validate(testFile, new NullProgressMonitor());
		elMarkers = testFile.findMarkers(I18nValidationComponent.PROBLEM_ID, true, IResource.DEPTH_ZERO);
		assertEquals("There shouldn't be "+NUMBER_OF_EXT_PROBLEMS_IN_FILE+" validation markers", NUMBER_OF_EXT_PROBLEMS_IN_FILE,elMarkers.length); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals("Marker Severity should be warning",(Integer)IMarker.SEVERITY_ERROR, (Integer)elMarkers[0].getAttribute(IMarker.SEVERITY));//$NON-NLS-1$
	}
	
	public void testRegionLineIsCorrect() throws CoreException{
		store.setValue(ELSeverityPreferences.NON_EXTERNALIZED_STRINGS, ELSeverityPreferences.WARNING);
		ValidationFramework.getDefault().validate(testFile, new NullProgressMonitor());
		IMarker[] elMarkers = testFile.findMarkers(I18nValidationComponent.PROBLEM_ID, true, IResource.DEPTH_ZERO);
		assertEquals("There shouldn't be "+NUMBER_OF_EXT_PROBLEMS_IN_FILE+" validation markers", NUMBER_OF_EXT_PROBLEMS_IN_FILE,elMarkers.length); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals("Marker Severity should be warning",(Integer)IMarker.SEVERITY_WARNING, (Integer)elMarkers[0].getAttribute(IMarker.SEVERITY));//$NON-NLS-1$
		Arrays.sort(elMarkers,new Comparator<IMarker>() {
			public int compare(IMarker o1, IMarker o2) {
				try {
					return (Integer)o1.getAttribute(IMarker.LINE_NUMBER)-(Integer)o2.getAttribute(IMarker.LINE_NUMBER);
				} catch (CoreException e) {
					fail(e.getMessage());
				}
				return 0;
			}
		});
		assertEquals("Line number should be",8,elMarkers[0].getAttribute(IMarker.LINE_NUMBER)); //$NON-NLS-1$
		assertEquals("Line number should be",10,elMarkers[1].getAttribute(IMarker.LINE_NUMBER)); //$NON-NLS-1$
		assertEquals("Line number should be",12,elMarkers[2].getAttribute(IMarker.LINE_NUMBER)); //$NON-NLS-1$
	}
}

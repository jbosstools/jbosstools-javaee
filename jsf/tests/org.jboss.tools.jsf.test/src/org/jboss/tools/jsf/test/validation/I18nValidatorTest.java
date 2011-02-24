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

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		setProject(ResourcesPlugin.getWorkspace().getRoot()
				.getProject("i18nTestProject")); //$NON-NLS-1$
		store = WebKbPlugin.getDefault().getPreferenceStore();
	}

	public void testShowErrorMarkers() throws CoreException {
		store.setValue(ELSeverityPreferences.NON_EXTERNALIZED_STRINGS, ELSeverityPreferences.WARNING);
		IFile testFile = getProject().getFile("WebContent/externalization-validator-test-case-1.xhtml"); //$NON-NLS-1$
		testFile.deleteMarkers(I18nValidationComponent.PROBLEM_ID, true, IResource.DEPTH_ZERO);
		IMarker[] elMarkers = testFile.findMarkers(I18nValidationComponent.PROBLEM_ID, true, IResource.DEPTH_ZERO);
		assertEquals("Markers should be cleaned", 0,elMarkers.length); //$NON-NLS-1$
		ValidationFramework.getDefault().validate(testFile, new NullProgressMonitor());
		elMarkers = testFile.findMarkers(I18nValidationComponent.PROBLEM_ID, true, IResource.DEPTH_ZERO);
		assertEquals("Should be 2 Markers", 2,elMarkers.length); //$NON-NLS-1$	
	}

	public void testDefaultStateI19nValidator() {
		assertEquals(
				"By Default I18nValidator should be ignored", ELSeverityPreferences.IGNORE,//$NON-NLS-1$
				store.getDefaultString(ELSeverityPreferences.NON_EXTERNALIZED_STRINGS)); 
	}
}

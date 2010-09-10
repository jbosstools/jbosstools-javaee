/*******************************************************************************
 * Copyright (c) 2007-2010 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.jsf.test.validation;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.validation.ValidationFramework;
import org.jboss.tools.jsf.jsf2.util.JSF2ResourceUtil;
import org.jboss.tools.test.util.ProjectImportTestSetup;

import junit.framework.TestCase;

/**
 * 
 * @author yzhishko
 *
 */

public class JSF2ComponentsValidatorTest extends TestCase {

	private static String projectName = "JSF2ComponentsValidator"; //$NON-NLS-1$
	private static IProject project;

	public JSF2ComponentsValidatorTest() {
		super("JSF 2 Components Validator Test"); //$NON-NLS-1$
	}

	protected void setUp() throws Exception {
		project = ProjectImportTestSetup.loadProject(projectName);
		project.build(IncrementalProjectBuilder.FULL_BUILD,
				new NullProgressMonitor());
	}
	//junit test add to check JBIDE-7016 by Maksim Areshkau
	public void testCreatingFile() throws CoreException, IOException{
		final IFile createdFile = JSF2ResourceUtil
		.createCompositeComponentFile(project,
				new Path("/jbide7016/jbide7016test.xhtml"), new String[0]); //$NON-NLS-1$
		//this method throw exception if file isn't accessible for some reasons
		InputStream is = createdFile.getContents();
		is.close();
	}
	
	public void testJSF2ComponentsValidator() throws Exception {
		ValidationFramework.getDefault().validate(new IProject[] { project },
				false, false, new NullProgressMonitor());
		IResource resource = project
				.findMember("/WebContent/pages/inputname.xhtml"); //$NON-NLS-1$
		assertNotNull(resource);
		IMarker[] markers = resource.findMarkers(
				"org.jboss.tools.jsf.jsf2problemmarker", false, 1); //$NON-NLS-1$
		assertNotNull(markers);
		assertTrue(isMarkerExist(markers,
				"Composite component \"echo\" was not found in a project resources directory")); //$NON-NLS-1$
		assertTrue(isMarkerExist(markers,
				"Attribute \"anknownAttr\" is not defined for \"echo\" composite component")); //$NON-NLS-1$
		assertTrue(isMarkerExist(
				markers,
				"JSF 2 Resources folder \"/resources/jarPage1\" is missing in a project root directory")); //$NON-NLS-1$
		assertTrue(isMarkerExist(markers,
				"Composite component \"echo1\" was not found in a project resources directory")); //$NON-NLS-1$
	}

	private boolean isMarkerExist(IMarker[] markers, String markerMesssage)
			throws CoreException {
		for (int i = 0; i < markers.length; i++) {
			if (markerMesssage.equals((String) markers[i]
					.getAttribute("message"))) { //$NON-NLS-1$
				return true;
			}
		}
		return false;
	}

}

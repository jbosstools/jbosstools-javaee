/*******************************************************************************
 * Copyright (c) 2007-2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.test.validation;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.jboss.tools.common.base.test.validation.TestUtil;
import org.jboss.tools.jsf.jsf2.util.JSF2ResourceUtil;
import org.jboss.tools.jsf.web.validation.JSFValidationMessage;
import org.jboss.tools.test.util.ProjectImportTestSetup;
import org.jboss.tools.tests.AbstractResourceMarkerTest;

/**
 * 
 * @author yzhishko
 *
 */
public class JSF2ComponentsValidatorTest extends TestCase {

	public static String PROJECT_NAME = "JSF2ComponentsValidator"; //$NON-NLS-1$
	private IProject project;

	public JSF2ComponentsValidatorTest() {
		super("JSF 2 Components Validator Test"); //$NON-NLS-1$
	}

	@Override
	protected void setUp() throws Exception {
		project = ProjectImportTestSetup.loadProject(PROJECT_NAME);
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
		IResource resource = project.findMember("/WebContent/pages/inputname.xhtml"); //$NON-NLS-1$
		TestUtil.validate(resource);
		assertTrue(resource.exists());
		IMarker[] markers = resource.findMarkers("org.jboss.tools.jsf.compositeproblem", false, 1); //$NON-NLS-1$
		assertEquals(3, markers.length);
		assertTrue(isMarkerExist(markers, MessageFormat.format(JSFValidationMessage.UNKNOWN_COMPOSITE_COMPONENT_NAME, "echo"))); //$NON-NLS-1$
		assertTrue(isMarkerExist(markers, MessageFormat.format(JSFValidationMessage.UNKNOWN_COMPOSITE_COMPONENT_NAME, "echo1"))); //$NON-NLS-1$
		assertTrue(isMarkerExist(markers, MessageFormat.format(JSFValidationMessage.UNKNOWN_COMPOSITE_COMPONENT_ATTRIBUTE, "anknownAttr", "echo"))); //$NON-NLS-1$
	}

	public void testELInTagBodyInCompositeComponent() throws Exception {
		IResource resource = project.findMember("/WebContent/resources/demo/input.xhtml"); //$NON-NLS-1$
		assertTrue(resource.exists());
		TestUtil.validate(resource);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(resource, "\"name\" cannot be resolved", 20);
		AbstractResourceMarkerTest.assertMarkerIsCreated(resource, "\"nameBroken\" cannot be resolved", 21);
	}

	private boolean isMarkerExist(IMarker[] markers, String markerMesssage)	throws CoreException {
		for (int i = 0; i < markers.length; i++) {
			if (markerMesssage.equals((String) markers[i].getAttribute("message"))) { //$NON-NLS-1$
				return true;
			}
		}
		return false;
	}

	public void testJSF2ComponentsConstraint() throws Exception {
		IResource resource = project.findMember("/WebContent/resources/xdata/data.xhtml"); //$NON-NLS-1$
		assertTrue(resource.exists());
		IMarker[] markers = resource.findMarkers("org.jboss.tools.jst.web.constraintsmarker", true, 0);
		assertEquals(0, markers.length);
	}

	public void testCompositeLibAvailableForValidation() throws Exception {
		IResource resource = project.findMember("/WebContent/resources/xdata/data.xhtml"); //$NON-NLS-1$
		assertTrue(resource.exists());
		IMarker[] markers = resource.findMarkers(IMarker.PROBLEM, true, 0);
		StringBuilder ms = new StringBuilder();
		for (int i = 0; i < markers.length; i++) {
			ms.append(markers[i].getAttribute(IMarker.MESSAGE, "") + "\n");
		}
		assertEquals("Unexpected markers: " + ms.toString(), 0, markers.length);
	}

}
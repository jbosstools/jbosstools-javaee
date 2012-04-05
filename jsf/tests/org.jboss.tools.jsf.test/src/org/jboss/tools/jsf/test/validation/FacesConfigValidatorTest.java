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
import java.text.MessageFormat;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.validation.ValidationFramework;
import org.jboss.tools.jsf.model.JSFConstants;
import org.jboss.tools.jsf.web.validation.JSFValidationMessage;
import org.jboss.tools.tests.AbstractResourceMarkerTest;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class FacesConfigValidatorTest extends TestCase {

	public static String PROJECT_NAME = "JSF2ComponentsValidator"; //$NON-NLS-1$
	private IProject project;

	public FacesConfigValidatorTest() {
		super("JSF 2 Components Validator Test"); //$NON-NLS-1$
	}

	@Override
	protected void setUp() throws Exception {
		project = ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT_NAME);
	}

	public void testNavigation() throws Exception {
		ValidationFramework.getDefault().validate(new IProject[] {project},	false, false, new NullProgressMonitor());
		IResource resource = project.findMember("/WebContent/WEB-INF/faces-config.xml"); //$NON-NLS-1$
		assertTrue(resource.exists());
		AbstractResourceMarkerTest.assertMarkerIsCreated(resource, NLS.bind(JSFValidationMessage.VIEW_ID_NO_SLASH, JSFConstants.ATT_FROM_VIEW_ID), 23);
		AbstractResourceMarkerTest.assertMarkerIsCreated(resource, NLS.bind(JSFValidationMessage.TO_VIEW_ID_STAR, JSFConstants.ATT_TO_VIEW_ID), 26);
		AbstractResourceMarkerTest.assertMarkerIsCreated(resource, NLS.bind(JSFValidationMessage.VIEW_NOT_EXISTS, JSFConstants.ATT_TO_VIEW_ID, "/pages/greeting3.xhtml"), 30, 42);
		AbstractResourceMarkerTest.assertMarkerIsCreated(resource, NLS.bind(JSFValidationMessage.VIEW_ID_NO_SLASH, JSFConstants.ATT_TO_VIEW_ID), 34);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(resource, NLS.bind(JSFValidationMessage.VIEW_NOT_EXISTS, JSFConstants.ATT_TO_VIEW_ID, "/pages/#{aaa.bbb}"), 38);
	}

}
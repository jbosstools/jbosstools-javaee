/*******************************************************************************
 * Copyright (c) 2007-2012 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.test.validation;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.osgi.util.NLS;
import org.jboss.tools.common.base.test.validation.TestUtil;
import org.jboss.tools.jsf.model.JSFConstants;
import org.jboss.tools.jsf.web.validation.JSFValidationMessage;
import org.jboss.tools.jst.web.validation.WebXMLValidatorMessages;
import org.jboss.tools.test.util.ProjectImportTestSetup;
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
		project = ProjectImportTestSetup.loadProject(PROJECT_NAME);
	}

	public void testWrongNavigationHandler() throws Exception {
		IResource resource = project.findMember("/WebContent/WEB-INF/faces-config.xml"); //$NON-NLS-1$
		assertTrue(resource.exists());
		TestUtil.validate(resource);
		AbstractResourceMarkerTest.assertMarkerIsCreated(resource, NLS.bind(WebXMLValidatorMessages.CLASS_NOT_EXTENDS, new String[]{"navigation-handler", "test.MyNav", "javax.faces.application.NavigationHandler"}), 50);
	}

	public void testNavigation() throws Exception {
		IResource resource = project.findMember("/WebContent/WEB-INF/faces-config.xml"); //$NON-NLS-1$
		assertTrue(resource.exists());
		TestUtil.validate(resource);
		AbstractResourceMarkerTest.assertMarkerIsCreated(resource, NLS.bind(JSFValidationMessage.VIEW_ID_NO_SLASH, JSFConstants.ATT_FROM_VIEW_ID), 23);
		AbstractResourceMarkerTest.assertMarkerIsCreated(resource, NLS.bind(JSFValidationMessage.TO_VIEW_ID_STAR, JSFConstants.ATT_TO_VIEW_ID), 26);
		AbstractResourceMarkerTest.assertMarkerIsCreated(resource, NLS.bind(JSFValidationMessage.VIEW_NOT_EXISTS, JSFConstants.ATT_TO_VIEW_ID, "/pages/greeting3.xhtml"), 30, 42);
		AbstractResourceMarkerTest.assertMarkerIsCreated(resource, NLS.bind(JSFValidationMessage.VIEW_ID_NO_SLASH, JSFConstants.ATT_TO_VIEW_ID), 34);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(resource, NLS.bind(JSFValidationMessage.VIEW_NOT_EXISTS, JSFConstants.ATT_TO_VIEW_ID, "/pages/#{aaa.bbb}"), 38);
		AbstractResourceMarkerTest.assertMarkerIsCreated(resource, NLS.bind(JSFValidationMessage.VIEW_NOT_EXISTS, JSFConstants.ATT_FROM_VIEW_ID, "/pages/inputname222.xhtml"), 46);
	}
}
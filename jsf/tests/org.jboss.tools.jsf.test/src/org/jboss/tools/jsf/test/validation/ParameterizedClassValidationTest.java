/*******************************************************************************
 * Copyright (c) 2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.test.validation;

import java.text.MessageFormat;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.jboss.tools.jst.web.kb.internal.validation.ELValidationMessages;
import org.jboss.tools.test.util.ProjectImportTestSetup;
import org.jboss.tools.tests.AbstractResourceMarkerTest;

/**
 * @author Alexey Kazakov
 */
public class ParameterizedClassValidationTest extends TestCase {

	IProject project;
	IFile xhtml;
	String messageBroken = MessageFormat.format(ELValidationMessages.UNKNOWN_EL_VARIABLE_PROPERTY_NAME, new Object[]{"broken"});
	String[] messagesOk = new String[] {MessageFormat.format(ELValidationMessages.UNKNOWN_EL_VARIABLE_PROPERTY_NAME, new Object[]{"personHome"}),
										MessageFormat.format(ELValidationMessages.UNKNOWN_EL_VARIABLE_PROPERTY_NAME, new Object[]{"instance"}),
										MessageFormat.format(ELValidationMessages.UNKNOWN_EL_VARIABLE_PROPERTY_NAME, new Object[]{"name"}),
										MessageFormat.format(ELValidationMessages.UNKNOWN_EL_VARIABLE_PROPERTY_NAME, new Object[]{"create"}),
										MessageFormat.format(ELValidationMessages.UNKNOWN_EL_VARIABLE_PROPERTY_NAME, new Object[]{"find"}),
										MessageFormat.format(ELValidationMessages.UNKNOWN_EL_VARIABLE_PROPERTY_NAME, new Object[]{"id"}),
										MessageFormat.format(ELValidationMessages.UNKNOWN_EL_VARIABLE_PROPERTY_NAME, new Object[]{"managed"}),
										MessageFormat.format(ELValidationMessages.UNKNOWN_EL_VARIABLE_PROPERTY_NAME, new Object[]{"version"})};

	@Override
	protected void setUp() throws Exception {
		project = ProjectImportTestSetup.loadProject("jsf2pr");
		xhtml = project.getFile("WebContent/paramValidation.xhtml");
	}

	public void testBroken() throws Exception {
		AbstractResourceMarkerTest.assertMarkerIsCreated(xhtml, messageBroken, 7, 8, 11);
	}

	public void testOk() throws Exception {
		for (String message : messagesOk) {
			for(int i = 6; i<15; i++) {
				AbstractResourceMarkerTest.assertMarkerIsNotCreated(xhtml, message, i);
			}
		}
	}
}
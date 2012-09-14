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
import org.eclipse.core.resources.ResourcesPlugin;
import org.jboss.tools.jst.web.kb.internal.validation.ELValidationMessages;
import org.jboss.tools.tests.AbstractResourceMarkerTest;

/**
 * @author Alexey Kazakov
 */
public class VarAttributesTest extends TestCase {

	IProject project;
	IFile xhtml;
	String messageBroken = MessageFormat.format(ELValidationMessages.UNKNOWN_EL_VARIABLE_PROPERTY_NAME, new Object[]{"broken"});
	String messageOk = MessageFormat.format(ELValidationMessages.UNKNOWN_EL_VARIABLE_PROPERTY_NAME, new Object[]{"name"});

	@Override
	protected void setUp() throws Exception {
		project = ResourcesPlugin.getWorkspace().getRoot().getProject("jsf2pr");
		xhtml = project.getFile("WebContent/varAttributes.xhtml");
	}

	public void testBroken() throws Exception {
		AbstractResourceMarkerTest.assertMarkerIsCreated(xhtml, messageBroken, 9, 11, 17, 18, 19, 24, 29, 34, 39, 44);
	}

	public void testInnerClass() throws Exception {
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(xhtml, messageOk, 8);
	}

	public void testMap() throws Exception {
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(xhtml, messageOk, 15);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(xhtml, messageOk, 16);
	}

	public void testArray() throws Exception {
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(xhtml, messageOk, 23);
	}

	public void testIterable() throws Exception {
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(xhtml, messageOk, 28);
	}

	public void testList() throws Exception {
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(xhtml, messageOk, 33);
	}

	public void testDataModel() throws Exception {
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(xhtml, messageOk, 38);
	}

	public void testSet() throws Exception {
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(xhtml, messageOk, 43);
	}
}
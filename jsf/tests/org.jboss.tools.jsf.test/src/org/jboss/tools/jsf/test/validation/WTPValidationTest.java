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

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.jboss.tools.test.util.ProjectImportTestSetup;
import org.jboss.tools.tests.AbstractResourceMarkerTest;

/**
 * @author Alexey Kazakov
 */
public class WTPValidationTest extends TestCase {

	IProject project;
	IFile xhtml;

	@Override
	protected void setUp() throws Exception {
		project = ProjectImportTestSetup.loadProject("jsf2pr");

		xhtml = project.getFile("WebContent/wtpvalidation.xhtml");
	}

	public void testMethodSignature() throws Exception {
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(xhtml, AbstractResourceMarkerTest.MARKER_TYPE, ".*", 6);
	}
}
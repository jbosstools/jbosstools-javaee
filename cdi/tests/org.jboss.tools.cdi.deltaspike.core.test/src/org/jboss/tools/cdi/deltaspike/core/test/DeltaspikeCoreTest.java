/******************************************************************************* 
 * Copyright (c) 2012 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.deltaspike.core.test;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.jboss.tools.cdi.core.test.tck.validation.AbstractValidationTest;

/**
 * @author Alexey Kazakov
 */
public class DeltaspikeCoreTest extends AbstractValidationTest {

	protected IProject getTestProject() throws Exception {
		return ResourcesPlugin.getWorkspace().getRoot().getProject(DeltaspikeCoreTestSetup.PROJECT_NAME);
	}

}
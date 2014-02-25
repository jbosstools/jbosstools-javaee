/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.jsf.kb.test;

import org.eclipse.core.resources.ResourcesPlugin;

public class MyFacesKbModelWithMetadataInSourcesTest extends MyFacesKbModelTest {

	public MyFacesKbModelWithMetadataInSourcesTest() {
		setName("MyFaces With Metadata In Sources Kb Model Test");
	}

	public void setUp() throws Exception {
		project = ResourcesPlugin.getWorkspace().getRoot().getProject("MyFaces2");
		assertNotNull("Can't load TestKbModel", project); //$NON-NLS-1$
	}

}
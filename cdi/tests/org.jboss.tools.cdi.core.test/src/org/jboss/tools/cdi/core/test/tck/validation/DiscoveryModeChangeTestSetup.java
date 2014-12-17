/******************************************************************************* 
 * Copyright (c) 2014 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.core.test.tck.validation;

import junit.extensions.TestSetup;
import junit.framework.Test;

import org.eclipse.core.resources.IProject;

/**
 * @author Viacheslav Kabanovich
 */
public class DiscoveryModeChangeTestSetup extends TestSetup {

	protected IProject testProject;
	protected IProject[] projects;

	public DiscoveryModeChangeTestSetup(Test test) {
		super(test);
	}

	@Override
	protected void setUp() throws Exception {
		DiscoveryModeChangeTest test = new DiscoveryModeChangeTest();
		projects = test.importPreparedProjects();
		testProject = projects[0];
	}
}
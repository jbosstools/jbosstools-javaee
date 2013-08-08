/******************************************************************************* 
 * Copyright (c) 2010 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.core.test;

import junit.extensions.TestSetup;
import junit.framework.Test;

import org.eclipse.core.resources.IProject;
import org.jboss.tools.cdi.core.test.tck.TCKTest;

/**
 * @author Alexey Kazakov
 */
public class CDICoreTestSetup extends TestSetup {

	protected IProject tckProject;
	protected IProject[] projects;

	public CDICoreTestSetup(Test test) {
		super(test);
	}

	/*
	 * (non-Javadoc)
	 * @see junit.extensions.TestSetup#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		TCKTest test = new TCKTest();
		projects = test.importPreparedProjects();
		tckProject = projects[1];
	}
}
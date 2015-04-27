/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.core.test;

import junit.framework.Test;

import org.jboss.tools.cdi.core.test.tck11.TCK11AnnotatedTest;

/**
 * @author Alexey Kazakov
 */
public class CDI11AnnotatedCoreTestSetup extends CDICoreTestSetup {

	/**
	 * @param test
	 */
	public CDI11AnnotatedCoreTestSetup(Test test) {
		super(test);
	}

	@Override
	protected void setUp() throws Exception {
		TCK11AnnotatedTest test = new TCK11AnnotatedTest();
		projects = test.importPreparedProjects();
		tckProject = projects[1];
	}
}
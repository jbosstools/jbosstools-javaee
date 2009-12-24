/******************************************************************************* 
 * Copyright (c) 2009 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.core.test;

import org.eclipse.core.resources.IProject;
import org.jboss.tools.cdi.core.test.tck.TCKTest;

/**
 * @author Alexey Kazakov
 */
public class CATest extends TCKTest {

	protected void setUp() throws Exception {
		IProject p = importPreparedProject("/definition/qualifier");
	}

	public void test1() {
		
	}

	protected void tearDown() throws Exception {
		cleanProject("/definition/qualifier");
	}
}
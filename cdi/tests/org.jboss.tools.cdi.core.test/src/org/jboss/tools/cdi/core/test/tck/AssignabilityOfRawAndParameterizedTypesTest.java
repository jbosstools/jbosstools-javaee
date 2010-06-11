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
package org.jboss.tools.cdi.core.test.tck;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.cdi.core.IBean;

/**
 * @author Alexey Kazakov
 */
public class AssignabilityOfRawAndParameterizedTypesTest extends TCKTest {

	/**
	 * Section 5.2 - Typesafe resolution
	 *   kb) Test with a raw type.
	 *   
	 * @throws CoreException 
	 */
	public void testAssignabilityToRawType() throws CoreException {
		Set<IBean> beans = getBeans("org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.parameterized.Dao");
		assertEquals("Wrong number of the beans", 4, beans.size());
	}

	// TODO continue implementing tests.
}
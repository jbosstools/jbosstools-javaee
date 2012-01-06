/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
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

import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.cdi.core.IBean;

/**
 * @author Viacheslav Kabanovich
 */
public class ResourceExclusionTest extends TCKTest {

	public void testExclusion() throws JavaModelException {
		Set<IBean> beans = cdiProject.getBeans("myExcludedBean", false);
		assertEquals("Wrong number of beans.", 0, beans.size());

		beans = cdiProject.getBeans("myExcludedBean2", false);
		assertEquals("Wrong number of beans.", 0, beans.size());

		beans = cdiProject.getBeans("myIncludedBean", false);
		assertEquals("Wrong number of beans.", 1, beans.size());

	}
}
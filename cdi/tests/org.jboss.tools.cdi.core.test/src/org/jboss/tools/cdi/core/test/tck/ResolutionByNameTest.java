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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.internal.core.impl.CDIProject;

/**
 * @author Alexey Kazakov
 */
public class ResolutionByNameTest extends TCKTest {

	/**
	 * section 5.3.1 ca),
	 * section 11.3.5 aa),
	 * section 11.3.5 b)
	 * 
	 * @throws CoreException
	 */
	public void testAmbiguousELNamesResolved() throws CoreException {
	    // Cod, Plaice and AlaskaPlaice are named "whitefishJBT" - Cod is a not-enabled policy, AlaskaPlaice specializes Plaice
		Set<IBean> beans = cdiProject.getBeans("whitefishJBT", true);
		assertEquals("Wrong number of the beans", 1, beans.size());
		assertContainsBeanClass(beans, "org.jboss.jsr299.tck.tests.lookup.byname.AlaskaPlaice");

		IFile f = tckProject.getFile("/JavaSource/org/jboss/jsr299/tck/tests/lookup/byname/beans.xml");
		assertTrue("File /JavaSource/org/jboss/jsr299/tck/tests/lookup/byname/beans.xml not found", f != null && f.exists());

		IPath old = ((CDIProject)cdiProject).replaceBeanXML(f.getFullPath());

		assertTrue("Old beans.xml is not found", old != null);

		try {
			beans = cdiProject.getBeans("whitefishJBT", true);
			assertEquals("Wrong number of the beans", 1, beans.size());
			assertContainsBeanClass(beans, "org.jboss.jsr299.tck.tests.lookup.byname.AlaskaPlaice");

            // Both Salmon and Sole are named "fishJBT" - Sole is an enabled policy
			beans = cdiProject.getBeans("fishJBT", false);
			assertEquals("Wrong number of the beans", 2, beans.size());
			assertContainsBeanClasses(beans, "org.jboss.jsr299.tck.tests.lookup.byname.Salmon", "org.jboss.jsr299.tck.tests.lookup.byname.Sole");

			beans = cdiProject.getBeans("fishJBT", true);
			assertEquals("Wrong number of the beans", 1, beans.size());
			assertContainsBeanClass(beans, "org.jboss.jsr299.tck.tests.lookup.byname.Sole");
		} finally {
			old = ((CDIProject)cdiProject).replaceBeanXML(old);
		}
	}
}
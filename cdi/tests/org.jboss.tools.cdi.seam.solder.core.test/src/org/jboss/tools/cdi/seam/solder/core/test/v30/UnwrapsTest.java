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
package org.jboss.tools.cdi.seam.solder.core.test.v30;

import java.io.IOException;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IInjectionPointField;
import org.jboss.tools.cdi.core.IProducerMethod;
import org.jboss.tools.cdi.seam.solder.core.CDISeamSolderConstants;
import org.jboss.tools.cdi.seam.solder.core.CDISeamSolderConstants30;

/**
 *   
 * @author Viacheslav Kabanovich
 *
 */
public class UnwrapsTest extends SeamSolderTest {

	public UnwrapsTest() {}

	public void testUnwraps() throws CoreException, IOException {
		ICDIProject cdi = CDICorePlugin.getCDIProject(getTestProject(), true);

		IInjectionPointField logger = getInjectionPointField(cdi, "src/org/jboss/unwraps/Unwrapped.java", "permission");

		Set<IBean> bs = cdi.getBeans(false, logger);
		assertEquals(1, bs.size());

		IBean b = bs.iterator().next();

		assertTrue(b instanceof IProducerMethod);

		IProducerMethod m = (IProducerMethod)b;
		assertTrue(m.isAnnotationPresent(CDISeamSolderConstants30.UNWRAPS_ANNOTATION_TYPE_NAME_30));
		assertEquals("getPermission", m.getMethod().getElementName());
	}
}
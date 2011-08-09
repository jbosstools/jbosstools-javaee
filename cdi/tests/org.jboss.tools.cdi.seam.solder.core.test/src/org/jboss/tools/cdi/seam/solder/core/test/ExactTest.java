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
package org.jboss.tools.cdi.seam.solder.core.test;

import java.io.IOException;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IMember;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IProducerMethod;

/**
 *   
 * @author Viacheslav Kabanovich
 *
 */
public class ExactTest extends SeamSolderTest {

	public ExactTest() {}

	public void testExact() throws CoreException, IOException {
		ICDIProject cdi = CDICorePlugin.getCDIProject(getTestProject(), true);

		Set<IBean> bs = cdi.getBeans(new Path("/CDISolderTest/src/org/jboss/exact/FishFactory.java"));
		assertEquals(2, bs.size());
		IClassBean cls = null;
		IProducerMethod mtd = null;
		for (IBean b: bs) {
			if(b instanceof IClassBean) {
				cls = (IClassBean)b;
			} else if(b instanceof IProducerMethod) {
				mtd = (IProducerMethod)b;
			}
		}
		assertNotNull(cls);
		assertNotNull(mtd);
		Set<IInjectionPoint> points = cls.getInjectionPoints();
		int count = 0;
		for (IInjectionPoint p: points) {
			Set<IBean> injected = cdi.getBeans(false, p);
			IMember member = p.getSourceMember();
			if(member.getElementName().equals("peacefulFish")) {
				assertEquals(1, injected.size());
				IBean ib = injected.iterator().next();
				assertEquals("org.jboss.exact.Salmon", ib.getBeanClass().getFullyQualifiedName());
				count++;
			} else if(member.getElementName().equals("dangerousFish")) {
				assertEquals(1, injected.size());
				IBean ib = injected.iterator().next();
				assertEquals("org.jboss.exact.Shark", ib.getBeanClass().getFullyQualifiedName());
				count++;
			} else if(member.getElementName().equals("getTastyFish")) {
				assertEquals(1, injected.size());
				IBean ib = injected.iterator().next();
				assertEquals("org.jboss.exact.Salmon", ib.getBeanClass().getFullyQualifiedName());
				count++;
			} else {
			}
		}
		assertEquals(3, count);
	}
}
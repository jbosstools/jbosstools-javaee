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
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IInjectionPointField;
import org.jboss.tools.cdi.core.IProducerMethod;

/**
 *   
 * @author Viacheslav Kabanovich
 *
 */
public class ServiceHandlerTest extends SeamSolderTest {

	public ServiceHandlerTest() {}

	public void testMessageLogger() throws CoreException, IOException {
		ICDIProject cdi = CDICorePlugin.getCDIProject(project, true);
	
		IInjectionPointField logger = getInjectionPointField(cdi, "src/org/jboss/service/UserListManager.java", "userQuery");
		
		Set<IBean> bs = cdi.getBeans(false, logger);
		assertEquals(1, bs.size());
		
		IBean b = bs.iterator().next();
		
		IType t = b.getBeanClass();
		assertNotNull(t);
		assertTrue(t.isInterface());
		assertEquals("org.jboss.service.UserQuery", t.getFullyQualifiedName());

	}

}

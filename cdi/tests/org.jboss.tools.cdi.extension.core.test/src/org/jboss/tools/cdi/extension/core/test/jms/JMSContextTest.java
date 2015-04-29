/******************************************************************************* 
 * Copyright (c) 2012 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.extension.core.test.jms;

import java.util.Collection;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IInjectionPointField;
import org.jboss.tools.cdi.core.IProducer;
import org.jboss.tools.cdi.core.test.DependentProjectTest;
import org.jboss.tools.cdi.core.test.tck.validation.AbstractValidationTest;

/**
 * @author Viacheslav Kabanovich
 */
public class JMSContextTest extends AbstractValidationTest {

	protected IProject getTestProject() throws Exception {
		return ResourcesPlugin.getWorkspace().getRoot().getProject(JMSContextTestSetup.PROJECT_NAME);
	}
	
	public void testDummy() throws Exception {
		IProject project = getTestProject();
		ICDIProject cdi = CDICorePlugin.getCDIProject(project, true);
		IInjectionPointField f = DependentProjectTest.getInjectionPointField(cdi, "src/cdi/test/extension/JMSClient.java", "f");
		Collection<IBean> bs = cdi.getBeans(true, f);
		assertEquals(1, bs.size());
		IBean b = bs.iterator().next();
		assertTrue(b instanceof IProducer);
	}

}

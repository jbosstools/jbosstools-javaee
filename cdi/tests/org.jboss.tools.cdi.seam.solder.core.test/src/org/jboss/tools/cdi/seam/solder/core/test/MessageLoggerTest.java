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

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IInjectionPointField;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ResourcesUtils;

/**
 *   
 * @author Viacheslav Kabanovich
 *
 */
public class MessageLoggerTest extends TestCase {
	protected static String PLUGIN_ID = "org.jboss.tools.cdi.seam.solder.core.test";
	IProject project = null;

	public MessageLoggerTest() {}

	public void setUp() throws Exception {
		project = ResourcesUtils.importProject(PLUGIN_ID, "/projects/CDISolderTest");
		JobUtils.waitForIdle();
		project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		JobUtils.waitForIdle();
	}

	public void testMessageLogger() throws CoreException, IOException {
		ICDIProject cdi = CDICorePlugin.getCDIProject(project, true);
	
		IInjectionPointField logger = getInjectionPointField(cdi, "src/org/jboss/logger/LogAccess.java", "logger");
		
		Set<IBean> bs = cdi.getBeans(false, logger);
		assertEquals(1, bs.size());
		
		IBean b = bs.iterator().next();
		
		IType t = b.getBeanClass();
		assertNotNull(t);
		assertTrue(t.isInterface());
		assertEquals("org.jboss.logger.MyLogger", t.getFullyQualifiedName());

	}

	public void testMessageBundle() throws CoreException, IOException {
		ICDIProject cdi = CDICorePlugin.getCDIProject(project, true);
	
		IInjectionPointField bundle = getInjectionPointField(cdi, "src/org/jboss/logger/LogAccess.java", "bundle");
		
		Set<IBean> bs = cdi.getBeans(false, bundle);
		assertEquals(1, bs.size());
		
		IBean b = bs.iterator().next();
		
		IType t = b.getBeanClass();
		assertNotNull(t);
		assertTrue(t.isInterface());
		assertEquals("org.jboss.logger.MyBundle", t.getFullyQualifiedName());

	}

	public void tearDown() throws Exception {
		boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
		JobUtils.waitForIdle();
		project.delete(true, true, null);
		JobUtils.waitForIdle();
		ResourcesUtils.setBuildAutomatically(saveAutoBuild);
		JobUtils.waitForIdle();
	}

	protected IInjectionPointField getInjectionPointField(ICDIProject cdi, String beanClassFilePath, String fieldName) {
		IFile file = cdi.getNature().getProject().getFile(beanClassFilePath);
		Set<IBean> beans = cdi.getBeans(file.getFullPath());
		assertEquals("Wrong number of the beans", 1, beans.size());
		Set<IInjectionPoint> injections = beans.iterator().next().getInjectionPoints();
		for (IInjectionPoint injectionPoint : injections) {
			if(injectionPoint instanceof IInjectionPointField) {
				IInjectionPointField field = (IInjectionPointField)injectionPoint;
				if(fieldName.equals(field.getField().getElementName())) {
					return field;
				}
			}
		}
		fail("Can't find \"" + fieldName + "\" injection point filed in " + beanClassFilePath);
		return null;
	}

}

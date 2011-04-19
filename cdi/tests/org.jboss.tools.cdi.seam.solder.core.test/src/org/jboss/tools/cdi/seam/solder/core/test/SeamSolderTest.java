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

import java.util.Iterator;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IInjectionPointField;
import org.jboss.tools.cdi.core.IProducer;
import org.jboss.tools.test.util.ResourcesUtils;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class SeamSolderTest extends TestCase {
	protected static String PLUGIN_ID = "org.jboss.tools.cdi.seam.solder.core.test";
	protected static String PROJECT_NAME = "CDISolderTest";
	protected static String PROJECT_PATH = "/projects/CDISolderTest";

	protected IProject project;
	protected ICDIProject cdiProject;

	public SeamSolderTest() {
		project = getTestProject();
		cdiProject = CDICorePlugin.getCDIProject(project, false);
	}

	public IProject getTestProject() {
		if(project==null) {
			try {
				project = findTestProject();
				if(project==null || !project.exists()) {
					project = ResourcesUtils.importProject(PLUGIN_ID, PROJECT_PATH);
				}
			} catch (Exception e) {
				e.printStackTrace();
				fail("Can't import CDI test project: " + e.getMessage());
			}
		}
		return project;
	}

	public static IProject findTestProject() {
		return ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT_NAME);
	}

	protected IInjectionPointField getInjectionPointField(ICDIProject cdi, String beanClassFilePath, String fieldName) {
		IFile file = cdi.getNature().getProject().getFile(beanClassFilePath);
		Set<IBean> beans = cdi.getBeans(file.getFullPath());
		Iterator<IBean> it = beans.iterator();
		while(it.hasNext()) {
			IBean b = it.next();
			if(b instanceof IProducer) it.remove();
		}
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

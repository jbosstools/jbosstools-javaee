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

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IInjectionPointField;
import org.jboss.tools.cdi.core.test.DependentProjectTest;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class SeamSolderTest extends TestCase {
	protected static String PLUGIN_ID = "org.jboss.tools.cdi.seam.solder.core.test";
	protected static String PROJECT_NAME = "CDISolderTest";
	protected static String PROJECT_PATH = "/projects/CDISolderTest";
	protected static String DEPENDENT_PROJECT_NAME = "CDIDependentSolderTest";
	protected static String DEPENDENT_PROJECT_PATH = "/projects/CDIDependentSolderTest";

	private ICDIProject cdiProject;
	private IProject project;

	private IProject dependentProject;
	private ICDIProject cdiDependentProject;

	public IProject getTestProject() {
		if(cdiProject==null) {
			project = findTestProject(PROJECT_NAME);
			cdiProject = CDICorePlugin.getCDIProject(project, true);
		}
		return project;
	}

	public IProject getDependentTestProject() {
		if(cdiDependentProject==null) {
			dependentProject = findTestProject(DEPENDENT_PROJECT_NAME);
			cdiDependentProject = CDICorePlugin.getCDIProject(dependentProject, true);
		}
		return dependentProject;
	}

	public ICDIProject getCDIProject() {
		if(cdiProject==null) {
			getTestProject();
		}
		return cdiProject;
	}

	public ICDIProject getDependentCDIProject() {
		if(cdiDependentProject==null) {
			getDependentTestProject();
		}
		return cdiDependentProject;
	}

	public static IProject findTestProject(String name) {
		return ResourcesPlugin.getWorkspace().getRoot().getProject(name);
	}

	protected IInjectionPointField getInjectionPointField(ICDIProject cdi, String beanClassFilePath, String fieldName) {
		return DependentProjectTest.getInjectionPointField(cdi, beanClassFilePath, fieldName);
	}
}
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
package org.jboss.tools.cdi.seam.config.core.test;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IInjectionPointField;
import org.jboss.tools.cdi.core.extension.feature.IBuildParticipantFeature;
import org.jboss.tools.cdi.core.test.DependentProjectTest;
import org.jboss.tools.cdi.seam.config.core.CDISeamConfigExtension;
import org.jboss.tools.cdi.seam.config.core.ConfigDefinitionContext;
import org.jboss.tools.cdi.seam.config.core.definition.SeamBeanDefinition;
import org.jboss.tools.cdi.seam.config.core.definition.SeamBeansDefinition;
import org.jboss.tools.cdi.seam.config.core.xml.SAXElement;
import org.jboss.tools.test.util.ResourcesUtils;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class SeamConfigTest extends TestCase {
	protected static String PLUGIN_ID = "org.jboss.tools.cdi.seam.config.core.test";
	protected static String PROJECT_NAME = "CDIConfigTest";
	protected static String PROJECT_PATH = "/projects/" + PROJECT_NAME;

	protected static String DEPENDENT_PROJECT_NAME = "CDIDependentConfigTest";
	protected static String DEPENDENT_PROJECT_PATH = "/projects/" + DEPENDENT_PROJECT_NAME;

	protected IProject project;
	protected ICDIProject cdiProject;

	protected IProject dependentProject;
	protected ICDIProject cdiDependentProject;

	public SeamConfigTest() {
		project = getTestProject();
		cdiProject = CDICorePlugin.getCDIProject(project, false);
		dependentProject = getDependentTestProject();
		cdiDependentProject = CDICorePlugin.getCDIProject(dependentProject, false);
	}

	public IProject getTestProject() {
		if(project==null) {
			try {
				project = findTestProject(PROJECT_NAME);
				if(project==null || !project.exists()) {
					project = ResourcesUtils.importProject(PLUGIN_ID, PROJECT_PATH);
					project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
				}
			} catch (Exception e) {
				e.printStackTrace();
				fail("Can't import CDI test project: " + e.getMessage());
			}
		}
		return project;
	}

	public IProject getDependentTestProject() {
		if(dependentProject==null) {
			try {
				dependentProject = findTestProject(DEPENDENT_PROJECT_NAME);
				if(dependentProject==null || !dependentProject.exists()) {
					dependentProject = ResourcesUtils.importProject(PLUGIN_ID, DEPENDENT_PROJECT_PATH);
					dependentProject.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
				}
			} catch (Exception e) {
				e.printStackTrace();
				fail("Can't import CDI test project: " + e.getMessage());
			}
		}
		return dependentProject;
	}

	public static IProject findTestProject(String name) {
		return ResourcesPlugin.getWorkspace().getRoot().getProject(name);
	}

	protected CDISeamConfigExtension getConfigExtension(ICDIProject cdi) {
		Set<IBuildParticipantFeature> bp = cdi.getNature().getExtensionManager().getBuildParticipantFeatures();
		for (IBuildParticipantFeature p: bp) {
			if(p instanceof CDISeamConfigExtension) {
				return (CDISeamConfigExtension)p;
			}
		}
		return null;
	}

	protected IInjectionPointField getInjectionPointField(ICDIProject cdi, String beanClassFilePath, String fieldName) {
		return DependentProjectTest.getInjectionPointField(cdi, beanClassFilePath, fieldName);
	}

	protected SeamBeansDefinition getBeansDefinition(ConfigDefinitionContext context, String path) {
		IFile f = project.getFile(path);
		assertNotNull(f);
		assertTrue(f.exists());		
		SeamBeansDefinition d = context.getDefinition(f.getFullPath());
		assertNotNull(d);		
		return d;
	}

	protected Set<SeamBeanDefinition> findBeanDefinitionByTagName(SeamBeansDefinition seamBeans, String tagname) {
		Set<SeamBeanDefinition> ds = new HashSet<SeamBeanDefinition>();
		Set<SeamBeanDefinition> all = seamBeans.getBeanDefinitions();
		for (SeamBeanDefinition d: all) {
			SAXElement e = d.getElement();
			if(tagname.equals(e.getName())) {
				ds.add(d);
			}
		}
		return ds;
	}
}
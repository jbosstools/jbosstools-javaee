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
	protected static String PROJECT_PATH = "/projects/CDIConfigTest";

	protected IProject project;
	protected ICDIProject cdiProject;

	public SeamConfigTest() {
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

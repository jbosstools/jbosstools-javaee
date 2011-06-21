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
package org.jboss.tools.jsf.test.project.facet;

import junit.framework.TestCase;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jst.jsf.core.internal.project.facet.JSFFacetInstallDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;
import org.jboss.tools.jst.web.kb.internal.KbBuilder;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ResourcesUtils;

/**
 * @author Alexey Kazakov and Viacheslav Kabanovich
 */
public class JSFFacetOnExistingProjectTest extends TestCase {

	IProject project;

	@Override
	protected void setUp() throws Exception {
		project = ResourcesUtils.importProject("org.jboss.tools.jsf.test", "projects/web");
		JobUtils.waitForIdle();

		IFacetedProject fproj = ProjectFacetsManager.create(project);

		IProjectFacet jsfFacet = ProjectFacetsManager.getProjectFacet("jst.jsf");
		IProjectFacetVersion jsfFacetVersion = jsfFacet.getVersion("1.2");

		fproj.installProjectFacet(jsfFacetVersion, createJSFDataModel(), null);
		JobUtils.waitForIdle();
	}

	public void testJSFProjectBuilders() throws CoreException {
		ICommand[] cs = project.getDescription().getBuildSpec();
		int validation = -1;
		int kb = -1;
		for (int i = 0; i < cs.length; i++) {
			ICommand c = cs[i];
			if(ValidationPlugin.VALIDATION_BUILDER_ID.equals(c.getBuilderName())) {
				validation = i;
			} else if(KbBuilder.BUILDER_ID.equals(c.getBuilderName())) {
				kb = i;
			}
		}
		assertTrue(kb >= 0);
		assertTrue(validation > kb);
	}

	@Override
	protected void tearDown() throws Exception {
		ResourcesUtils.deleteProject(project.getName());
		JobUtils.waitForIdle();
	}

	protected IDataModel createJSFDataModel() {
		IDataModel config = DataModelFactory.createDataModel(new JSFFacetInstallDataModelProvider());
		return config;
	}
}
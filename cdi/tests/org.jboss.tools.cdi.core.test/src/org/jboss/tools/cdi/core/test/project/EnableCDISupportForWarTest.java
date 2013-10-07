/******************************************************************************* 
 * Copyright (c) 2010 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/

package org.jboss.tools.cdi.core.test.project;

import java.io.File;

import junit.framework.TestCase;

import org.apache.tools.ant.util.FileUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.CDIUtil;
import org.jboss.tools.cdi.core.CDIVersion;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ResourcesUtils;

/**
 * @author Alexey Kazakov
 */
public class EnableCDISupportForWarTest extends TestCase  {

	protected static String PLUGIN_ID = "org.jboss.tools.cdi.core.test";
	protected IProject project = null;

	@Override
	public void setUp() throws Exception {
		project = ResourcesUtils.importProject(PLUGIN_ID, "/projects/warproject");
		project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
	}

	protected String getBeansXmlPath() {
		return "WebContent/WEB-INF/beans.xml";
	}

	public void testEnableCDISupportWithoutBeansXml() throws CoreException {
		CDIUtil.enableCDI(project, null, new NullProgressMonitor());
		project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
		assertNotNull("CDI is not enabled", CDICorePlugin.getCDI(project, false));

		IFile beansXml = project.getFile(new Path(getBeansXmlPath()));
		assertFalse("beans.xml is created", beansXml.isAccessible());

		CDIUtil.disableCDI(project);
		project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
		assertNull("CDI is enabled", CDICorePlugin.getCDI(project, false));
	}

	public void testEnableCDISupportWithBeansXml10() throws Exception {
		assertCDISupportEnabledWithBeansXML(CDIVersion.CDI_1_0, "beans.xml");
	}

	public void testEnableCDISupportWithBeansXml11() throws Exception {
		assertCDISupportEnabledWithBeansXML(CDIVersion.CDI_1_1, "beans11.xml");
	}

	private void assertCDISupportEnabledWithBeansXML(CDIVersion version, String beansXmlTemplateFileName) throws Exception {
		CDIUtil.enableCDI(project, version, new NullProgressMonitor());
		project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
		assertNotNull("CDI is not enabled", CDICorePlugin.getCDI(project, false));

		IFile beansXml = project.getFile(new Path(getBeansXmlPath()));
		assertTrue("Can't find beans.xml", beansXml.isAccessible());
		File beansXmlTemplateFile = new File(CDIUtil.getTemplatesFolder(), beansXmlTemplateFileName);
		File resultBeanXmlFile = beansXml.getLocation().toFile();
		assertTrue("Created beans.xml is not correct", FileUtils.getFileUtils().contentEquals(beansXmlTemplateFile, resultBeanXmlFile));

		CDIUtil.disableCDI(project);
		project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
		assertNull("CDI is enabled", CDICorePlugin.getCDI(project, false));

		beansXml.delete(true, new NullProgressMonitor());
	}

	@Override
	public void tearDown() throws Exception {
		boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
		project.delete(true, true, null);
		JobUtils.waitForIdle();
		ResourcesUtils.setBuildAutomatically(saveAutoBuild);
	}
}
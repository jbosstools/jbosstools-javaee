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
package org.jboss.tools.cdi.seam.core.test.international;

import java.io.File;

import junit.extensions.TestSetup;
import junit.framework.Test;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.jboss.tools.cdi.core.test.tck.TCKTest;
import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ResourcesUtils;
import org.osgi.framework.Bundle;

public class SeamInternationalTestSetup extends TestSetup {

	protected static String LIB_SUFFIX = "/lib";
	protected static final String SEAM_INTERNATIONAL_LIB_SUFFIX = "/seam-international.jar";
	protected static final String SEAM_INTERNATIONAL_PAGE_SUFFIX = "/seam-international.xhtml";
	protected static final String DEFAULT_RESOURCE_BUNDLE_SUFFIX = "/messages.properties";
	protected static final String DE_RESOURCE_BUNDLE_SUFFIX = "/messages_de.properties";
	protected static final String RESOURCES_SUFFIX = "/resources";

	public static final String PLUGIN_ID = "org.jboss.tools.cdi.seam.core.test";

	protected IProject project;

	public SeamInternationalTestSetup(Test test) {
		super(test);
	}

	@Override
	protected void setUp() throws Exception {
		project = TCKTest.importPreparedProject("/");
		project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
		// Set up seam-international.jar library into the project's WEB-INF/lib folder
		try {
			assertTrue("Cannot set up SEAM International module and resource bundles into a test project", setUpSeamInternationalLibraryAndResourceBundle());
		} catch (Exception e) {
			fail("Cannot set up SEAM International module and resource bundles into a test project: " + e.getLocalizedMessage());
		}
	}

	private boolean setUpSeamInternationalLibraryAndResourceBundle() throws Exception {
		Bundle b = Platform.getBundle(PLUGIN_ID);
		String projectPath = project.getLocation().toOSString();
		String resourcePath = FileLocator.resolve(b.getEntry(RESOURCES_SUFFIX)).getFile();

		File seamInternationalLibFrom = new File(resourcePath + SEAM_INTERNATIONAL_LIB_SUFFIX);
		File seamInternationalLibTo = new File(projectPath + TCKTest.WEB_CONTENT_SUFFIX + TCKTest.WEB_INF_SUFFIX 
				+ LIB_SUFFIX + SEAM_INTERNATIONAL_LIB_SUFFIX);
		if (!FileUtil.copyFile(seamInternationalLibFrom, seamInternationalLibTo))
			return false;

		File defaultResourceBundleFrom = new File(resourcePath + DEFAULT_RESOURCE_BUNDLE_SUFFIX);
		File defaultResourceBundleTo = new File(projectPath + TCKTest.JAVA_SOURCE_SUFFIX + DEFAULT_RESOURCE_BUNDLE_SUFFIX);
		if (!FileUtil.copyFile(defaultResourceBundleFrom, defaultResourceBundleTo))
			return false;

		File germanResourceBundleFrom = new File(resourcePath + DE_RESOURCE_BUNDLE_SUFFIX);
		File germanResourceBundleTo = new File(projectPath + TCKTest.JAVA_SOURCE_SUFFIX + DE_RESOURCE_BUNDLE_SUFFIX);
		if (!FileUtil.copyFile(germanResourceBundleFrom, germanResourceBundleTo))
			return false;

		File seamInternationalPageFrom = new File(resourcePath + SEAM_INTERNATIONAL_PAGE_SUFFIX);
		File seamInternationalPageTo = new File(projectPath + TCKTest.WEB_CONTENT_SUFFIX 
				+ SEAM_INTERNATIONAL_PAGE_SUFFIX);
		if (!FileUtil.copyFile(seamInternationalPageFrom, seamInternationalPageTo))
			return false;

		project.refreshLocal(IResource.DEPTH_INFINITE, null);
		project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
		return true;
	}

	@Override
	protected void tearDown() throws Exception {
		boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
		project.delete(true, true, null);
		JobUtils.waitForIdle();
		ResourcesUtils.setBuildAutomatically(saveAutoBuild);
	}
}
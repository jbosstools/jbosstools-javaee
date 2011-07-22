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
package org.jboss.tools.cdi.seam.core.test;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.test.tck.TCKTest;
import org.jboss.tools.cdi.seam.core.CDISeamCorePlugin;
import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.jst.jsp.test.TestUtil;
import org.jboss.tools.jst.jsp.test.ca.ContentAssistantTestCase;
import org.jboss.tools.jst.web.kb.internal.validation.ValidatorManager;
import org.jboss.tools.test.util.JobUtils;
import org.osgi.framework.Bundle;

/**
 * @author Alexey Kazakov
 * @author Victor Rubezhny
 */
public class SeamResourceBundlesTest extends TCKTest {
	protected static final String SEAM_INTERNATIONAL_LIB_SUFFIX = "/seam-international.jar";
	protected static final String SEAM_INTERNATIONAL_PAGE_SUFFIX = "/seam-international.xhtml";
	protected static final String DEFAULT_RESOURCE_BUNDLE_SUFFIX = "/messages.properties";
	protected static final String DE_RESOURCE_BUNDLE_SUFFIX = "/messages_de.properties";
	protected static final String RESOURCES_SUFFIX = "/resources";
	
	protected static String LIB_SUFFIX = "/lib";

	private ContentAssistantTestCase caTest = new ContentAssistantTestCase();
	private static final String PAGE_NAME = "WebContent/seam-international.xhtml";
	private String[] resourceBundleNames = new String[] {"bundles.messages"};
	private String[] defaultResourceBundleNameProperties = new String[] {"bundles.messages.home_header", "bundles.messages.home_body", "bundles.messages.home_note"};
	private String[] germanResourceBundleNameProperties = new String[] {"bundles.messages.de_home_header", "bundles.messages.de_home_body", "bundles.messages.de_home_note"};
	
	public void testResourceBundles() {
		// Set up seam-international.jar library into the project's WEB-INF/lib folder
		try {
			assertTrue("Cannot set up SEAM International module and resource bundles into a test project", 
					setUpSeamInternationalLibraryAndResourceBundle());
		} catch (Exception e) {
			fail("Cannot set up SEAM International module and resource bundles into a test project: " 
					+ e.getLocalizedMessage());
		}
		
		// Test that seam-international module is successfully installed on the CDI project
		assertTrue("SEAM International module is not installed or incorrectly installed", 
				CDICorePlugin.getCDI(tckProject, true).getExtensionManager()
					.isCDIExtensionAvailable(CDISeamCorePlugin.CDI_INTERNATIONAL_RUNTIME_EXTENTION));

		// Perform CA test
		caTest.checkProposals(PAGE_NAME, "value=\"#{", 9, resourceBundleNames, false);
		caTest.checkProposals(PAGE_NAME, "value=\"#{bundles.messages.", 26, defaultResourceBundleNameProperties, false);
		caTest.checkProposals(PAGE_NAME, "value=\"#{bundles.messages.", 26, germanResourceBundleNameProperties, false);

	}
	
	private boolean setUpSeamInternationalLibraryAndResourceBundle() throws Exception {
		Bundle b = Platform.getBundle(CDISeamCoreAllTests.PLUGIN_ID);
		String projectPath = tckProject.getLocation().toOSString();
		String resourcePath = FileLocator.resolve(b.getEntry(RESOURCES_SUFFIX)).getFile();

		File seamInternationalLibFrom = new File(resourcePath + SEAM_INTERNATIONAL_LIB_SUFFIX);
		File seamInternationalLibTo = new File(projectPath + WEB_CONTENT_SUFFIX + WEB_INF_SUFFIX 
				+ LIB_SUFFIX + SEAM_INTERNATIONAL_LIB_SUFFIX);
		if (!FileUtil.copyFile(seamInternationalLibFrom, seamInternationalLibTo))
			return false;

		File defaultResourceBundleFrom = new File(resourcePath + DEFAULT_RESOURCE_BUNDLE_SUFFIX);
		File defaultResourceBundleTo = new File(projectPath + JAVA_SOURCE_SUFFIX + DEFAULT_RESOURCE_BUNDLE_SUFFIX);
		if (!FileUtil.copyFile(defaultResourceBundleFrom, defaultResourceBundleTo))
			return false;

		File germanResourceBundleFrom = new File(resourcePath + DE_RESOURCE_BUNDLE_SUFFIX);
		File germanResourceBundleTo = new File(projectPath + JAVA_SOURCE_SUFFIX + DE_RESOURCE_BUNDLE_SUFFIX);
		if (!FileUtil.copyFile(germanResourceBundleFrom, germanResourceBundleTo))
			return false;

		File seamInternationalPageFrom = new File(resourcePath + SEAM_INTERNATIONAL_PAGE_SUFFIX);
		File seamInternationalPageTo = new File(projectPath + WEB_CONTENT_SUFFIX 
				+ SEAM_INTERNATIONAL_PAGE_SUFFIX);
		if (!FileUtil.copyFile(seamInternationalPageFrom, seamInternationalPageTo))
			return false;

		ValidatorManager.setStatus(ValidatorManager.RUNNING);
		tckProject.refreshLocal(IResource.DEPTH_INFINITE, null);
		JobUtils.waitForIdle();
		tckProject.build(IncrementalProjectBuilder.CLEAN_BUILD, new NullProgressMonitor());
		JobUtils.waitForIdle();
		tckProject.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
		TestUtil.waitForValidation();

		caTest.setProject(tckProject);
		return true;
	}
}
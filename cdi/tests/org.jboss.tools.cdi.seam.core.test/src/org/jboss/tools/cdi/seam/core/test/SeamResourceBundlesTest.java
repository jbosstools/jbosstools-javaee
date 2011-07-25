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
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.test.tck.TCKTest;
import org.jboss.tools.cdi.seam.core.CDISeamCorePlugin;
import org.jboss.tools.cdi.seam.core.international.el.CDIInternationalMessagesELResolver;
import org.jboss.tools.common.el.core.resolver.ELContext;
import org.jboss.tools.common.el.core.resolver.ELResolver;
import org.jboss.tools.common.text.TextProposal;
import org.jboss.tools.common.text.ext.util.Utils;
import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.jst.jsp.test.TestUtil;
import org.jboss.tools.jst.jsp.test.ca.ContentAssistantTestCase;
import org.jboss.tools.jst.web.kb.PageContextFactory;
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
	private String[] textDefaultResourceBundleNameProperties = new String[] {"home_header", "home_body", "home_note"};
	private String[] textGermanResourceBundleNameProperties = new String[] {"de_home_header", "de_home_body", "de_home_note"};
	
	boolean bReadyForTesting = false;
	String errMessage = null;
	
	public SeamResourceBundlesTest () {
		super();
		
		boolean setupOK = true;
		// Set up seam-international.jar library into the project's WEB-INF/lib folder
		try {
			setupOK = setUpSeamInternationalLibraryAndResourceBundle();
			if (!setupOK)
			 errMessage = "Cannot set up SEAM International module and resource bundles into a test project";
		} catch (Exception e) {
			setupOK = false;
			errMessage = "Cannot set up SEAM International module and resource bundles into a test project: " 
					+ e.getLocalizedMessage();
		}
		
		if (!setupOK) 
			return;
		
		// Test that seam-international module is successfully installed on the CDI project
		setupOK = CDICorePlugin.getCDI(tckProject, true).getExtensionManager()
				.isCDIExtensionAvailable(CDISeamCorePlugin.CDI_INTERNATIONAL_RUNTIME_EXTENTION);
		if (!setupOK)
			errMessage = "SEAM International module is not installed or incorrectly installed";
		
		bReadyForTesting = setupOK;
	}


	/**
	 * The method checks if CDIInternationalMessagesELResolver present among the EL Resolvers,
	 * then if the required resolver found uses it to retrieve and test proposals for bundle and their properties
	 */
	public void testCDIInternationalMessages () {
		assertTrue(errMessage, bReadyForTesting);
		
		IFile page = tckProject.getFile(PAGE_NAME);
		assertTrue("Test page not found: " + PAGE_NAME, (page != null && page.exists())); 
		ELContext elContext = PageContextFactory.createPageContext(page);
		
		ELResolver[] elResolvers = elContext.getElResolvers();
		ELResolver cdiInternationalModuleResolver = null;
		if (elResolvers != null) {
			for (ELResolver r : elResolvers) {
				if (r instanceof CDIInternationalMessagesELResolver) {
					cdiInternationalModuleResolver = r;
					break;
				}
			}
		}
		assertNotNull("Seam International module resolver is not set up on the project", cdiInternationalModuleResolver);
		
		List<TextProposal> bundleProposals = cdiInternationalModuleResolver.getProposals(elContext, "value=\"#{", 1);
		assertTrue("Seam International module resolver didn't return proposals for bundles", 
				(bundleProposals != null && bundleProposals.size() > 0));
		proposalsExist(bundleProposals, resourceBundleNames);
		
		List<TextProposal> bundlePropertyProposals = cdiInternationalModuleResolver.getProposals(elContext, "value=\"#{bundles.messages.", 1);
		assertTrue("Seam International module resolver didn't return proposals for bundles", 
				(bundlePropertyProposals != null && bundlePropertyProposals.size() > 0));
		proposalsExist(bundlePropertyProposals, textDefaultResourceBundleNameProperties);
		proposalsExist(bundlePropertyProposals, textGermanResourceBundleNameProperties);
	}
	
	public void proposalsExist(List<TextProposal> res, String[] proposals) {
        TextProposal[] result = res.toArray(new TextProposal[res.size()]);
        for (int i = 0; i < proposals.length; i++) {
        	boolean found = compareTextProposal(proposals[i], result);
            assertTrue("Proposal " + proposals[i] + " not found!", found ); //$NON-NLS-1$ //$NON-NLS-2$
        }
	}
	
	public boolean compareTextProposal(String proposal, TextProposal[] proposals){
		for (int i = 0; i < proposals.length; i++) {
			String replacementString = proposals[i].getReplacementString().toLowerCase();
			if (replacementString.equalsIgnoreCase(proposal)) return true;				
				// For an attribute value proposal there will be the quote characters

			replacementString = Utils.trimQuotes(replacementString);
			if (replacementString.equalsIgnoreCase(proposal)) return true;
			
		}
		return false;
	}

	/**
	 * The method tests CA on CDI Seam International Module Resource Bundles
	 */
	public void testResourceBundles() {
		assertTrue(errMessage, bReadyForTesting);
		
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
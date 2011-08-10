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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.seam.core.CDISeamCorePlugin;
import org.jboss.tools.cdi.seam.core.international.el.CDIInternationalMessagesELResolver;
import org.jboss.tools.common.el.core.resolver.ELContext;
import org.jboss.tools.common.el.core.resolver.ELResolver;
import org.jboss.tools.common.text.TextProposal;
import org.jboss.tools.common.text.ext.util.Utils;
import org.jboss.tools.jst.jsp.test.ca.ContentAssistantTestCase;
import org.jboss.tools.jst.text.ext.hyperlink.ELHyperlink;
import org.jboss.tools.jst.text.ext.hyperlink.ELHyperlinkDetector;
import org.jboss.tools.jst.text.ext.test.HyperlinkTestUtil;
import org.jboss.tools.jst.text.ext.test.HyperlinkTestUtil.TestHyperlink;
import org.jboss.tools.jst.text.ext.test.HyperlinkTestUtil.TestRegion;
import org.jboss.tools.jst.web.kb.PageContextFactory;

/**
 * @author Victor Rubezhny
 */
public class SeamResourceBundlesTest extends SeamCoreTest {

	private ContentAssistantTestCase caTest = new ContentAssistantTestCase();
	private static final String PAGE_NAME = "WebContent/seam-international.xhtml";
	private String[] resourceBundleNames = new String[] {"bundles.messages"};
	private String[] defaultResourceBundleNameProperties = new String[] {"bundles.messages.home_header", "bundles.messages.home_body", "bundles.messages.home_note"};
	private String[] germanResourceBundleNameProperties = new String[] {"bundles.messages.de_home_header", "bundles.messages.de_home_body", "bundles.messages.de_home_note"};
	private String[] textDefaultResourceBundleNameProperties = new String[] {"home_header", "home_body", "home_note"};
	private String[] textGermanResourceBundleNameProperties = new String[] {"de_home_header", "de_home_body", "de_home_note"};

	/**
	 * Test that seam-international module is successfully installed on the CDI project
	 * @throws Exception 
	 */
	public void testExtension() throws Exception {
		assertTrue("SEAM International module is not installed or incorrectly installed", CDICorePlugin.getCDI(getTestProject(), true).getExtensionManager().isCDIExtensionAvailable(CDISeamCorePlugin.CDI_INTERNATIONAL_RUNTIME_EXTENTION));
	}

	/**
	 * The method checks if CDIInternationalMessagesELResolver present among the EL Resolvers,
	 * then if the required resolver found uses it to retrieve and test proposals for bundle and their properties
	 * @throws Exception 
	 */
	public void testCDIInternationalMessages () throws Exception {
		IFile page = getTestProject().getFile(PAGE_NAME);
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
	 * @throws Exception 
	 */
	public void testResourceBundles() throws Exception {
		// Perform CA test
		caTest.setProject(getTestProject());
		caTest.checkProposals(PAGE_NAME, "value=\"#{", 9, resourceBundleNames, false);
		caTest.checkProposals(PAGE_NAME, "value=\"#{bundles.messages.", 26, defaultResourceBundleNameProperties, false);
		caTest.checkProposals(PAGE_NAME, "value=\"#{bundles.messages.", 26, germanResourceBundleNameProperties, false);
	}

	/**
	 * The method tests CA on CDI Seam International Module Resource Bundles
	 */
	public void testSeamInternationalHyperlinks() throws Exception {
		// Perform Hyperlink test
		ArrayList<TestRegion> regionList = new ArrayList<TestRegion>();
		regionList.add(new TestRegion(381, 15, new TestHyperlink[]{new TestHyperlink(ELHyperlink.class, "Open bundle 'messages'", null)})); 
		regionList.add(new TestRegion(398, 10, new TestHyperlink[]{new TestHyperlink(ELHyperlink.class, "Open property 'home_header' of bundle 'messages'", null)})); 

		HyperlinkTestUtil.checkRegions(getTestProject(), PAGE_NAME, regionList, new ELHyperlinkDetector());
	}
}
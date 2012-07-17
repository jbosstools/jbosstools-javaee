/*******************************************************************************
 * Copyright (c) 2010-2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.seam3.bot.test.tests;

import java.util.Arrays;
import java.util.List;

import org.jboss.tools.cdi.bot.test.CDIConstants;
import org.jboss.tools.cdi.seam3.bot.test.base.Seam3TestBase;
import org.jboss.tools.cdi.seam3.bot.test.util.SeamLibrary;
import org.jboss.tools.ui.bot.ext.helper.ContentAssistHelper;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * @author jjankovi
 *
 */
public class SeamConfigCodeCompletionTest extends Seam3TestBase {

	private static String projectName = "seamConfigCodeCompletion";
	private static final String SEAM_CONFIG = "seam-beans.xml";
	
	@BeforeClass
	public static void setup() {
		importSeam3ProjectWithLibrary(projectName, SeamLibrary.SOLDER_3_1);
		openSeamConfig();
	}
	
	/**
	 * In context of <beans> ... </beans>
	 * Suggest all classes in available packages.
	 */
	@Test
	public void testClassesCodeCompletion() {

		List<String> expectedProposalList = Arrays.asList("r:Envelope - test","r:Report - test");
		ContentAssistHelper.checkContentAssistContent(bot, 
				SEAM_CONFIG, "<r:Report >", -1, 0, expectedProposalList, false);
		
	}
	
	/**
	 * In context of <beans> ... </beans>
	 * Suggest all annotation types in available packages.
	 */
	@Test
	public void testAnnotationsCodeCompletion() {
		
		List<String> expectedProposalList = Arrays.asList("r:Q1 - test","r:S1 - test");
		ContentAssistHelper.checkContentAssistContent(bot, 
				SEAM_CONFIG, "<r:Report >", -1, 0, expectedProposalList, false);
		
	}
	
	/**
	 *  In context of tag header <r:Report | >
		Suggest all fields available if Report is class;
	 */
	@Test
	public void testClassInFieldsCodeCompletion() {
		
		List<String> expectedProposalList = Arrays.asList("annotatedValue");
		ContentAssistHelper.checkContentAssistContent(bot, 
				SEAM_CONFIG, "<r:Report >", 10, 0, expectedProposalList, false);
		
	}
	
	/**
	 *  In context of tag header <r:Report | >
		Suggest all methods available if Report is annotation type.
	 */
	@Test
	public void testAnnotationInMethodsCodeCompletion() {
		
		List<String> expectedProposalList = Arrays.asList("someMethod");
		ContentAssistHelper.checkContentAssistContent(bot, 
				SEAM_CONFIG, "<r:S1 >", 6, 0, expectedProposalList, false);
		
	}
	
	/**
	 *  In context of tag content <r:Report> | </r:Report>
		Suggest <s:replaces/> and <s:modifies/> if Report is class;
	 */
	@Test
	public void testReplacesModifiesCodeCompletion() {
		
		List<String> expectedProposalList = Arrays.asList("s:replaces", "s:modifies");
		ContentAssistHelper.checkContentAssistContent(bot, 
				SEAM_CONFIG, "<r:Report >", 11, 0, expectedProposalList, false);
		
	}
	
	/**
	 * In context of tag content <r:Report> | </r:Report>
	   Suggest <s:parameters> if Report is class;
	 */
	@Test
	public void testParametersCodeCompletion() {
		
		List<String> expectedProposalList = Arrays.asList("s:parameters");
		ContentAssistHelper.checkContentAssistContent(bot, 
				SEAM_CONFIG, "<r:Report >", 11, 0, expectedProposalList, false);
		
	}
	
	/**
	 * In context of tag content <r:Report> | </r:Report>
	   Suggest all fields and methods available if Report is class;
	 */
	@Test
	public void testClassFieldsAndMethodsCodeCompletion() {
		
		List<String> expectedProposalList = Arrays.asList("r:value", "r:someMethod");
		ContentAssistHelper.checkContentAssistContent(bot, 
				SEAM_CONFIG, "<r:Envelope >", 13, 0, expectedProposalList, false);
		
	}
	
	/**
	 * In context of tag content <r:Report> | </r:Report>
	   Suggest all methods available if Report is annotation type;
	 */
	@Test
	public void testAnnotationMethodsCodeCompletion() {
		
		List<String> expectedProposalList = Arrays.asList("r:someMethod");
		ContentAssistHelper.checkContentAssistContent(bot, 
				SEAM_CONFIG, "<r:S1 >", 7, 0, expectedProposalList, false);
		
	}
	
	/**
	 * In context of tag content <r:Report> | </r:Report>
	   Suggest all annotation types in available packages.
	 */
	@Test
	public void testAnnotationsInPackageCodeCompletion() {
		
		List<String> expectedProposalList = Arrays.asList("r:Q1 - test", "r:S1 - test");
		ContentAssistHelper.checkContentAssistContent(bot, 
				SEAM_CONFIG, "<r:Report >", 11, 0, expectedProposalList, false);
		
	}
	
	/**
	In context of <r:Report> <r:address> | </r:address> </r:Report>
	Suggest <s:value> if 'address' is class field or annotation type method;
	 */
	@Test
	public void testValueCodeCompletion() {
		
		List<String> expectedProposalList = Arrays.asList("s:value");
		ContentAssistHelper.checkContentAssistContent(bot, 
				SEAM_CONFIG, "<r:annotatedValue>", 18, 0, expectedProposalList, false);
		
	}
	
	/**
	In context of <r:Report> <r:address> | </r:address> </r:Report>
	Suggest <s:entry> if 'address' is class field or annotation type method (maybe we should check that it is map);
	 */
	@Test
	public void testEntryCodeCompletion() {
		
		List<String> expectedProposalList = Arrays.asList("s:entry");
		ContentAssistHelper.checkContentAssistContent(bot, 
				SEAM_CONFIG, "<r:annotatedValue>", 18, 0, expectedProposalList, false);
		
	}
	
	/**
	In context of <r:Report> <r:address> | </r:address> </r:Report>
	Suggest <s:parameters> if 'address' is class method;
	 */
	@Test
	public void testMethodParametersCodeCompletion() {
	
		List<String> expectedProposalList = Arrays.asList("s:parameters");
		ContentAssistHelper.checkContentAssistContent(bot, 
				SEAM_CONFIG, "<r:someMethod>", 14, 0, expectedProposalList, false);
		
	}
	
	/**
	In context of <s:entry> | </s:entry>
	Suggest <s:value> and <s:key> 
	 */
	@Test
	public void testValueAndKeyCodeCompletion() {
		
		List<String> expectedProposalList = Arrays.asList("s:key", "s:value");
		ContentAssistHelper.checkContentAssistContent(bot, 
				SEAM_CONFIG, "<s:entry>", 9, 0, expectedProposalList, false);
		
	}
	
	/**
	In context of <s:value> | </s:value> or <s:key> | </s:key>
	Suggest all classes in available packages since value may be set as an inline bean.
	 */
	@Test
	public void testInlineBeanCompletion() {
		
		List<String> expectedProposalList = Arrays.asList("r:Report - test", 
				"r:Envelope - test", "r:Q1 - test", "r:S1 - test");
		ContentAssistHelper.checkContentAssistContent(bot, 
				SEAM_CONFIG, "<s:value>", 9, 0, expectedProposalList, false);
		
		ContentAssistHelper.checkContentAssistContent(bot, 
				SEAM_CONFIG, "<s:key>", 7, 0, expectedProposalList, false);
		
	}
	
	/**
	In context of xmlns:*="|"
	Suggest "urn:java:" with available packages.
	 */
	@Test
	public void testAvailablePackagesCompletion() {
		
		List<String> expectedProposalList = Arrays.asList("test", "org", "com");
		ContentAssistHelper.checkContentAssistContent(bot, 
				SEAM_CONFIG, "xmlns:s=\"", 9, 0, expectedProposalList, false);
		
	}
	
	private static void openSeamConfig() {
		packageExplorer.openFile(projectName, CDIConstants.WEBCONTENT, 
				CDIConstants.WEB_INF, SEAM_CONFIG);
		bot.cTabItem("Source").activate();
	}
	
}

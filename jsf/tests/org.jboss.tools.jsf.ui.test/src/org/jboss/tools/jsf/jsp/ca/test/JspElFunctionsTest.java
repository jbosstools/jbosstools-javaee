/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.jsf.jsp.ca.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.jboss.tools.jst.jsp.test.ca.ContentAssistantTestCase;
import org.jboss.tools.test.util.TestProjectProvider;

/**
 * Test for CA on JSP EL Functions
 * 
 * @author Victor V. Rubezhny
 */
public class JspElFunctionsTest extends ContentAssistantTestCase {
   private static final String PROJECT_NAME = "testJSFProject"; //$NON-NLS-1$
   private static final String PAGE_NAME = "/WebContent/templates/outputWeekDays.xhtml"; //$NON-NLS-1$
   private TestProjectProvider provider = null;
   private static String STRING_TO_FIND_TARGET = "target:";
   private static String STRING_TO_FIND_CONVERT = "target:co";
   private static String STRING_TO_FIND_LOOP = "target:lo";
   private static String PREFIX_TO_CHECK = "#{";
   
   public static Test suite() {
       return new TestSuite(JspElFunctionsTest.class);
   }
   
   public void setUp() throws Exception {
       provider = new TestProjectProvider("org.jboss.tools.jsf.ui.test", null, PROJECT_NAME,false);  //$NON-NLS-1$
       project = provider.getProject();
   }

   protected void tearDown() throws Exception {
       if(provider != null) {
           provider.dispose();
       }
   }
   
   
   public void testJspElFunctionsCATestFuncNamespace(){
       final String[] proposals = new String[]{
               "target" //$NON-NLS-1$
       };

       openEditor(PAGE_NAME);
       IRegion reg = null;
		try {
			reg = new FindReplaceDocumentAdapter(this.document).find(0, STRING_TO_FIND_TARGET, true, false, false, false); //$NON-NLS-1$
		} catch (BadLocationException e) {
			fail(e.getMessage());
		}
		assertNotNull("Cannot find text region to test: \'" + STRING_TO_FIND_TARGET + "\'", reg);

		IRegion testReg = null;
		try {
			testReg = new FindReplaceDocumentAdapter(this.document).find(reg.getOffset() - PREFIX_TO_CHECK.length(), PREFIX_TO_CHECK, true, false, false, false); //$NON-NLS-1$
		} catch (BadLocationException e) {
			fail(e.getMessage());
		}
		assertNotNull("Text region to test doesn\'t start with prefix: \'" + PREFIX_TO_CHECK + "\'", testReg);
		assertTrue("Text region to test doesn\'t start with prefix: \'" + PREFIX_TO_CHECK + "\'", (reg.getOffset() - testReg.getOffset() == PREFIX_TO_CHECK.length()));
		
		
       final ICompletionProposal[] rst = checkProposals(PAGE_NAME,reg.getOffset(), proposals, false);
       
       closeEditor();
   }

   public void testJspElFunctionsCATestFuncs(){
       final String[] proposals = new String[]{
               "target:convertToInteger()", //$NON-NLS-1$
               "target:loopModel()" //$NON-NLS-1$
       };
       
       openEditor(PAGE_NAME);
       IRegion reg = null;
		try {
			reg = new FindReplaceDocumentAdapter(this.document).find(0, STRING_TO_FIND_TARGET, true, false, false, false); //$NON-NLS-1$
		} catch (BadLocationException e) {
			fail(e.getMessage());
		}
		assertNotNull("Cannot find text region to test: \'" + STRING_TO_FIND_TARGET + "\'", reg);

		IRegion testReg = null;
		try {
			testReg = new FindReplaceDocumentAdapter(this.document).find(reg.getOffset() - PREFIX_TO_CHECK.length(), PREFIX_TO_CHECK, true, false, false, false); //$NON-NLS-1$
		} catch (BadLocationException e) {
			fail(e.getMessage());
		}
		assertNotNull("Text region to test doesn\'t start with prefix: \'" + PREFIX_TO_CHECK + "\'", testReg);
		assertTrue("Text region to test doesn\'t start with prefix: \'" + PREFIX_TO_CHECK + "\'", (reg.getOffset() - testReg.getOffset() == PREFIX_TO_CHECK.length()));
		
		
       final ICompletionProposal[] rst = checkProposals(PAGE_NAME,reg.getOffset() + STRING_TO_FIND_TARGET.length(), proposals, false);
       
       closeEditor();
   }

   public void testJspElFunctionsCATestFuncsConvertFunc(){
       final String[] proposals = new String[]{
               "target:convertToInteger()" //$NON-NLS-1$
       };
       
       openEditor(PAGE_NAME);
       IRegion reg = null;
		try {
			reg = new FindReplaceDocumentAdapter(this.document).find(0, STRING_TO_FIND_CONVERT, true, false, false, false); //$NON-NLS-1$
		} catch (BadLocationException e) {
			fail(e.getMessage());
		}
		assertNotNull("Cannot find text region to test: \'" + STRING_TO_FIND_CONVERT + "\'", reg);

		IRegion testReg = null;
		try {
			testReg = new FindReplaceDocumentAdapter(this.document).find(reg.getOffset() - PREFIX_TO_CHECK.length(), PREFIX_TO_CHECK, true, false, false, false); //$NON-NLS-1$
		} catch (BadLocationException e) {
			fail(e.getMessage());
		}
		assertNotNull("Text region to test doesn\'t start with prefix: \'" + PREFIX_TO_CHECK + "\'", testReg);
		assertTrue("Text region to test doesn\'t start with prefix: \'" + PREFIX_TO_CHECK + "\'", (reg.getOffset() - testReg.getOffset() == PREFIX_TO_CHECK.length()));
		
		
       final ICompletionProposal[] rst = checkProposals(PAGE_NAME,reg.getOffset() + STRING_TO_FIND_CONVERT.length(), proposals, false);
       
       closeEditor();
   }

   public void testJspElFunctionsCATestFuncsLoopFunc(){
       final String[] proposals = new String[]{
               "target:loopModel()" //$NON-NLS-1$
       };
       
       openEditor(PAGE_NAME);
       IRegion reg = null;
		try {
			reg = new FindReplaceDocumentAdapter(this.document).find(0, STRING_TO_FIND_LOOP, true, false, false, false); //$NON-NLS-1$
		} catch (BadLocationException e) {
			fail(e.getMessage());
		}
		assertNotNull("Cannot find text region to test: \'" + STRING_TO_FIND_LOOP + "\'", reg);

		IRegion testReg = null;
		try {
			testReg = new FindReplaceDocumentAdapter(this.document).find(reg.getOffset() - PREFIX_TO_CHECK.length(), PREFIX_TO_CHECK, true, false, false, false); //$NON-NLS-1$
		} catch (BadLocationException e) {
			fail(e.getMessage());
		}
		assertNotNull("Text region to test doesn\'t start with prefix: \'" + PREFIX_TO_CHECK + "\'", testReg);
		assertTrue("Text region to test doesn\'t start with prefix: \'" + PREFIX_TO_CHECK + "\'", (reg.getOffset() - testReg.getOffset() == PREFIX_TO_CHECK.length()));
		
		
       final ICompletionProposal[] rst = checkProposals(PAGE_NAME,reg.getOffset() + STRING_TO_FIND_LOOP.length(), proposals, false);
       
       closeEditor();
   }

}

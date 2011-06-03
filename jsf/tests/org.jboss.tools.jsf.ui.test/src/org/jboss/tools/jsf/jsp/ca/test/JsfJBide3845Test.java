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

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.jboss.tools.common.base.test.contentassist.CATestUtil;
import org.jboss.tools.jst.jsp.test.ca.ContentAssistantTestCase;
import org.jboss.tools.test.util.TestProjectProvider;

/**
 * Test cast testing http://jira.jboss.com/jira/browse/JBIDE-3845 issue.
 * 
 * @author Victor V. Rubezhny
 *
 */
public class JsfJBide3845Test extends ContentAssistantTestCase {
   private static final String PROJECT_NAME = "Jbide3845Test"; //$NON-NLS-1$
   private static final String JSP_PAGE_NAME = "/WebContent/pages/jsp_page.jsp"; //$NON-NLS-1$
   private static final String XHTML_PAGE_NAME = "/WebContent/pages/xhtml_page.xhtml"; //$NON-NLS-1$
   private static final String[] PROPOSALS = new String[] {"Message['org.jboss.tools.long.property.Name']"}; //$NON-NLS-1$

   private TestProjectProvider provider = null;

   
   private static final String STRING_TO_FIND = "#{Message[";

   public static Test suite() {
       return new TestSuite(JsfJBide3845Test.class);
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
   
   public void testJbide3845OnJspPage(){
		doTheResourceBundleCAForALongPropertyNameTest(JSP_PAGE_NAME, STRING_TO_FIND, PROPOSALS);
   }

   public void testJbide6061OnXhtmlPage(){
		doTheResourceBundleCAForALongPropertyNameTest(XHTML_PAGE_NAME, STRING_TO_FIND, PROPOSALS);
   }
   
   protected void doTheResourceBundleCAForALongPropertyNameTest(String pageName, String textToFind, String[] proposals) {
       openEditor(pageName);
       IRegion reg=null;
		try {
			reg = new FindReplaceDocumentAdapter(this.document).find(0, textToFind, true, false, false, false); //$NON-NLS-1$
		} catch (BadLocationException e) {
			fail(e.getMessage());
		}
		
		assertNotNull("Cannot find a text region to test", reg);

       final ICompletionProposal[] rst = checkProposals(pageName,reg.getOffset() + textToFind.length(), proposals, false);

       closeEditor();
   }
}

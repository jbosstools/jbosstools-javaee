/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.jsf.test.jbide;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.wst.sse.ui.StructuredTextViewerConfiguration;
import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.jst.jsp.contentassist.AutoContentAssistantProposal;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.jst.jsp.jspeditor.JSPTextEditor;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.jboss.tools.vpe.ui.test.VpeTest;

/**
 * @author Max Areshkau
 * 
 * JUnit test for http://jira.jboss.com/jira/browse/JBIDE-788
 */
public class JBIDE788Test extends VpeTest {

	private static final String CA_NAME = "org.eclipse.wst.html.HTML_DEFAULT"; //$NON-NLS-1$

	public JBIDE788Test(String name) {
		super(name);
	}

	/**
	 * Tests inner nodes include URI
	 * 
	 * @throws Throwable
	 */
	public void testCAforIncludeTaglibInInenerNodes() throws Throwable {
		// wait
		TestUtil.waitForJobs();
		// set exception
		setException(null);
		// Tests CA

		checkOfCAByStartString(CA_NAME, "JBIDE/788/TestChangeUriInInnerNodes.xhtml","s:validateFormat",11,2);    //$NON-NLS-1$//$NON-NLS-2$
		checkOfCAByStartString(CA_NAME, "JBIDE/788/TestChangeUriInInnerNodes.xhtml","rich:validateA", 14,14); //$NON-NLS-1$ //$NON-NLS-2$
		checkOfCAByStartString(CA_NAME, "JBIDE/788/TestChangeUriInInnerNodes.xhtml","c:otherwi",18,6);  //$NON-NLS-1$//$NON-NLS-2$

		// check exception
		if (getException() != null) {

			throw getException();
		}
	}

	/**
	 * Tests Path proposals of CA
	 */
	public void testCAPathProposals() throws Throwable {
		// wait
		TestUtil.waitForJobs();
		// set exception
		setException(null);
		// Tests CA

		ICompletionProposal[] results = checkOfCAByStartString(CA_NAME, "JBIDE/788/testCAMessageBundlesAndEL.xhtml","",11,31,false); //$NON-NLS-1$ //$NON-NLS-2$
		assertNotNull(results);
		assertTrue("The length should be more than 0",results.length>0); //$NON-NLS-1$
		boolean str_exists=false;
		for (ICompletionProposal completionProposal : results) {
			String displayString = ((ICompletionProposal) completionProposal).getDisplayString();		
			if(displayString.startsWith("msg.")) { //$NON-NLS-1$
				str_exists=true;
			} 
			
		}
		assertEquals("$msg should be in proposals",true, str_exists);
		str_exists=false;
		results = checkOfCAByStartString(CA_NAME, "JBIDE/788/testCAPathProposals.xhtml","",11,41,false);  //$NON-NLS-1$//$NON-NLS-2$
		assertNotNull(results);
		for(ICompletionProposal completionProposal : results) {
			String displayString = ((ICompletionProposal) completionProposal).getDisplayString();
			if(displayString.contains("templates")) { //$NON-NLS-1$ //$NON-NLS-2$
				str_exists=true;
			}  
		}
		assertEquals("path proposala should be in proposals",true, str_exists);
		// check exception
		if (getException() != null) {

			throw getException();
		}
	}

	/**
	 * Tests CA for proposals for JSFC
	 * 
	 * @throws Throwable
	 */
	public void testCAforForJSFCProposals() throws Throwable {
		// wait
		TestUtil.waitForJobs();
		// set exception
		setException(null);
		// Tests CA
		ICompletionProposal[] results =checkOfCAByStartString(CA_NAME, "JBIDE/788/testCAMessageBundlesAndEL.xhtml","",21,58);  //$NON-NLS-1$//$NON-NLS-2$
		assertNotNull(results);
		assertTrue(results.length>=2);
		for(ICompletionProposal completionProposal : results) {
			if(completionProposal instanceof AutoContentAssistantProposal ) {
				String displayString = ((ICompletionProposal) completionProposal).getDisplayString();
				if(!(displayString.contains("h:command") || displayString.contains("New JSF EL"))) { //$NON-NLS-1$ //$NON-NLS-2$
					fail("String doesn't matches"); //$NON-NLS-1$
				}
			}
		}

		// check exception
		if (getException() != null) {

			throw getException();
		}

	}

	/**
	 * Tests CA on html files
	 * 
	 * @throws Throwable
	 */
	public void testCAforHtmlFiles() throws Throwable {
		// wait
		TestUtil.waitForJobs();
		// set exception
		setException(null);
		// Tests CA
		ICompletionProposal[] results =checkOfCAByStartString(CA_NAME, "JBIDE/788/testCAforHtml.html", "", 5, 13,false);  //$NON-NLS-1$//$NON-NLS-2$

		assertNotNull(results);
		assertTrue("The lenft should be more than 0",results.length>0); //$NON-NLS-1$
		boolean isMatches=true;
		for (ICompletionProposal completionProposal : results) {
			if(completionProposal instanceof AutoContentAssistantProposal ) {
				String displayString = ((ICompletionProposal) completionProposal).getDisplayString();
				
				if(!displayString.startsWith("ta")) { //$NON-NLS-1$
					isMatches=false;
				}
			}	
		}
		assertTrue("Proposals doesn't match to entered string",isMatches); //$NON-NLS-1$

		// check exception
		if (getException() != null) {

			throw getException();
		}
	}

	/**
	 * Tests CA on jsp files
	 * 
	 * @throws Throwable
	 */
	public void testCAforJSPFiles() throws Throwable {
		// wait
		TestUtil.waitForJobs();
		// set exception
		setException(null);
		// Tests CA

		// cursor will set after "outputText" tag
		ICompletionProposal[] results = checkOfCAByStartString(CA_NAME, "JBIDE/788/testCAforJSP.jsp", "h:outp",26,14,false); //$NON-NLS-1$ //$NON-NLS-2$

		for (ICompletionProposal completionProposal : results) {

			String displayString = ((ICompletionProposal) completionProposal).getDisplayString();
			 
			if(completionProposal instanceof AutoContentAssistantProposal) {
				
				assertTrue(displayString.startsWith("h:outp")) ; //$NON-NLS-1$
			}
		}
		// check exception
		if (getException() != null) {

			throw getException();
		}
	}

	/**
	 * Tests CA on jsp files
	 * 
	 * @throws Throwable
	 */
	public void testCAforXHTMLFiles() throws Throwable {
		// wait
		TestUtil.waitForJobs();
		// set exception
		setException(null);

		// cursor will set after "<" simbol
		checkOfCAByStartString(CA_NAME, "JBIDE/788/testCAforXHTML.xhtml", "c", //$NON-NLS-1$ //$NON-NLS-2$
				15,12);

		// cursor will set after "outputText" tag
		checkOfCAByStartString(CA_NAME, "JBIDE/788/testCAforXHTML.xhtml", "s",  //$NON-NLS-1$//$NON-NLS-2$
				19,43);

		// check exception
		if (getException() != null) {

			throw getException();
		}
	}

	/**
	 * 
	 * @param caName
	 * @param testPagePath
	 * @param partOfString
	 * @param lineIndex
	 * @param linePosition
	 * @return
	 * @throws CoreException
	 */
    private ICompletionProposal[] checkOfCAByStartString(String caName, String testPagePath,
            String partOfString, int lineIndex, int linePosition) throws CoreException {
        return this.checkOfCAByStartString(caName, testPagePath, partOfString, lineIndex, linePosition,true);
        
    }
	/**
	 * 
	 * @param caName
	 * @param testPagePath
	 * @param partOfString
	 * @param lineIndex
	 * @param linePosition
	 * @param isCheck
	 * @return
	 * @throws CoreException
	 */
	
	private ICompletionProposal[] checkOfCAByStartString(String caName, String testPagePath,
            String partOfString, int lineIndex, int linePosition,boolean isCheck) throws CoreException {
        // get test page path
        IFile file = (IFile) TestUtil.getComponentPath(testPagePath,
        		JsfAllTests.IMPORT_PROJECT_NAME);
		assertNotNull("Could not open specified file. componentPage = " + testPagePath
				+ ";projectName = " + JsfAllTests.IMPORT_PROJECT_NAME, file);//$NON-NLS-1$

        IEditorInput input = new FileEditorInput(file);

        assertNotNull("Editor input is null", input); //$NON-NLS-1$

        // open and get editor
        ICompletionProposal[] results;
		try {
			JSPMultiPageEditor part = openEditor(input);
			
			int position = TestUtil.getLinePositionOffcet(part.getSourceEditor().getTextViewer(), lineIndex, linePosition);

			// insert string
			part.getSourceEditor().getTextViewer().getTextWidget()
			        .replaceTextRange(position, 0, partOfString);

			int newPosition = position + partOfString.length();

			// sets cursor position
			part.getSourceEditor().getTextViewer().getTextWidget().setCaretOffset(
			        newPosition);
			
			TestUtil.waitForJobs();
			TestUtil.delay(1000);
			SourceViewerConfiguration sourceViewerConfiguration = ((JSPTextEditor) part
			        .getSourceEditor()).getSourceViewerConfigurationForTest();
			// errase errors which can be on start of editor(for example xuklunner
			// not found)
			setException(null);
			StructuredTextViewerConfiguration stvc = (StructuredTextViewerConfiguration) sourceViewerConfiguration;
			IContentAssistant iContentAssistant = stvc
			        .getContentAssistant((ISourceViewer) part.getSourceEditor()
			                .getAdapter(ISourceViewer.class));
			assertNotNull(iContentAssistant);
			IContentAssistProcessor iContentAssistProcessor = iContentAssistant
			        .getContentAssistProcessor(caName);
			assertNotNull(iContentAssistProcessor);
			results = iContentAssistProcessor
			        .computeCompletionProposals(part.getSourceEditor()
			                .getTextViewer(), newPosition);

			// remove inserted string
			part.getSourceEditor().getTextViewer().getTextWidget()
			        .replaceTextRange(position, partOfString.length(), ""); //$NON-NLS-1$
			assertNotNull(results);
			assertTrue("Number of ca proposals shouldn't be a null",results.length>0); //$NON-NLS-1$
			if (isCheck) {
			    for (int i = 0; i < results.length; i++) {
			    	if(results[i] instanceof AutoContentAssistantProposal ) {
			        String displayString = ((ICompletionProposal) results[i]).getDisplayString();
			        assertNotNull(displayString);
			        assertEquals(true, displayString.startsWith(partOfString));
			    	}
			    }
			}
		} finally {
			closeEditors();
	        TestUtil.delay(1000L);
		}

        return results;
	}
}

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

	private static final String IMPORT_PROJECT_NAME = "jsfTest"; //$NON-NLS-1$

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

		checkOfCAByStartString(CA_NAME, "JBIDE/788/TestChangeUriInInnerNodes.xhtml","s:validateFormat",359);    //$NON-NLS-1$//$NON-NLS-2$
		checkOfCAByStartString(CA_NAME, "JBIDE/788/TestChangeUriInInnerNodes.xhtml","rich:validateA", 427); //$NON-NLS-1$ //$NON-NLS-2$
		checkOfCAByStartString(CA_NAME, "JBIDE/788/TestChangeUriInInnerNodes.xhtml","c:otherwi",493);  //$NON-NLS-1$//$NON-NLS-2$

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

		ICompletionProposal[] results = checkOfCAByStartString(CA_NAME, "JBIDE/788/testCAMessageBundlesAndEL.xhtml","",545,false); //$NON-NLS-1$ //$NON-NLS-2$
		assertNotNull(results);
		assertTrue("The lenft should be more than 0",results.length>0); //$NON-NLS-1$
		boolean isMatches=true;
		for (ICompletionProposal completionProposal : results) {
			String displayString = ((ICompletionProposal) completionProposal).getDisplayString();
			
			if(!displayString.startsWith("msg.")) { //$NON-NLS-1$
				isMatches=false;
			}
			
		}
		assertTrue("String not matches", isMatches); //$NON-NLS-1$
		
		results = checkOfCAByStartString(CA_NAME, "JBIDE/788/testCAPathProposals.xhtml","",511,false);  //$NON-NLS-1$//$NON-NLS-2$
		assertNotNull(results);
		isMatches = false;
		for(ICompletionProposal completionProposal : results) {
			String displayString = ((ICompletionProposal) completionProposal).getDisplayString();
			if(displayString.contains("temp")) { //$NON-NLS-1$
				isMatches=true;
			}
		}
		assertTrue("String not matches", isMatches); //$NON-NLS-1$
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
//		checkOfCAByStartString(CA_NAME, "JBIDE/788/testCAMessageBundlesAndEL.xhtml","p",1203);  //$NON-NLS-1$//$NON-NLS-2$
		
		//TODO Max Areshkau  Repair when this functionality will be workd
		fail("This functionality doesn't works now"); //$NON-NLS-1$
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
		ICompletionProposal[] results =checkOfCAByStartString(CA_NAME, "JBIDE/788/testCAforHtml.html", "", 42,false);  //$NON-NLS-1$//$NON-NLS-2$

		assertNotNull(results);
		assertTrue("The lenft should be more than 0",results.length>0); //$NON-NLS-1$
		boolean isMatches=true;
		for (ICompletionProposal completionProposal : results) {
			String displayString = ((ICompletionProposal) completionProposal).getDisplayString();
			
			if(!displayString.startsWith("ta")) { //$NON-NLS-1$
				isMatches=false;
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
		ICompletionProposal[] results = checkOfCAByStartString(CA_NAME, "JBIDE/788/testCAforJSP.jsp", "h:outp",1139,false); //$NON-NLS-1$ //$NON-NLS-2$

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
				687);

		// cursor will set after "outputText" tag
		checkOfCAByStartString(CA_NAME, "JBIDE/788/testCAforXHTML.xhtml", "s",  //$NON-NLS-1$//$NON-NLS-2$
				778);

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
	 * @param position
	 * @param numberOfProposals
	 * @throws CoreException
	 */
    private ICompletionProposal[] checkOfCAByStartString(String caName, String testPagePath,
            String partOfString, int position) throws CoreException {
        return this.checkOfCAByStartString(caName, testPagePath, partOfString, position,true);
        
    }
	/**
	 * 
	 * @param caName
	 * @param testPagePath
	 * @param partOfString
	 * @param position
	 * @param isCheck
	 * @return
	 * @throws CoreException
	 */
	
	private ICompletionProposal[] checkOfCAByStartString(String caName, String testPagePath,
            String partOfString, int position,boolean isCheck) throws CoreException {
        // get test page path
        IFile file = (IFile) TestUtil.getComponentPath(testPagePath,
                IMPORT_PROJECT_NAME);
        assertNotNull("Could not open specified file " + file.getFullPath(), //$NON-NLS-1$
                file);

        IEditorInput input = new FileEditorInput(file);

        assertNotNull("Editor input is null", input); //$NON-NLS-1$

        // open and get editor
        JSPMultiPageEditor part = openEditor(input);

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
        ICompletionProposal[] results = iContentAssistProcessor
                .computeCompletionProposals(part.getSourceEditor()
                        .getTextViewer(), newPosition);

        // remove inserted string
        part.getSourceEditor().getTextViewer().getTextWidget()
                .replaceTextRange(position, partOfString.length(), ""); //$NON-NLS-1$

        assertNotNull(results);
        assertTrue("Number of ca proposals shouldn't be a null",results.length>0); //$NON-NLS-1$
        if (isCheck) {
            for (int i = 0; i < results.length; i++) {

                String displayString = ((ICompletionProposal) results[i]).getDisplayString();
                assertNotNull(displayString);
                assertEquals(true, displayString.startsWith(partOfString));
            }
        }

        closeEditors();
        TestUtil.delay(1000L);
        return results;
	}

}

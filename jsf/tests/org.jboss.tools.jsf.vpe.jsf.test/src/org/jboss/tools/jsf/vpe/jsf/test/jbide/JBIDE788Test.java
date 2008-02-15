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
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.jst.jsp.jspeditor.JSPTextEditor;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.jboss.tools.vpe.ui.test.VpeTest;

/**
 * @author Max Areshkau
 *
 *	JUnit test for http://jira.jboss.com/jira/browse/JBIDE-788
 */
public class JBIDE788Test extends VpeTest{
	
	private static final String IMPORT_PROJECT_NAME = "jsfTest";
	
	private static final String CA_NAME = "org.eclipse.wst.html.HTML_DEFAULT";
	 
	public JBIDE788Test(String name) {
		super(name);
	}
	/**
	 * Tests inner nodes include URI
	 * @throws Throwable
	 */
	public void testCAforIncludeTaglibInInenerNodes() throws Throwable {
		// wait
		TestUtil.waitForJobs();
		// set exception
		setException(null);
		//Tests CA

		baseCheckofCA(CA_NAME, "JBIDE/788/TestChangeUriInInnerNodes.xhtml", 395, 185);
		baseCheckofCA(CA_NAME, "JBIDE/788/TestChangeUriInInnerNodes.xhtml", 503, 125);
		baseCheckofCA(CA_NAME, "JBIDE/788/TestChangeUriInInnerNodes.xhtml", 567, 199);
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
		//Tests CA

		baseCheckofCA(CA_NAME, "JBIDE/788/testCAPathProposals.xhtml", 514, 3);
		baseCheckofCA(CA_NAME, "JBIDE/788/testCAPathProposals.xhtml", 586, 4);
		baseCheckofCA(CA_NAME, "JBIDE/788/testCAPathProposals.xhtml", 653, 46);
		baseCheckofCA(CA_NAME, "JBIDE/788/testCAPathProposals.xhtml", 719, 46);
		// check exception
		if (getException() != null) {
		
			throw getException();
		}	
	}	
	/**
	 * Tests CA for Messages Bundles and EL Values
	 * @throws Throwable
	 */
	public void testCAforMessageBundlesAndELExpressions() throws Throwable {
		// wait
		TestUtil.waitForJobs();
		// set exception
		setException(null);
		//Tests CA

		baseCheckofCA(CA_NAME, "JBIDE/788/testCAMessageBundlesAndEL.xhtml", 1245, 2);
		
		// check exception
		if (getException() != null) {
		
			throw getException();
		}
	}
	
	/**
	 * Tests CA for proposals for JSFC
	 * @throws Throwable
	 */
	public void testCAforForJSFCProposals() throws Throwable {
		// wait
		TestUtil.waitForJobs();
		// set exception
		setException(null);
		//Tests CA
		baseCheckofCA(CA_NAME, "JBIDE/788/testCAMessageBundlesAndEL.xhtml", 1200, 101);
		
		// check exception
		if (getException() != null) {
		
			throw getException();
		}
		
	}
	/**
	 * Tests CA on html files
	 * @throws Throwable
	 */
	public void testCAforHtmlFiles() throws Throwable {
		// wait
		TestUtil.waitForJobs();
		// set exception
		setException(null);
		//Tests CA
		baseCheckofCA(CA_NAME, "JBIDE/788/testCAforHtml.html", 39, 79);

		// check exception
		if (getException() != null) {
		
			throw getException();
		}
	}
	/**
	 * Tests CA on jsp files
	 * @throws Throwable
	 */
	public void testCAforJSPFiles() throws Throwable {
		// wait
		TestUtil.waitForJobs();
		// set exception
		setException(null);
		//Tests CA
		baseCheckofCA(CA_NAME, "JBIDE/788/testCAforJSP.jsp", 1000, 110);
		// check exception
		if (getException() != null) {
		
			throw getException();
		}
	}
	/**
	 * Tests CA on jsp files
	 * @throws Throwable
	 */
	public void testCAforXHTMLFiles() throws Throwable {
		// wait
		TestUtil.waitForJobs();
		// set exception
		setException(null);
		//Tests CA
		baseCheckofCA(CA_NAME, "JBIDE/788/testCAforXHTML.xhtml", 745, 96);
		// check exception
		if (getException() != null) {
		
			throw getException();
		}
	}
	/**
	 * Perfoms base test of ca, compare number of proposals which what returned by ca 
	 * with etalon
	 * @param caName - content assistent name
	 * @param testPagePath - test page
	 * @param position - position on test page
	 * @param numberOfProposals - standard number of proposals 
	 * @throws CoreException 
	 */
	private void baseCheckofCA(String caName,String testPagePath, int position, int numberOfProposals) throws CoreException {
		// get test page path
		IFile file = (IFile) TestUtil.getComponentPath(testPagePath,
				IMPORT_PROJECT_NAME);
		assertNotNull("Could not open specified file " + file.getFullPath(),
				file);

		IEditorInput input = new FileEditorInput(file);

		assertNotNull("Editor input is null", input);
		
		// open and get editor
		JSPMultiPageEditor part = openEditor(input);
		
		//sets cursor position
		part.getSourceEditor().getTextViewer().getTextWidget().setCaretOffset(position);
		TestUtil.waitForJobs();
		TestUtil.delay(2000);
		SourceViewerConfiguration  sourceViewerConfiguration = ((JSPTextEditor)part.getSourceEditor()).getSourceViewerConfigurationForTest();

		StructuredTextViewerConfiguration stvc = (StructuredTextViewerConfiguration)  sourceViewerConfiguration;
		IContentAssistant iContentAssistant = stvc.getContentAssistant((ISourceViewer) part.getSourceEditor().getAdapter(ISourceViewer.class));
		assertNotNull(iContentAssistant);
		IContentAssistProcessor iContentAssistProcessor= iContentAssistant.getContentAssistProcessor(caName);
		assertNotNull(iContentAssistProcessor);
		ICompletionProposal[] results = iContentAssistProcessor.computeCompletionProposals(part.getSourceEditor().getTextViewer(), position);
		assertNotNull(results);
		assertEquals(numberOfProposals,results.length);
	}
}

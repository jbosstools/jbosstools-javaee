/*******************************************************************************
 * Copyright (c) 2007-2008 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/


package org.jboss.tools.seam.ui.test.jbide;


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.wst.sse.ui.StructuredTextViewerConfiguration;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditorPart;
import org.jboss.tools.jst.jsp.jspeditor.JSPTextEditor;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.TestProjectProvider;
import org.jboss.tools.test.util.WorkbenchUtils;


/**
 * The Class JBide2227TestCase.
 */
public class JBide2227TestCase extends TestCase {

    /** The Constant CA_NAME. */
    private static final String CA_NAME = "org.eclipse.wst.html.HTML_DEFAULT";

    /** The Constant IMPORT_PROJECT_NAME. */
    public static final String IMPORT_PROJECT_NAME = "TestSeamELContentAssist";

    /** The Constant PAGE_1. */
    private static final String PAGE_1 = "/WebContent/jbide2227/withEl.xhtml";

    /** The Constant PAGE_2. */
    private static final String PAGE_2 = "/WebContent/jbide2227/withoutEl.xhtml";

    protected final static String JSP_EDITOR_ID = "org.jboss.tools.jst.jsp.jspeditor.JSPTextEditor"; //$NON-NLS-1$

    /**
     * Suite.
     * 
     * @return the test
     */
    public static Test suite() {
        return new TestSuite(JBide2227TestCase.class);
    }

    /** The make copy. */
    private boolean makeCopy;

    /** The project. */
    private IProject project;

    /** The provider. */
    private TestProjectProvider provider;

    /**
     * The Constructor.
     */
    public JBide2227TestCase() {
        super("");
    }

    /**
     * The Constructor.
     * 
     * @param name
     *      the name
     */
    public JBide2227TestCase(String name) {
        super(name);
    }

    /**
     * Base checkof CA.
     * 
     * @param testPagePath
     *      the test page path
     * @param position
     *      the position
     * @param caName
     *      the ca name
     * @param numberOfProposals
     *      the number of proposals
     * 
     * @throws CoreException
     *      the core exception
     */
    protected void check(String caName, String testPagePath, int position, int numberOfProposals) throws CoreException {
        // get test page path
        IFile file = project.getFile(testPagePath);
        assertNotNull("Could not open specified file. testPagePath = " + testPagePath, file);

        IEditorInput input = new FileEditorInput(file);

        assertNotNull("Editor input is null", input);

        // open and get editor
        // get editor
		JSPMultiPageEditor part = (JSPMultiPageEditor) PlatformUI
				.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.openEditor(input, JSP_EDITOR_ID, true);


        // sets cursor position
        part.getSourceEditor().getTextViewer().getTextWidget().setCaretOffset(position);
        JobUtils.waitForIdle();
        JobUtils.delay(2000);
        SourceViewerConfiguration sourceViewerConfiguration = ((JSPTextEditor) part.getSourceEditor())
                .getSourceViewerConfigurationForTest();
        // errase errors which can be on start of editor(for example xuklunner
        // not found)

        StructuredTextViewerConfiguration stvc = (StructuredTextViewerConfiguration) sourceViewerConfiguration;
        IContentAssistant iContentAssistant = stvc.getContentAssistant((ISourceViewer) part.getSourceEditor().getAdapter(
                ISourceViewer.class));
        assertNotNull(iContentAssistant);
        IContentAssistProcessor iContentAssistProcessor = iContentAssistant.getContentAssistProcessor(caName);
        assertNotNull(iContentAssistProcessor);
        ICompletionProposal[] results = iContentAssistProcessor
                .computeCompletionProposals(part.getSourceEditor().getTextViewer(), position);
        assertNotNull(results);
        assertEquals(numberOfProposals, results.length);

        WorkbenchUtils.closeAllEditors();
        JobUtils.delay(1000L);
    }

    /**
     * Sets the up.
     * 
     * @throws Exception
     *      the exception
     */
    public void setUp() throws Exception {
        provider = new TestProjectProvider("org.jboss.tools.seam.ui.test", null, IMPORT_PROJECT_NAME, makeCopy);
        project = provider.getProject();
        Throwable exception = null;
        try {
            project.refreshLocal(IResource.DEPTH_INFINITE, null);
        } catch (Exception x) {
            exception = x;
            x.printStackTrace();
        }
        assertNull("An exception caught: " + (exception != null ? exception.getMessage() : ""), exception);
    }

    /**
     * Tear down.
     * 
     * @throws Exception
     *      the exception
     */
    protected void tearDown() throws Exception {
        if (provider != null) {
            provider.dispose();
        }
    }

    /**
     * Test content assist with el.
     * 
     * @throws Throwable
     *      the throwable
     */
    public void testContentAssistWithEl() throws Throwable {
        check(CA_NAME, PAGE_1, 576, 114);
    }

    /**
     * Test content assist without el.
     * 
     * @throws Throwable
     *      the throwable
     */
    public void testContentAssistWithoutEl() throws Throwable {
        check(CA_NAME, PAGE_2, 580, 11);
    }

}
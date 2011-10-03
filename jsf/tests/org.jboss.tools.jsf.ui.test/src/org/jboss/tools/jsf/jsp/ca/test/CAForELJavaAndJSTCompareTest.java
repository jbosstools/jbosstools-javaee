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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.jboss.tools.common.base.test.contentassist.CATestUtil;
import org.jboss.tools.common.el.ui.ca.ELProposalProcessor;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.jst.jsp.contentassist.AutoELContentAssistantProposal;
import org.jboss.tools.jst.jsp.test.ca.ContentAssistantTestCase;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ProjectImportTestSetup;

/**
 * The JUnit test case for JBIDE-9792 issue
 * 
 * @author Victor V. Rubezhny
 *
 */
public class CAForELJavaAndJSTCompareTest extends ContentAssistantTestCase {
	private static final String PROJECT_NAME = "JSF2KickStartWithoutLibs";
	private static final String PAGE_NAME = "WebContent/pages/inputname.xhtml";
	private static final String JAVA_FILENAME = "/demo/User.java";
	private static final String PREFIXES[] = new String[] {"#{us" , "#{user.na"};

	public void setUp() throws Exception {
		project = ProjectImportTestSetup.loadProject(PROJECT_NAME);
		project.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
	}

	public static Test suite() {
		return new TestSuite(CAForELJavaAndJSTCompareTest.class);
	}

	public void testCAForELJavaAndJSTCompare () {
		for (String prefix : PREFIXES) {
			ELProposalProcessor.Proposal javaProposals[] = getJavaEditorProposals(prefix);
			assertFalse ("No EL Proposals found in Java file: " + JAVA_FILENAME, (javaProposals == null || javaProposals.length == 0));
			assertEquals ("Content Assist in returned more than 1 proposal for Java file: " + JAVA_FILENAME + 
					". Test project and/or data should be verfied/updated.", 1,  javaProposals.length);
			
			AutoELContentAssistantProposal jstProposals[] = getJSTProposals(prefix);
			assertFalse ("No EL Proposals found in Web page: " + PAGE_NAME, (jstProposals == null || jstProposals.length == 0));
			assertEquals ("Content Assist in returned more than 1 proposal for Web page: " + PAGE_NAME + 
					". Test project and/or data should be verfied/updated.", 1, jstProposals.length);
			
			compareJavaAndJSTProposals(javaProposals[0], jstProposals[0]);
		}
	}
	
	private ELProposalProcessor.Proposal[] getJavaEditorProposals(String prefix) {
		assertNotNull("Test project \"" + PROJECT_NAME + "\" is not loaded", project);
		 
		IFolder srcRoot = (IFolder)EclipseResourceUtil.getJavaSourceRoot(project);
		IFile javaFile = (srcRoot == null ? null : (IFile)srcRoot.findMember(JAVA_FILENAME));  

		assertNotNull("The file \"" + JAVA_FILENAME + "\" is not found", javaFile);
		assertTrue("The file \"" + JAVA_FILENAME + "\" is not found", (javaFile.exists()));

		FileEditorInput editorInput = new FileEditorInput(javaFile);
		IEditorPart editorPart = null;

		try {
			editorPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(editorInput, "org.eclipse.jdt.ui.CompilationUnitEditor");
		} catch (PartInitException e) {
			fail("Couldn't obtain Java Editor for " + JAVA_FILENAME + ": " + e.getLocalizedMessage());
		}
		
		// clean deffered events 
		while (Display.getCurrent().readAndDispatch());

		try {
			CompilationUnitEditor javaEditor = null;
			
			if (editorPart instanceof CompilationUnitEditor)
				javaEditor = (CompilationUnitEditor)editorPart;
		
			// Delay for 3 seconds so that
			// the Favorites view can be seen.
			JobUtils.waitForIdle();
			
			ISourceViewer viewer = javaEditor.getViewer();
			IDocument document = viewer.getDocument();
			SourceViewerConfiguration config = CATestUtil.getSourceViewerConfiguration(javaEditor);
			IContentAssistant contentAssistant = (config == null ? null : config.getContentAssistant(viewer));
	
			assertNotNull("Cannot get the Content Assistant instance for the editor for file  \"" + JAVA_FILENAME + "\"", contentAssistant);
			
			String documentContent = document.get();
			int start = (documentContent == null ? -1 : documentContent.indexOf(prefix));
			int offsetToTest = start + prefix.length();
			
			assertNotSame("Cannot find the starting point in the test file  \"" + JAVA_FILENAME + "\"", -1, start);
	
			ICompletionProposal[] result= null;
	
			IContentAssistProcessor p= CATestUtil.getProcessor(viewer, offsetToTest, contentAssistant);
			if (p != null) {
				result= p.computeCompletionProposals(viewer, offsetToTest);
			}
			
			assertTrue("Content Assistant peturned no proposals", (result != null && result.length > 0));

			Set<ELProposalProcessor.Proposal> javaProposals = new HashSet<ELProposalProcessor.Proposal>();
			for (int j = 0; j < result.length; j++) {
				if (result[j] instanceof ELProposalProcessor.Proposal) {
					ELProposalProcessor.Proposal proposal = (ELProposalProcessor.Proposal)result[j];
					javaProposals.add(proposal);
				}
			}
			return javaProposals.toArray(new ELProposalProcessor.Proposal[0]);
		} finally {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
			.closeEditor(editorPart, false);
		}
	}
	
	AutoELContentAssistantProposal[] getJSTProposals(String prefix) {
		openEditor(PAGE_NAME);
		try {
			String documentContent = document.get();
			int start = (documentContent == null ? -1 : documentContent.indexOf(prefix));
			assertFalse("Required node '" + prefix + "' not found in document", (start == -1));
			int offsetToTest = start + prefix.length();
			
			JobUtils.waitForIdle();
			
			List<ICompletionProposal> res = CATestUtil.collectProposals(contentAssistant, viewer, offsetToTest);
	
			assertTrue("Content Assistant returned no proposals", (res != null && res.size() > 0));

			Set<AutoELContentAssistantProposal> jstProposals = new HashSet<AutoELContentAssistantProposal>();
			for (ICompletionProposal p : res) {
				if (p instanceof AutoELContentAssistantProposal) {
					jstProposals.add((AutoELContentAssistantProposal)p);
				}
			}
			
			return jstProposals.toArray(new AutoELContentAssistantProposal[0]);
		} finally {
			closeEditor();
		}
	}
	
	private boolean compareJavaAndJSTProposals(ELProposalProcessor.Proposal javaProposal, AutoELContentAssistantProposal jstProposal) {
		assertNotNull ("Display String of Java EL Proposal should not be a null", javaProposal.getDisplayString());
		assertNotNull ("Display String of JST EL Proposal should not be a null", jstProposal.getDisplayString());
		assertEquals("Display Strings must be equal", jstProposal.getDisplayString(), javaProposal.getDisplayString());

		assertNotNull ("Additional Info String of Java EL Proposal should not be a null", javaProposal.getAdditionalProposalInfo());
		assertNotNull ("Additional Info String of JST EL Proposal should not be a null", jstProposal.getAdditionalProposalInfo());
		assertEquals("Additional Info Strings must be equal", jstProposal.getAdditionalProposalInfo(), javaProposal.getAdditionalProposalInfo());
		return true;
	}
}

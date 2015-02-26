/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.batch.ui.itest.ca;

import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.sse.ui.internal.contentassist.CustomCompletionProposal;
import org.jboss.tools.batch.ui.editor.internal.model.JobXMLEditor;
import org.jboss.tools.common.base.test.contentassist.AbstractContentAssistantTestCase;
import org.jboss.tools.test.util.ProjectImportTestSetup;

@SuppressWarnings("restriction")
public class ContentAssistantTestCase extends AbstractContentAssistantTestCase {
	private static final String PROJECT_NAME = "BatchTestProject";

	protected JobXMLEditor jobEditor = null;
	protected StructuredTextEditor jobTextEditor = null;

	@Override
	public void setUp() throws Exception {
		project = ProjectImportTestSetup.loadProject(getProjectName());
	}

	protected String getProjectName() {
		return PROJECT_NAME;
	}

	protected void obtainTextEditor(IEditorPart editorPart) {
		if (editorPart instanceof JobXMLEditor)
			jobEditor = (JobXMLEditor) editorPart;

		assertNotNull("Cannot get the Job XML Text Editor instance for page \"" //$NON-NLS-1$
						+ fileName + "\"", jobEditor);
		
		// clean deffered events 
		while (Display.getCurrent().readAndDispatch());

		textEditor = jobTextEditor = jobEditor.getSourceEditor();
	}

	protected ISourceViewer getTextViewer() {
		return ((StructuredTextEditor)textEditor).getTextViewer();
	}

	protected boolean isRelevantProposal(ICompletionProposal proposal) {
		return proposal instanceof CustomCompletionProposal;
	}

	/**
	 * @return the jspEditor
	 */
	public JobXMLEditor getJobEditor() {
		return jobEditor;
	}

	/**
	 * @param jspEditor the jspEditor to set
	 */
	public void setJobEditor(JobXMLEditor jobEditor) {
		this.jobEditor = jobEditor;
	}

	/**
	 * @return the jspTextEditor
	 */
	public StructuredTextEditor getJobTextEditor() {
		return jobTextEditor;
	}

	/**
	 * @param jspTextEditor the jspTextEditor to set
	 */
	public void setJobTextEditor(StructuredTextEditor jobTextEditor) {
		this.jobTextEditor = jobTextEditor;
	}

	public ICompletionProposal[] checkProposals(String fileName, String substring, int offset, String[] proposals, String[] noproposals) {
		assertNotNull("Test project \"" + getProjectName() + "\" is not loaded", project);
		ICompletionProposal[] result = checkProposals(fileName, substring, offset, proposals, false);
		for (int i = 0; i < noproposals.length; i++) {
        	boolean found = compareProposal(noproposals[i], null, result);
        	assertFalse("Proposal " + noproposals[i] + " should not be found!", found); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return result;
	}

}
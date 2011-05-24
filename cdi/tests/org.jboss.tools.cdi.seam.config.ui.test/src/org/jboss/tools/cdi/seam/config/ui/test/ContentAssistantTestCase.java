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

package org.jboss.tools.cdi.seam.config.ui.test;

import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.sse.ui.internal.contentassist.CustomCompletionProposal;
import org.jboss.tools.common.editor.ObjectMultiPageEditor;
import org.jboss.tools.common.model.ui.editor.EditorPartWrapper;
import org.jboss.tools.common.base.test.contentassist.AbstractContentAssistantTestCase;

public class ContentAssistantTestCase extends AbstractContentAssistantTestCase {

	protected void obtainTextEditor(IEditorPart editorPart) {
		if(editorPart instanceof EditorPartWrapper) {
			editorPart = ((EditorPartWrapper)editorPart).getEditor();
		}
		if (editorPart instanceof ObjectMultiPageEditor)  {
			textEditor = ((ObjectMultiPageEditor) editorPart).getSourceEditor();
		} else if (editorPart instanceof StructuredTextEditor) {
			textEditor = (StructuredTextEditor)editorPart;
		}
		
		// clean deffered events 
		while (Display.getCurrent().readAndDispatch());
	}

	protected boolean isRelevantProposal(ICompletionProposal proposal) {
		return (proposal instanceof CustomCompletionProposal)
				&& (proposal.getClass() != CustomCompletionProposal.class);
	}

}
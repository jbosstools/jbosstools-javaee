/*******************************************************************************
 * Copyright (c) 2007-2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.jsf.jsf2.refactoring.core;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.jboss.tools.common.text.ext.IMultiPageEditor;

/**
 * 
 * @author yzhishko
 *
 */

public class StructuredTextFileChange extends TextFileChange{

	public StructuredTextFileChange(String name, IFile file) {
		super(name, file);
	}
	
	
	@Override
	protected void releaseDocument(final IDocument document, IProgressMonitor pm)
			throws CoreException {
		super.releaseDocument(document, pm);
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				IEditorPart[] editors = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getDirtyEditors();
				for (IEditorPart editorPart : editors) {
					if (editorPart instanceof IMultiPageEditor) {
						StructuredTextEditor editor = ((IMultiPageEditor) editorPart).getSourceEditor();
						IDocument editorDocument = editor.getTextViewer().getDocument();
						if (document == editorDocument) {
							PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().saveEditor(editorPart, false);
							return;
						}
					}
				}
			}
			
		});
	}
	
}

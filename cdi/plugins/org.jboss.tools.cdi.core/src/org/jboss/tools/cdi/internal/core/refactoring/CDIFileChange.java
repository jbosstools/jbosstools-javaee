/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.internal.core.refactoring;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.jboss.tools.cdi.core.CDICorePlugin;

public class CDIFileChange extends TextFileChange{

	public CDIFileChange(String name, IFile file) {
		super(name, file);
	}

	@Override
	protected void releaseDocument(final IDocument document, IProgressMonitor pm)
			throws CoreException {
		super.releaseDocument(document, pm);
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				IEditorPart editor =  getEditor(getFile());
				if(editor != null){
					editor.doSave(new NullProgressMonitor());
				}
			}
			
		});
	}
	
	public static IEditorPart getEditor(IFile file){
		IEditorInput ii = EditorUtility.getEditorInput(file);
		
		IWorkbenchWindow[] windows = CDICorePlugin.getDefault().getWorkbench().getWorkbenchWindows();
		for(IWorkbenchWindow window : windows){
			IEditorPart editor = window.getActivePage().findEditor(ii);
			if(editor != null)
				return editor;
		}
		return null;
	}

}

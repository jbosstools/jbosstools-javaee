/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.ui.el.refactoring;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.jboss.tools.jsf.ui.JsfUIMessages;
import org.jboss.tools.jst.web.kb.refactoring.SearchUtil;
import org.jboss.tools.jst.web.kb.refactoring.SearchUtil.FileResult;
import org.jboss.tools.jst.web.kb.refactoring.SearchUtil.SearchResult;

public class MessagesFileRenameParticipant extends RenameParticipant {
	private static final String PROPERTIES_EXT = "properties";
	private RefactoringStatus status;
	private CompositeChange rootChange;
	private IFile file;

	@Override
	protected boolean initialize(Object element) {
		if(element instanceof IFile){
			rootChange = new CompositeChange(JsfUIMessages.MESSAGES_FILE_RENAME_PARTICIPANT_UPDATE_MESSAGE_BUNDLE_REFERENCES);
			file = (IFile)element;
			String ext = file.getFileExtension();
			if(PROPERTIES_EXT.equals(ext)){
				
				IPath path = file.getFullPath();
				String newName = getArguments().getNewName();
				String oldName = "\"demo.Messages\"";
				
				SearchUtil su = new SearchUtil(SearchUtil.XML_FILES, oldName);
				SearchResult result = su.searchInNodeAttribute(file.getProject(), ":loadBundle", "basename");
				for(FileResult fr : result.getEntries()){
					TextFileChange fileChange = new TextFileChange(fr.getFile().getName(), fr.getFile());
					MultiTextEdit root = new MultiTextEdit();
					fileChange.setEdit(root);
					rootChange.add(fileChange);
					for(int position : fr.getPositions()){
						TextEdit edit = new ReplaceEdit(position, oldName.length(), "\""+newName+"\"");
						fileChange.addEdit(edit);
					}
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public RefactoringStatus checkConditions(IProgressMonitor pm,
			CheckConditionsContext context) throws OperationCanceledException {
		
		return status;
	}

	@Override
	public Change createChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {
		return rootChange;
	}

	@Override
	public String getName() {
		if(file != null)
			return file.getName();
		return null;
	}

}

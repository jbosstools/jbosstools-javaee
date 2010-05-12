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

package org.jboss.tools.jsf.jsf2.refactoring;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;
import org.eclipse.ui.ide.IDE;
import org.jboss.tools.jsf.jsf2.model.JSF2ComponentModelManager;
import org.jboss.tools.jsf.jsf2.util.JSF2ResourceUtil;
import org.jboss.tools.jsf.messages.JSFUIMessages;

/**
 * 
 * @author yzhishko
 * 
 */

public class JSF2RenameParticipant extends RenameParticipant {

	private IProject project;
	private String URI;
	private String oldFileName;

	@Override
	public RefactoringStatus checkConditions(IProgressMonitor pm,
			CheckConditionsContext context) throws OperationCanceledException {
		return new RefactoringStatus();
	}

	@Override
	public Change createChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {
		String newFileName = getArguments().getNewName();
		if (project == null || newFileName == null || oldFileName == null) {
			return null;
		}
		oldFileName = oldFileName.substring(0, oldFileName.lastIndexOf('.'));
		newFileName = newFileName.substring(0, newFileName.lastIndexOf('.'));
		JSF2ComponentModelManager.getManager().renameCompositeComponents(
				project, URI, oldFileName, newFileName);
		project = null;
		URI = null;
		oldFileName = null;
		return null;
	}

	@Override
	public String getName() {
		return JSFUIMessages.Rename_JSF_2_Composite_Components;
	}

	@Override
	protected boolean initialize(Object element) {
		if (element instanceof IFile) {
			IFile file = (IFile) element;
			URI = calcURIFromPath(file.getFullPath());
			if (checkContentType(file)) {
				project = file.getProject();
				oldFileName = file.getName();
				return true;
			}
		}
		return false;
	}

	private boolean checkContentType(IFile file) {
		if (URI == null || URI.equals("")) { //$NON-NLS-1$
			return false;
		}
		if (!"xhtml".equals(file.getFileExtension())) { //$NON-NLS-1$
			return false;
		}
		IContentType contentType = IDE.getContentType(file);
		if (!"org.eclipse.wst.html.core.htmlsource".equals(contentType.getId())) { //$NON-NLS-1$
			return false;
		}
		if ((JSF2ComponentModelManager.getManager()
				.checkCompositeInterface(JSF2ComponentModelManager
						.getReadableDOMDocument(file))) == null) {
			return false;
		}
		return true;
	}

	private String calcURIFromPath(IPath path) {
		StringBuilder uri = new StringBuilder(""); //$NON-NLS-1$
		String[] segments = path.segments();
		if (segments.length > 3) {
			if (segments[2].equals("resources")) { //$NON-NLS-1$
				for (int i = 3; i < segments.length - 1; i++) {
					uri.append("/" + segments[i]); //$NON-NLS-1$
				}
				uri.insert(0, JSF2ResourceUtil.JSF2_URI_PREFIX);
			}
		}
		return uri.toString();
	}

}

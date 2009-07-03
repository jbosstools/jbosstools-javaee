/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.seam.internal.core.validation;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;

/**
 * Helper for Seam Validators that use Seam Validator Context. 
 * @author Alexey Kazakov
 */
public class SeamContextValidationHelper extends SeamValidationHelper {

	protected ISeamValidationContext validationContext;
	protected TextFileDocumentProvider documentProvider = new TextFileDocumentProvider();

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.wst.validation.internal.operations.WorkbenchContext#registerResource(org.eclipse.core.resources.IResource)
	 */
	@Override
	public void registerResource(IResource resource) {
		if(resource instanceof IFile) {
			IFile file = (IFile)resource;
			if(!file.exists()) {
				getValidationContext().addRemovedFile(file);
			} else {
				getValidationContext().registerFile(file);
			}
		}
	}

	/**
	 * @return Set of changed resources
	 */
	public Set<IFile> getChangedFiles() {
		Set<IFile> result = new HashSet<IFile>();
		String[] uris = getURIs();
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		for (int i = 0; i < uris.length; i++) {
			IFile currentFile = root.getFile(new Path(uris[i]));
			result.add(currentFile);
		}
		result.addAll(getValidationContext().getRemovedFiles());
		return result;
	}

	public ISeamValidationContext getValidationContext() {
		if(validationContext==null) {
			validationContext = new SeamValidationContext(getProject());
		}
		return validationContext;
	}

	public void setValidationContext(ISeamValidationContext context) {
		validationContext = context;
	}

	public TextFileDocumentProvider getDocumentProvider(){
		return documentProvider;
	}
}
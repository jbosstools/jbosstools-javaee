 /*******************************************************************************
  * Copyright (c) 2008 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributors:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.seam.ui.refactoring;

import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;

/**
 * @author Alexey Kazakov
 *
 */
public class SeamComponentRenameHandler extends AbstractHandler {

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IEditorPart editor = HandlerUtil.getActiveEditor(event);

		IEditorInput input = editor.getEditorInput();
		if(input instanceof IFile) {
			IFile file = (IFile)input;
			IProject project = file.getProject();
			ISeamProject seamProject = SeamCorePlugin.getSeamProject(project, true);
			if(seamProject!=null) {
				Set<ISeamComponent> components = seamProject.getComponentsByPath(file.getFullPath());
				if(components.size() > 0) {
					ISeamComponent component = components.iterator().next();
					// TODO this is a component which we want to rename.
				}
			}
		}
		return null;
	}
}
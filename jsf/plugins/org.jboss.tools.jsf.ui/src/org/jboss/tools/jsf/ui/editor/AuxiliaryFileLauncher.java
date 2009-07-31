/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.jsf.ui.editor;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.*;
import org.eclipse.ui.part.*;
import org.jboss.tools.common.editor.XMLEditorLauncher;
import org.jboss.tools.jsf.model.FacesConfigLoader;
import org.jboss.tools.jsf.ui.JsfUiPlugin;
import org.jboss.tools.common.model.ui.ModelUIPlugin;

public class AuxiliaryFileLauncher implements IEditorLauncher {

	public void open(IFile file) {
		if(file == null || !file.exists() || file.getFullPath() == null) return;
		String s = file.getFullPath().lastSegment();
		if(!s.endsWith("." + FacesConfigLoader.AUXILIARY_FILE_EXTENSION)) return; //$NON-NLS-1$
		//starts with dot
		s = s.substring(1, s.length() - FacesConfigLoader.AUXILIARY_FILE_EXTENSION.length() - 1);
		IResource r = file.getParent().findMember(s);
		if(!(r instanceof IFile) || !r.exists()) return;
		IWorkbenchPage workbenchPage = ModelUIPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if(workbenchPage != null) try {
			workbenchPage.openEditor(new FileEditorInput((IFile)r), "org.jboss.tools.common.model.ui.editor.EditorPartWrapper");			 //$NON-NLS-1$
		} catch (PartInitException e) {
			JsfUiPlugin.getPluginLog().logError(e);
		}
	}

		
	public void open(IPath path) {
		open(XMLEditorLauncher.convert(path));
	}

}

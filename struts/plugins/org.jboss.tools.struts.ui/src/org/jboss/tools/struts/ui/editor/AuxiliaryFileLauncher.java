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
package org.jboss.tools.struts.ui.editor;

import java.io.File;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.*;
import org.eclipse.ui.editors.text.ILocationProvider;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.part.*;
import org.jboss.tools.common.editor.XMLEditorLauncher;
import org.eclipse.jface.resource.ImageDescriptor;
import org.jboss.tools.common.model.ui.ModelUIPlugin;
import org.jboss.tools.struts.model.StrutsConfigLoader;
import org.jboss.tools.struts.ui.StrutsUIPlugin;

public class AuxiliaryFileLauncher implements IEditorLauncher {
	static String LAYOUT_FILE_EXTENSION = StrutsConfigLoader.LAYOUT_FILE_EXTENSION;

	public void open(IFile file) {
		String s = file.getLocation().lastSegment();
		if(!s.endsWith("." + LAYOUT_FILE_EXTENSION)) return;
		// starts with dot
		s = s.substring(1, s.length() - LAYOUT_FILE_EXTENSION.length() - 1); // + "xml";
		IResource r = file.getParent().findMember(s);
		if(!(r instanceof IFile) || !r.exists()) return;
		open(new FileEditorInput((IFile)r));
	}

	public void open(File file) {
		String s = file.getAbsolutePath();
		if(s.endsWith("." + LAYOUT_FILE_EXTENSION)) {
			// starts with dot
			s = s.substring(1, s.length() - LAYOUT_FILE_EXTENSION.length() - 1); // + "xml";
			File f = new File(s);
			if(!f.isFile()) return;
			open(new ExternalFileEditorInput(f));
		}
	}

	private void open(IEditorInput input) {
		IWorkbenchPage workbenchPage = ModelUIPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
		try {
			workbenchPage.openEditor(input, "org.jboss.tools.common.model.ui.editor.EditorPartWrapper");			
		} catch (Exception e) {
			StrutsUIPlugin.getPluginLog().logError(e);
		}
	}

		
	public void open(IPath path) {
		if(path == null) return;
		IFile f = XMLEditorLauncher.convert(path);
		if(f != null && f.exists()) {
			open(f);
		} else if(path.toFile().isFile()) {
			open(path.toFile());
		}
	}
}

class ExternalFileEditorInput implements IEditorInput, ILocationProvider {
	private class WorkbenchAdapter implements IWorkbenchAdapter {
		public Object[] getChildren(Object o) {
			return null;
		}
		public ImageDescriptor getImageDescriptor(Object object) {
			return null;
		}
		public String getLabel(Object o) {
			return ((ExternalFileEditorInput) o).getName();
		}
		public Object getParent(Object o) {
			return null;
		}
	}

	private File fFile;
	private WorkbenchAdapter fWorkbenchAdapter = new WorkbenchAdapter();

	public ExternalFileEditorInput(File file) {
		super();
		fFile = file;
		fWorkbenchAdapter = new WorkbenchAdapter();
	}
	public boolean exists() {
		return fFile.exists();
	}
	public ImageDescriptor getImageDescriptor() {
		return null;
	}
	public String getName() {
		return fFile.getName();
	}
	public IPersistableElement getPersistable() {
		return null;
	}
	public String getToolTipText() {
		return fFile.getAbsolutePath();
	}
	public Object getAdapter(Class adapter) {
		if (ILocationProvider.class.equals(adapter))
			return this;
		if (IWorkbenchAdapter.class.equals(adapter))
			return fWorkbenchAdapter;
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}
	public IPath getPath(Object element) {
		if (element instanceof ExternalFileEditorInput) {
			ExternalFileEditorInput input = (ExternalFileEditorInput) element;
			return Path.fromOSString(input.fFile.getAbsolutePath());
		}
		return null;
	}
    public IPath getPath() {
        return Path.fromOSString(fFile.getAbsolutePath());
    }
	public boolean equals(Object o) {
		if (o == this)
			return true;

		if (o instanceof ExternalFileEditorInput) {
			ExternalFileEditorInput input = (ExternalFileEditorInput) o;
			return fFile.equals(input.fFile);
		}
		
        if (o instanceof IPathEditorInput) {
            IPathEditorInput input= (IPathEditorInput)o;
            return getPath().equals(input.getPath());
        }

		return false;
	}
	public int hashCode() {
		return fFile.hashCode();
	}
}


/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.seam.ui.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.jface.viewers.Viewer;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;

/**
 * This content provider is designed for stand-alone Seam Components view
 * to build all the tree starting from the workspace root.
 * 
 * @author Viacheslav Kabanovich
 */
public class RootContentProvider extends AbstractSeamContentProvider {
	IWorkspaceRoot root;
	
	public RootContentProvider() {}
	
	/**
	 * Returns child nodes for the tree of stand alone Seam Components view.
	 * On the first level, array of ISeamProject objects for projects that are
	 * seam projects is returned.
	 */
	@Override
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof IWorkspaceRoot) {
			IWorkspaceRoot root = (IWorkspaceRoot)parentElement;
			IProject[] ps = root.getProjects();
			List<IProject> children = new ArrayList<IProject>();
			for (int i = 0; i < ps.length; i++) {
				if(!isGoodProject(ps[i])) continue;
				ISeamProject p = SeamCorePlugin.getSeamProject(ps[i], false);
				if(p != null) {
					if(!processed.contains(p)) {
						processed.add(p);
						p.addSeamProjectListener(this);
					}
					children.add(p.getProject());
				}
			}
			return children.toArray(new IProject[0]);
		} else if(parentElement instanceof IProject) {
			ISeamProject p = SeamCorePlugin.getSeamProject((IProject)parentElement, false);
			if(p != null) {
				return super.getChildren(p);
			} else {
				return new Object[0];
			}
		} else {
			return super.getChildren(parentElement);
		}
	}

	@Override
	public Object getParent(Object element) {
		if(element instanceof ISeamProject || element instanceof IProject) {
			return root;
		} else {
			Object o = super.getParent(element);
			if(o instanceof ISeamProject) {
				return ((ISeamProject)o).getProject();
			}
			return o;
		}
	}

	protected Object getTreeObject(Object source) {
		if(source instanceof ISeamProject) {
			return ((ISeamProject)source).getProject();
		}
		return source;
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		super.inputChanged(viewer, oldInput, newInput);
		if(newInput instanceof IWorkspaceRoot || newInput == null) {
			root = (IWorkspaceRoot)newInput;
		}
	}
	
	@Override
	public void dispose() {
		super.dispose();
		root = null;
	}

	boolean isGoodProject(IProject project) {
		if(project == null || !project.exists() || !project.isOpen()) return false;
		return true;
	}

}

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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;
import org.jboss.tools.seam.core.IRole;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamComponentDeclaration;
import org.jboss.tools.seam.core.ISeamElement;
import org.jboss.tools.seam.core.ISeamJavaComponentDeclaration;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.ISeamScope;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.event.ISeamProjectChangeListener;
import org.jboss.tools.seam.core.event.SeamProjectChangeEvent;

/**
 * Basic type for content providers that add seam components 
 * tree structure to views based on 
 * org.eclipse.ui.navigator.CommonNavigator
 * Sub-classes need to override methods getChildren(Object) 
 * and getParent(Object) to specify starting points of the
 * seam components sub-trees.
 * Content provider for stand-alone view may start from 
 * the workspace root, while content provider contributing
 * to the standard Project Explorer is better to append 
 * project-specific seam components to that project's node.
 * 
 * @author Viacheslav Kabanovich
 */
public abstract class AbstractSeamContentProvider implements ITreeContentProvider, ISeamProjectChangeListener {
	protected Viewer viewer;
	IResourceChangeListener listener = new ResourceChangeListener();
	Set<ISeamProject> processed = new HashSet<ISeamProject>();
	
	public AbstractSeamContentProvider() {}

	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	public boolean hasChildren(Object element) {
		if(element instanceof ISeamComponentDeclaration) return false;
		if(element instanceof IRole) return false;
		return true;
	}

	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof ISeamProject) {
			return ((ISeamProject)parentElement).getScopes();
		} else if(parentElement instanceof ISeamScope) {
			((ISeamScope)parentElement).getSeamProject().resolve();
			return ((ISeamScope)parentElement).getComponents().toArray(new Object[0]);
		} else if(parentElement instanceof ISeamComponent) {
			List<Object> children = new ArrayList<Object>();
			Set<ISeamComponentDeclaration> ds = ((ISeamComponent)parentElement).getAllDeclarations();
			children.addAll(ds);
			for (ISeamComponentDeclaration d : ds) {
				if(d instanceof ISeamJavaComponentDeclaration) {
					Set<IRole> rs = ((ISeamJavaComponentDeclaration)d).getRoles();
					children.addAll(rs);
				}
			}
			return children.toArray(new Object[0]);
		}
		return null;
	}

	public Object getParent(Object element) {
		if(element instanceof IRole) {
			ISeamElement p = ((IRole)element).getParent();
			return p == null ? p : p.getParent();
		} else if(element instanceof ISeamElement) {
			return ((ISeamElement)element).getParent();
		}
		return null;
	}

	public void projectChanged(SeamProjectChangeEvent event) {
		if(viewer == null || viewer.getControl() == null || viewer.getControl().isDisposed()) return;
		Object o = event.getSource();
		if(o instanceof ISeamElement) {
			refresh(o);
		} else {
			System.out.println("event without source");
		}
	}

	void refresh(final Object o) {
		if(viewer == null || viewer.getControl() == null || viewer.getControl().isDisposed()) return;
		if(!(viewer instanceof StructuredViewer)) return;
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if(o == null) {
					((StructuredViewer)viewer).refresh();
				} else {
					((StructuredViewer)viewer).refresh(o);
				}
			}
		});
		
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = viewer;
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(listener);
		ResourcesPlugin.getWorkspace().addResourceChangeListener(listener);
	}

	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(listener);
		viewer = null;
		if(processed != null) {
			for (ISeamProject p : processed) p.removeSeamProjectListener(this);
			processed.clear();
		}
	}

	class ResourceChangeListener implements IResourceChangeListener {
		ResourceDeltaVisitor visitor = new ResourceDeltaVisitor();

		public void resourceChanged(IResourceChangeEvent event) {
			try {
				if(event.getDelta()==null) refresh(null);
				else event.getDelta().accept(visitor);
			} catch (Exception e) {
				SeamCorePlugin.getPluginLog().logError(e);
			}			
		}
		
	}
	
	class ResourceDeltaVisitor implements IResourceDeltaVisitor {

		public boolean visit(IResourceDelta delta) throws CoreException {
			int kind = delta.getKind();
			IResource r = delta.getResource();
			if(kind == IResourceDelta.ADDED || kind == IResourceDelta.REMOVED) {
				if(r instanceof IProject) {
					refresh(null);
				}
			} else if(kind == IResourceDelta.CHANGED) {
				IResourceDelta[] cs = delta.getAffectedChildren();
				if(cs != null) for (int i = 0; i < cs.length; i++) {
					IResource c = cs[i].getResource();
					if(c instanceof IFile && c.getName().endsWith(".project")) {
						refresh(null);
					}
				}
			}
			if(r instanceof IProject) return false;
			return true;
		}
		
	}

}

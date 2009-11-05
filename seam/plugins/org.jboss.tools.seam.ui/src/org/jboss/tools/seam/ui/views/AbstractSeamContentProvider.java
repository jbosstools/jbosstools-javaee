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
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;
import org.eclipse.ui.navigator.ICommonContentProvider;
import org.eclipse.ui.navigator.IExtensionStateModel;
import org.jboss.tools.seam.core.IRole;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamComponentDeclaration;
import org.jboss.tools.seam.core.ISeamElement;
import org.jboss.tools.seam.core.ISeamJavaComponentDeclaration;
import org.jboss.tools.seam.core.ISeamPackage;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.ISeamScope;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.event.ISeamProjectChangeListener;
import org.jboss.tools.seam.core.event.SeamProjectChangeEvent;
import org.jboss.tools.seam.ui.SeamGuiPlugin;
import org.jboss.tools.seam.ui.SeamUIMessages;

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
public abstract class AbstractSeamContentProvider implements ITreeContentProvider, ISeamProjectChangeListener, ICommonContentProvider {
	protected Viewer viewer;
	IResourceChangeListener listener = new ResourceChangeListener();
	Set<ISeamProject> processed = new HashSet<ISeamProject>();
	
	private IExtensionStateModel fStateModel;
	IPropertyChangeListener scopePropertyListener;
	IPropertyChangeListener layoutPropertyListener;
	boolean isFlatLayout = true;
	boolean isScopeLable = false;
	
	public AbstractSeamContentProvider() {}
	
	public void init(ICommonContentExtensionSite commonContentExtensionSite) {
		fStateModel = commonContentExtensionSite.getExtensionStateModel();
		IMemento memento = commonContentExtensionSite.getMemento();
		restoreState(memento);

		scopePropertyListener = new IPropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if (ViewConstants.SCOPE_PRESENTATION.equals(event.getProperty())) {
					if (event.getNewValue() != null) {
						boolean newValue = ((Boolean) event.getNewValue()).booleanValue();
						setIsScopeLable(newValue);
					}
				}
			}
		};
		fStateModel.addPropertyChangeListener(scopePropertyListener);

		layoutPropertyListener = new IPropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if (ViewConstants.PACKAGE_STRUCTURE.equals(event.getProperty())) {
					if (event.getNewValue() != null) {
						boolean newValue = ((Boolean)event.getNewValue()).booleanValue();
						setIsFlatLayout(newValue);
					}
				}
			}
		};
		fStateModel.addPropertyChangeListener(layoutPropertyListener);
		
	}

	void setIsFlatLayout(boolean b) {
		isFlatLayout = b;
	}
	
	void setIsScopeLable(boolean b) {
		isScopeLable = b;
	}

	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}
	
	boolean isNotShowingScopeNodes() {
		return isScopeLable; // ScopePresentationActionProvider.isScopePresentedAsLabel();
//		if(viewer == null) return false;
//		Boolean b = (Boolean)viewer.getData("scopeAsNode");
//		return b != null && b.booleanValue();
	}
	
	boolean isPackageStructureFlat() {
		return isFlatLayout; //ScopePresentationActionProvider.isPackageStructureFlat();
	}

	public boolean hasChildren(Object element) {
		if(element instanceof ISeamComponentDeclaration) return false;
		if(element instanceof IRole) return false;
		return true;
	}

	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof ISeamProject) {
			ISeamProject project = (ISeamProject)parentElement;
			if(isNotShowingScopeNodes()) {
				project.resolve();

				if(isPackageStructureFlat()) {
					return project.getAllPackages().toArray(new Object[0]);
				} else {
					return project.getPackages().toArray(new Object[0]);
				}
			}
			return project.getScopes();
		} else if(parentElement instanceof ISeamScope) {
			((ISeamScope)parentElement).getSeamProject().resolve();
			
			if(isPackageStructureFlat()) {
				return ((ISeamScope)parentElement).getAllPackages().toArray(new Object[0]);
			} else {
				return ((ISeamScope)parentElement).getPackages().toArray(new Object[0]);
			}
//			return ((ISeamScope)parentElement).getComponents().toArray(new Object[0]);
		} else if(parentElement instanceof ISeamPackage) {
			ISeamPackage p = (ISeamPackage)parentElement;
			List<Object> children = new ArrayList<Object>();
			for (ISeamComponent c : p.getComponents()) {
				children.add(c);
			}
			if(!isPackageStructureFlat()) {
				for (ISeamPackage pc : p.getPackages().values()) {
					children.add(pc);
				}
			}
			return children.toArray(new Object[0]);
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
		return new Object[0];
	}

	public Object getParent(Object element) {
		if(element instanceof IRole) {
			ISeamElement p = ((IRole)element).getParent();
			return p == null ? p : p.getParent();
		} else if(element instanceof ISeamElement) {
			if(element instanceof ISeamComponent) {
				ISeamComponent c = (ISeamComponent)element;
				if(isNotShowingScopeNodes()) {
					return c.getSeamProject().getPackage(c);
				} else {
					ISeamScope s = (ISeamScope)c.getParent();
					ISeamPackage p = s.getPackage(c);
					return p == null ? s : p;
					
				}
			}
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
			SeamGuiPlugin.getPluginLog().logError(SeamUIMessages.ABSTRACT_SEAM_CONTENT_PROVIDER_SEAM_PROJECT_CHANGE_EVENT_OCCURS_BUT_NO_SORCE_OF_PROJECT_PROVIDED);
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
					((StructuredViewer)viewer).refresh(getTreeObject(o));
				}
			}
		});
	}

	protected Object getTreeObject(Object source) {
		return source;
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = viewer;
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(listener);
		ResourcesPlugin.getWorkspace().addResourceChangeListener(listener);
	}

	class ResourceChangeListener implements IResourceChangeListener {
		ResourceDeltaVisitor visitor = new ResourceDeltaVisitor();

		public void resourceChanged(IResourceChangeEvent event) {
			try {
				if (event.getDelta() == null) {
					refresh(null);
				} else {
					event.getDelta().accept(visitor);
				}
			} catch (CoreException e) {
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
					if(c instanceof IFile && c.getName().endsWith(".project")) { //$NON-NLS-1$
						refresh(null);
					}
				}
			}
			if(r instanceof IProject) return false;
			return true;
		}
		
	}

	public void restoreState(IMemento memento) {
	}

	public void saveState(IMemento memento) {
	}

	public void dispose() { 
		fStateModel.removePropertyChangeListener(layoutPropertyListener);
		fStateModel.removePropertyChangeListener(scopePropertyListener);
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(listener);
		viewer = null;
		if(processed != null) {
			for (ISeamProject p : processed) {
				p.removeSeamProjectListener(this);
			}
			processed.clear();
		}
		fStateModel.removePropertyChangeListener(layoutPropertyListener);
		fStateModel.removePropertyChangeListener(scopePropertyListener);
	}

}

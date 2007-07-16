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
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamComponentDeclaration;
import org.jboss.tools.seam.core.ISeamElement;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.ISeamScope;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.event.ISeamProjectChangeListener;
import org.jboss.tools.seam.core.event.SeamProjectChangeEvent;

public class RootContentProvider implements ITreeContentProvider, ISeamProjectChangeListener {
	protected Viewer viewer;
	IWorkspaceRoot root;
	
	Set<ISeamProject> processed = new HashSet<ISeamProject>();
	
	IResourceChangeListener listener = new ResourceChangeListener();
	
	
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof IWorkspaceRoot) {
			IWorkspaceRoot root = (IWorkspaceRoot)parentElement;
			IProject[] ps = root.getProjects();
			List<ISeamProject> children = new ArrayList<ISeamProject>();
			for (int i = 0; i < ps.length; i++) {
				if(!isGoodProject(ps[i])) continue;
				ISeamProject p = SeamCorePlugin.getSeamProject(ps[i], false);
				if(p != null) {
					if(!processed.contains(p)) {
						processed.add(p);
						p.addSeamProjectListener(this);
					}
					children.add(p);
				}
			}
			return children.toArray(new ISeamProject[0]);
		} else if(parentElement instanceof ISeamProject) {
			return ((ISeamProject)parentElement).getScopes();
		} else if(parentElement instanceof ISeamScope) {
			((ISeamScope)parentElement).getSeamProject().resolve();
			return ((ISeamScope)parentElement).getComponents().toArray(new Object[0]);
		} else if(parentElement instanceof ISeamComponent) {
			return ((ISeamComponent)parentElement).getAllDeclarations().toArray(new Object[0]);
		}
		return null;
	}

	public Object getParent(Object element) {
		if(element instanceof ISeamProject) {
			return root;
		} else if(element instanceof ISeamElement) {
			return ((ISeamElement)element).getParent();
		}
		return null;
	}

	public boolean hasChildren(Object element) {
		if(element instanceof ISeamComponentDeclaration) return false;
		return true;
	}

	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	public void dispose() {
		if(root != null) {
			ResourcesPlugin.getWorkspace().removeResourceChangeListener(listener);
		}
		root = null;
		viewer = null;
		if(processed != null) {
			for (ISeamProject p : processed) p.removeSeamProjectListener(this);
			processed.clear();
		}
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = viewer;
		if(newInput instanceof IWorkspaceRoot || newInput == null) {
			root = (IWorkspaceRoot)newInput;
			ResourcesPlugin.getWorkspace().addResourceChangeListener(listener);
		}
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

	boolean isGoodProject(IProject project) {
	if(project == null || !project.exists() || !project.isOpen()) return false;
//		try {
//			if(!project.hasNature("org.jboss.tools.jsf.jsfnature")) return false;
//		} catch (CoreException e) {
//			//ignore - all checks are done above
//			return false;
//		}
		return true;
	}

	class ResourceChangeListener implements IResourceChangeListener {
		ResourceDeltaVisitor visitor = new ResourceDeltaVisitor();

		public void resourceChanged(IResourceChangeEvent event) {
			try {
				event.getDelta().accept(visitor);
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

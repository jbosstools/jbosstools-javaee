package org.jboss.tools.seam.ui.views;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;
import org.jboss.tools.seam.core.ISeamComponent;
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
	
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof IWorkspaceRoot) {
			IWorkspaceRoot root = (IWorkspaceRoot)parentElement;
			IProject[] ps = root.getProjects();
			List<ISeamProject> children = new ArrayList<ISeamProject>();
			for (int i = 0; i < ps.length; i++) {
				ISeamProject p = SeamCorePlugin.getSeamProject(ps[i]);
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
		return true;
	}

	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	public void dispose() {
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
				((StructuredViewer)viewer).refresh(o);
			}
		});
		
	}

}

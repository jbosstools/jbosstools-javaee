/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.seam.ui.search;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.search.internal.ui.text.IFileSearchContentProvider;
import org.eclipse.search.ui.text.AbstractTextSearchResult;
import org.jboss.tools.seam.core.IRole;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamElement;
import org.jboss.tools.seam.core.ISeamProject;


/**
 * Seam tree content provider used in seam search page
 *  
 * @author Jeremy
 *
 */
public class SeamTreeContentProvider implements ITreeContentProvider, IFileSearchContentProvider {

	private final Object[] EMPTY_ARR= new Object[0];

	private AbstractTextSearchResult fResult;
	private SeamSearchResultPage fPage;
	private AbstractTreeViewer fTreeViewer;
	private Map fChildrenMap;
	
	/**
	 * Constructs SeamTreeContentProvider object
	 * 
	 * @param page
	 */
	SeamTreeContentProvider(SeamSearchResultPage page, AbstractTreeViewer viewer) {
		fPage= page;
		fTreeViewer= viewer;
	}
	
	/**
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		Object[] children= getChildren(inputElement);
		int elementLimit= getElementLimit();
		if (elementLimit != -1 && elementLimit < children.length) {
			Object[] limitedChildren= new Object[elementLimit];
			System.arraycopy(children, 0, limitedChildren, 0, elementLimit);
			return limitedChildren;
		}
		return children;
	}
	
	private int getElementLimit() {
		return fPage.getElementLimit().intValue();
	}

	/**
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
		// nothing to do
	}
	
	/**
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput instanceof AbstractTextSearchResult) {
			initialize((AbstractTextSearchResult) newInput);
		}
	}
	
	private synchronized void initialize(AbstractTextSearchResult result) {
		fResult= result;
		fChildrenMap= new HashMap();
		if (result != null) {
			Object[] elements= result.getElements();
			for (int i= 0; i < elements.length; i++) {
				insert(elements[i],  false);
			}
		}
	}

	private void insert(Object child, boolean refreshViewer) {

		Object parent= getParent(child);
		while (parent != null) {
			if (insertChild(parent, child)) {
				if (refreshViewer)
					fTreeViewer.add(parent, child);
			} else {
				if (refreshViewer)
					fTreeViewer.refresh(parent);
				return;
			}
			child= parent;
			parent= getParent(child);
		}
		if (insertChild(fResult, child)) {
			if (refreshViewer)
				fTreeViewer.add(fResult, child);
		}
	}

	/**
	 * returns true if the child already was a child of parent.
	 * 
	 * @param parent
	 * @param child
	 * @return Returns <code>trye</code> if the child was added
	 */
	private boolean insertChild(Object parent, Object child) {
		Set children= (Set) fChildrenMap.get(parent);
		if (children == null) {
			children= new HashSet();
			fChildrenMap.put(parent, children);
		}
		return children.add(child);
	}

	private void remove(Object element, boolean refreshViewer) {
		// precondition here:  fResult.getMatchCount(child) <= 0
	
		if (hasChildren(element)) {
			if (refreshViewer)
				fTreeViewer.refresh(element);
		} else {
			if (fResult.getMatchCount(element) == 0) {
				fChildrenMap.remove(element);
				Object parent= getParent(element);
				if (parent != null) {
					removeFromSiblings(element, parent);
					remove(parent, refreshViewer);
				} else {
					removeFromSiblings(element, fResult);
					if (refreshViewer)
						fTreeViewer.refresh();
				}
			} else {
				if (refreshViewer) {
					fTreeViewer.refresh(element);
				}
			}
		}
	}

	private void removeFromSiblings(Object element, Object parent) {
		Set siblings= (Set) fChildrenMap.get(parent);
		if (siblings != null) {
			siblings.remove(element);
		}
	}

	/**
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement) {
		Set children= (Set) fChildrenMap.get(parentElement);
		if (children == null)
			return EMPTY_ARR;
		return children.toArray();
	}

	/**
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}

	/**
	 * (non-Javadoc)
	 * @see org.eclipse.search.internal.ui.text.IFileSearchContentProvider#elementsChanged(java.lang.Object[])
	 */
	public synchronized void elementsChanged(Object[] updatedElements) {
		for (int i= 0; i < updatedElements.length; i++) {
			if (fResult.getMatchCount(updatedElements[i]) > 0)
				insert(updatedElements[i], true);
			else
				remove(updatedElements[i], true);
		}
	}

	/**
	 * (non-Javadoc)
	 * @see org.eclipse.search.internal.ui.text.IFileSearchContentProvider#clear()
	 */
	public void clear() {
		initialize(fResult);
		fTreeViewer.refresh();
	}

	/**
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element) {
		if (element instanceof IProject || element instanceof IJavaProject || element instanceof ISeamProject)
			return null;
		if (element instanceof IResource) {
			IResource resource = (IResource) element;
			return resource.getParent();
		}
		if(element instanceof IRole) {
			ISeamElement p = ((IRole)element).getParent();
			return p == null ? p : p.getParent();
		}
		if(element instanceof ISeamElement) {
			if(element instanceof ISeamComponent) {
				ISeamComponent c = (ISeamComponent)element;
				return c.getSeamProject().getPackage(c);
			}
			return ((ISeamElement)element).getParent();
		}
		if (element instanceof IJavaElement) {
			return ((IJavaElement)element).getParent();
		}

		return null;
	}
}

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

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.search.internal.ui.text.IFileSearchContentProvider;
import org.eclipse.search.ui.text.AbstractTextSearchResult;

/**
 * Seam table content provider used in seam search page
 *  
 * @author Jeremy
 *
 */
public class SeamTableContentProvider implements IStructuredContentProvider, IFileSearchContentProvider {
	
	private final Object[] EMPTY_ARR= new Object[0];
	
	private SeamSearchResultPage fPage;
	private AbstractTextSearchResult fResult;

	/**
	 * Constructs SeamTableContentProvider object
	 * 
	 * @param page
	 */
	public SeamTableContentProvider(SeamSearchResultPage page) {
		fPage= page;
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
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof SeamSearchResult) {
			int elementLimit= getElementLimit();
			Object[] elements= ((SeamSearchResult)inputElement).getElements();
			if (elementLimit != -1 && elements.length > elementLimit) {
				Object[] shownElements= new Object[elementLimit];
				System.arraycopy(elements, 0, shownElements, 0, elementLimit);
				return shownElements;
			}
			return elements;
		}
		return EMPTY_ARR;
	}
	
	/**
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput instanceof SeamSearchResult) {
			fResult= (SeamSearchResult) newInput;
		}
	}
	
	/**
	 * (non-Javadoc)
	 * @see org.eclipse.search.internal.ui.text.IFileSearchContentProvider#elementsChanged(java.lang.Object[])
	 */
	public void elementsChanged(Object[] updatedElements) {
		TableViewer viewer= getViewer();
		int elementLimit= getElementLimit();
		boolean tableLimited= elementLimit != -1;
		for (int i= 0; i < updatedElements.length; i++) {
			if (fResult.getMatchCount(updatedElements[i]) > 0) {
				if (viewer.testFindItem(updatedElements[i]) != null)
					viewer.update(updatedElements[i], null);
				else {
					if (!tableLimited || viewer.getTable().getItemCount() < elementLimit)
						viewer.add(updatedElements[i]);
				}
			} else
				viewer.remove(updatedElements[i]);
		}
	}

	private int getElementLimit() {
		return fPage.getElementLimit().intValue();
	}

	private TableViewer getViewer() {
		return (TableViewer) fPage.getViewer();
	}

	/**
	 * (non-Javadoc)
	 * @see org.eclipse.search.internal.ui.text.IFileSearchContentProvider#clear()
	 */
	public void clear() {
		getViewer().refresh();
	}
}

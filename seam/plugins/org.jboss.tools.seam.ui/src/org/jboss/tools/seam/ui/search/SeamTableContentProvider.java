package org.jboss.tools.seam.ui.search;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.search.internal.ui.text.IFileSearchContentProvider;
import org.eclipse.search.ui.text.AbstractTextSearchResult;

public class SeamTableContentProvider implements IStructuredContentProvider, IFileSearchContentProvider {
	
	private final Object[] EMPTY_ARR= new Object[0];
	
	private SeamSearchResultPage fPage;
	private AbstractTextSearchResult fResult;

	public SeamTableContentProvider(SeamSearchResultPage page) {
		fPage= page;
	}
	
	public void dispose() {
		// nothing to do
	}
	
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
	
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput instanceof SeamSearchResult) {
			fResult= (SeamSearchResult) newInput;
		}
	}
	
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
	
	public void clear() {
		getViewer().refresh();
	}
}

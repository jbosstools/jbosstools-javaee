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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.util.TransferDragSourceListener;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.search.internal.ui.Messages;
import org.eclipse.search.internal.ui.SearchMessages;
import org.eclipse.search.internal.ui.text.EditorOpener;
import org.eclipse.search.internal.ui.text.FileLabelProvider;
import org.eclipse.search.internal.ui.text.IFileSearchContentProvider;
import org.eclipse.search.internal.ui.text.NewTextSearchActionGroup;
import org.eclipse.search.ui.IContextMenuConstants;
import org.eclipse.search.ui.ISearchResultViewPart;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.search.ui.text.AbstractTextSearchResult;
import org.eclipse.search.ui.text.AbstractTextSearchViewPage;
import org.eclipse.search.ui.text.Match;
import org.eclipse.search2.internal.ui.OpenSearchPreferencesAction;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.IShowInTargetList;
import org.eclipse.ui.part.ResourceTransfer;
import org.eclipse.ui.texteditor.ITextEditor;
import org.jboss.tools.common.java.IJavaSourceReference;
import org.jboss.tools.seam.core.IOpenableElement;

/**
 * Seam Search Result page
 * 
 * @author Jeremy
 *
 */
public class SeamSearchResultPage extends AbstractTextSearchViewPage implements IAdaptable {
	
	static class DecoratorIgnoringViewerSorter extends ViewerComparator {
		private final ILabelProvider fLabelProvider;
		
		public DecoratorIgnoringViewerSorter(ILabelProvider labelProvider) {
			fLabelProvider= labelProvider;
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ViewerComparator#category(java.lang.Object)
		 */
		public int category(Object element) {
			if (element instanceof IContainer) {
				return 1;
			}
			return 2;
		}
		
	    public int compare(Viewer viewer, Object e1, Object e2) {
	        int cat1 = category(e1);
	        int cat2 = category(e2);

	        if (cat1 != cat2) {
				return cat1 - cat2;
			}
	    	
	        String name1= fLabelProvider.getText(e1);
	        String name2= fLabelProvider.getText(e2);
	        if (name1 == null)
	            name1 = "";//$NON-NLS-1$
	        if (name2 == null)
	            name2 = "";//$NON-NLS-1$
	        return getComparator().compare(name1, name2);
	    }
	}
	
	private static final String KEY_SORTING= "org.eclipse.search.resultpage.sorting"; //$NON-NLS-1$
	private static final String KEY_LIMIT= "org.eclipse.search.resultpage.limit"; //$NON-NLS-1$
	private static final int DEFAULT_ELEMENT_LIMIT = 1000;

	private ActionGroup fActionGroup;
	private IFileSearchContentProvider fContentProvider;
	private int fCurrentSortOrder;
	private EditorOpener fEditorOpener= new EditorOpener();

		
	private static final String[] SHOW_IN_TARGETS= new String[] { IPageLayout.ID_RES_NAV };
	private  static final IShowInTargetList SHOW_IN_TARGET_LIST= new IShowInTargetList() {
		public String[] getShowInTargetIds() {
			return SHOW_IN_TARGETS;
		}
	};

	/**
	 * Constructs SeamSearchResultPage object 
	 */
	public SeamSearchResultPage() {
		setElementLimit(new Integer(DEFAULT_ELEMENT_LIMIT));
	}
	
	/**
	 * (non-Javadoc)
	 * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#setElementLimit(java.lang.Integer)
	 */
	public void setElementLimit(Integer elementLimit) {
		super.setElementLimit(elementLimit);
		int limit= elementLimit.intValue();
		getSettings().put(KEY_LIMIT, limit);
	}	
	
	/**
	 * (non-Javadoc)
	 * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#getViewer()
	 */
	public StructuredViewer getViewer() {
		return super.getViewer();
	}

	
	private void addDragAdapters(StructuredViewer viewer) {
		Transfer[] transfers= new Transfer[] { ResourceTransfer.getInstance() };
		int ops= DND.DROP_COPY | DND.DROP_LINK;
		viewer.addDragSupport(ops, transfers, new ResourceTransferDragAdapter(viewer));
	}	

	/**
	 * (non-Javadoc)
	 * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#configureTableViewer(org.eclipse.jface.viewers.TableViewer)
	 */
	protected void configureTableViewer(TableViewer viewer) {
		viewer.setUseHashlookup(true);
		SeamSearchViewLabelProvider innerLabelProvider= new SeamSearchViewLabelProvider(this, FileLabelProvider.SHOW_LABEL_PATH);
		viewer.setLabelProvider(new DecoratingLabelProvider(innerLabelProvider, PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()));
		viewer.setContentProvider(new SeamTableContentProvider(this));
		viewer.setComparator(new DecoratorIgnoringViewerSorter(innerLabelProvider));
		fContentProvider= (IFileSearchContentProvider) viewer.getContentProvider();
		addDragAdapters(viewer);
	}

	/**
	 * (non-Javadoc)
	 * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#configureTreeViewer(org.eclipse.jface.viewers.TreeViewer)
	 */
	protected void configureTreeViewer(TreeViewer viewer) {
		viewer.setUseHashlookup(true);
		SeamSearchViewLabelProvider innerLabelProvider= new SeamSearchViewLabelProvider(this, FileLabelProvider.SHOW_LABEL_PATH);
		viewer.setLabelProvider(new DecoratingLabelProvider(innerLabelProvider, PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()));
		viewer.setContentProvider(new SeamTreeContentProvider(this, viewer));
		viewer.setComparator(new DecoratorIgnoringViewerSorter(innerLabelProvider));
		fContentProvider= (IFileSearchContentProvider) viewer.getContentProvider();
		addDragAdapters(viewer);
	}

	/**
	 * (non-Javadoc)
	 * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#showMatch(org.eclipse.search.ui.text.Match, int, int, boolean)
	 */
	protected void showMatch(Match match, int offset, int length, boolean activate) throws PartInitException {
		if (match.getElement() instanceof IJavaSourceReference) {
			IJavaElement javaElement = ((IJavaSourceReference)match.getElement()).getSourceMember();
			try {
				IEditorPart part = JavaUI.openInEditor(javaElement);
				if (part != null) {
					JavaUI.revealInEditor(part, (IJavaElement)javaElement);
				}
			} catch (JavaModelException e) {
				// Ignore. It is probably because of Java element is not found 
			}
		} else if (match.getElement() instanceof IOpenableElement) {
			((IOpenableElement)match.getElement()).open();
		} else if (match.getElement() instanceof IFile) {
			IFile file= (IFile) match.getElement();
			IEditorPart editor= fEditorOpener.open(getSite().getPage(),file, activate);
			offset = match.getOffset();
			length = match.getLength();
			if (offset != 0 && length != 0) {
				if (editor instanceof ITextEditor) {
					ITextEditor textEditor= (ITextEditor) editor;
					textEditor.selectAndReveal(offset, length);
				} else if (editor != null) {
					showWithMarker(editor, file, offset, length);
				}
			}
		} else if (match.getElement() instanceof IJavaElement) {
			IJavaElement javaElement = (IJavaElement)match.getElement();
			try {
				IEditorPart part = JavaUI.openInEditor(javaElement);
				if (part != null) {
					JavaUI.revealInEditor(part, (IJavaElement)javaElement);
				}
			} catch (JavaModelException e) {
				// Ignore. It is probably because of Java element is not found 
			}

		}
	}
	
	private void showWithMarker(IEditorPart editor, IFile file, int offset, int length) throws PartInitException {
		IMarker marker= null;
		try {
			marker= file.createMarker(NewSearchUI.SEARCH_MARKER);
			HashMap attributes= new HashMap(4);
			attributes.put(IMarker.CHAR_START, new Integer(offset));
			attributes.put(IMarker.CHAR_END, new Integer(offset + length));
			marker.setAttributes(attributes);
			IDE.gotoMarker(editor, marker);
		} catch (CoreException e) {
			throw new PartInitException(SearchMessages.FileSearchPage_error_marker, e); 
		} finally {
			if (marker != null)
				try {
					marker.delete();
				} catch (CoreException e) {
					// ignore
				}
		}
	}

	/**
	 * (non-Javadoc)
	 * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#fillContextMenu(org.eclipse.jface.action.IMenuManager)
	 */
	protected void fillContextMenu(IMenuManager mgr) {
		super.fillContextMenu(mgr);
		addSortActions(mgr);
		fActionGroup.setContext(new ActionContext(getSite().getSelectionProvider().getSelection()));
		fActionGroup.fillContextMenu(mgr);
	}
	
	private void addSortActions(IMenuManager mgr) {
		if (getLayout() != FLAG_LAYOUT_FLAT)
			return;
		MenuManager sortMenu= new MenuManager(SearchMessages.FileSearchPage_sort_by_label); 
		mgr.appendToGroup(IContextMenuConstants.GROUP_VIEWER_SETUP, sortMenu);
	}

	/**
	 * (non-Javadoc)
	 * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#setViewPart(org.eclipse.search.ui.ISearchResultViewPart)
	 */
	public void setViewPart(ISearchResultViewPart part) {
		super.setViewPart(part);
		fActionGroup= new NewTextSearchActionGroup(part);
	}
	
	/**
	 * (non-Javadoc)
	 * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#init(org.eclipse.ui.part.IPageSite)
	 */
	public void init(IPageSite site) {
		super.init(site);
		IMenuManager menuManager = site.getActionBars().getMenuManager();
		menuManager.appendToGroup(IContextMenuConstants.GROUP_PROPERTIES, new OpenSearchPreferencesAction());
	}
	
	/**
	 * (non-Javadoc)
	 * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#dispose()
	 */
	public void dispose() {
		fActionGroup.dispose();
		super.dispose();
	}

	/**
	 * (non-Javadoc)
	 * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#elementsChanged(java.lang.Object[])
	 */
	protected void elementsChanged(Object[] objects) {
		if (fContentProvider != null)
			fContentProvider.elementsChanged(objects);
	}

	/**
	 * (non-Javadoc)
	 * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#clear()
	 */
	protected void clear() {
		if (fContentProvider != null)
			fContentProvider.clear();
	}

	/**
	 * Sets up a given sort order
	 * 
	 * @param sortOrder
	 */
	public void setSortOrder(int sortOrder) {
		fCurrentSortOrder= sortOrder;
		DecoratingLabelProvider lpWrapper= (DecoratingLabelProvider) getViewer().getLabelProvider();
		((FileLabelProvider) lpWrapper.getLabelProvider()).setOrder(sortOrder);
		getViewer().refresh();
		getSettings().put(KEY_SORTING, fCurrentSortOrder);
	}
	
	/**
	 * (non-Javadoc)
	 * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#restoreState(org.eclipse.ui.IMemento)
	 */
	public void restoreState(IMemento memento) {
		super.restoreState(memento);
		try {
			fCurrentSortOrder= getSettings().getInt(KEY_SORTING);
		} catch (NumberFormatException e) {
			// Ignore
		}
		int elementLimit= DEFAULT_ELEMENT_LIMIT;
		try {
			elementLimit= getSettings().getInt(KEY_LIMIT);
		} catch (NumberFormatException e) {
		}
		if (memento != null) {
			Integer value= memento.getInteger(KEY_SORTING);
			if (value != null)
				fCurrentSortOrder= value.intValue();
			
			value= memento.getInteger(KEY_LIMIT);
			if (value != null)
				elementLimit= value.intValue();
		}
		setElementLimit(new Integer(elementLimit));
	}
	
	/**
	 * (non-Javadoc)
	 * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#saveState(org.eclipse.ui.IMemento)
	 */
	public void saveState(IMemento memento) {
		super.saveState(memento);
		memento.putInteger(KEY_SORTING, fCurrentSortOrder);
		memento.putInteger(KEY_LIMIT, getElementLimit().intValue());
	}
	
	/**
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		if (IShowInTargetList.class.equals(adapter)) {
			return SHOW_IN_TARGET_LIST;
		}
		return null;
	}
	
	/**
	 * (non-Javadoc)
	 * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#getLabel()
	 */
	public String getLabel() {
		String label= super.getLabel();
		StructuredViewer viewer= getViewer();
		if (viewer instanceof TableViewer) {
			TableViewer tv= (TableViewer) viewer;

			AbstractTextSearchResult result= getInput();
			if (result != null) {
				int itemCount= ((IStructuredContentProvider) tv.getContentProvider()).getElements(getInput()).length;
				int fileCount= getInput().getElements().length;
				if (itemCount < fileCount) {					
					String format= SearchMessages.FileSearchPage_limited_format_files; 
					return Messages.format(format, new Object[]{label, new Integer(itemCount), new Integer(fileCount)});
				}
			}
		}
		return label;
	}

	/** copy from Eclipse 3.3 org.eclipse.search.internal.ui.text.ResourceTransferDragAdapter */
	/** Maybe NavigatorDragAdopter would be a better alternative. */ 
	static public class ResourceTransferDragAdapter extends DragSourceAdapter implements TransferDragSourceListener {

		private ISelectionProvider fProvider;

		/**
		 * Creates a new ResourceTransferDragAdapter for the given selection
		 * provider.
		 * 
		 * @param provider the selection provider to access the viewer's selection
		 */
		public ResourceTransferDragAdapter(ISelectionProvider provider) {
			fProvider= provider;
			Assert.isNotNull(fProvider);
		}
		
		public Transfer getTransfer() {
			return ResourceTransfer.getInstance();
		}
		
		public void dragStart(DragSourceEvent event) {
			event.doit= !convertSelection().isEmpty();
		}
		
		public void dragSetData(DragSourceEvent event) {
			List resources= convertSelection();
			event.data= resources.toArray(new IResource[resources.size()]);
		}
		
		public void dragFinished(DragSourceEvent event) {
			if (!event.doit)
				return;
		}
		
		private List convertSelection() {
			ISelection s= fProvider.getSelection();
			if (!(s instanceof IStructuredSelection))
				return Collections.EMPTY_LIST;
			IStructuredSelection selection= (IStructuredSelection) s;
			List result= new ArrayList(selection.size());
			for (Iterator iter= selection.iterator(); iter.hasNext();) {
				Object element= iter.next();
				if (element instanceof IResource) {
					result.add(element);
				}
			}
			return result;
		}
	}

}

package org.jboss.tools.seam.ui.search;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.search.internal.ui.SearchPluginImages;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.text.AbstractTextSearchResult;
import org.eclipse.search.ui.text.IEditorMatchAdapter;
import org.eclipse.search.ui.text.IFileMatchAdapter;
import org.eclipse.search.ui.text.Match;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

public class ELSearchResult extends AbstractTextSearchResult implements IEditorMatchAdapter, IFileMatchAdapter {
	private ELSearchQuery query;
	private final Match[] EMPTY_ARR= new Match[0];
	
	public ELSearchResult(ELSearchQuery query){
		super();
		this.query = query;
	}

	public ImageDescriptor getImageDescriptor() {
		return SearchPluginImages.DESC_OBJ_TSEARCH_DPDN;
	}

	public String getLabel() {
		return "'"+query.getPropertyName()+"' property references in Expression Language - "+getMatchCount()+" matches found";
	}

	public ISearchQuery getQuery() {
		return query;
	}

	public String getTooltip() {
		return getLabel();
	}

	@Override
	public IEditorMatchAdapter getEditorMatchAdapter() {
		return this;
	}

	@Override
	public IFileMatchAdapter getFileMatchAdapter() {
		return this;
	}

	public Match[] computeContainedMatches(AbstractTextSearchResult result,
			IEditorPart editor) {
		IEditorInput ei= editor.getEditorInput();
		if (ei instanceof IFileEditorInput) {
			IFileEditorInput fi= (IFileEditorInput) ei;
			return getMatches(fi.getFile());
		}
		return EMPTY_ARR;
	}

	public boolean isShownInEditor(Match match, IEditorPart editor) {
		IEditorInput ei= editor.getEditorInput();
		if (ei instanceof IFileEditorInput) {
			IFileEditorInput fi= (IFileEditorInput) ei;
			return match.getElement().equals(fi.getFile());
		}
		return false;
	}

	public Match[] computeContainedMatches(AbstractTextSearchResult result,
			IFile file) {
		return getMatches(file);
	}

	public IFile getFile(Object element) {
		if (element instanceof IFile)
			return (IFile)element;
		return null;
	}

}

package org.jboss.tools.seam.ui.pages.editor;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IEditorInput;

public class FakePageEditor implements IAdaptable {
	
	public FakePageEditor(IEditorInput input) {}

	public ISelectionProvider getModelSelectionProvider() {
		return null;
	}

	public Object getAdapter(Class adapter) {
		return null;
	}

	public void dispose() {}

}

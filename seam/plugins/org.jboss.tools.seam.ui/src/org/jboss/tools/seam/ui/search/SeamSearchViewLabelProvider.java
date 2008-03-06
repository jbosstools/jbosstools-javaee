package org.jboss.tools.seam.ui.search;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.search.internal.ui.text.FileLabelProvider;
import org.eclipse.search.ui.text.AbstractTextSearchViewPage;
import org.eclipse.swt.graphics.Image;
import org.jboss.tools.seam.core.ISeamContextVariable;
import org.jboss.tools.seam.core.ISeamElement;
import org.jboss.tools.seam.core.ISeamJavaSourceReference;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.ui.views.SeamLabelProvider;

public class SeamSearchViewLabelProvider extends LabelProvider {
	private FileLabelProvider fFileLabelProvider;
	private SeamLabelProvider fSeamLabelProvider;
	private AbstractTextSearchViewPage fPage;
	private int fOrderFlag;
	
	public SeamSearchViewLabelProvider(AbstractTextSearchViewPage page, int orderFlag) {
		fPage = page;
		fOrderFlag = orderFlag;
		fFileLabelProvider = new FileLabelProvider(page, orderFlag);
		fSeamLabelProvider = new SeamLabelProvider();
	}
	
	@Override
	public Image getImage(Object element) {
		if (element instanceof ISeamContextVariable ||
				element instanceof ISeamJavaSourceReference) {
			return fSeamLabelProvider.getImage(element);
		}
		if (element instanceof ISeamElement) {
			return fSeamLabelProvider.getImage(element);
		}
		if (element instanceof IProject) {
			ISeamProject seamProject = SeamCorePlugin.getSeamProject((IProject)element, false);
			if (seamProject != null) {
				return fSeamLabelProvider.getImage(seamProject);
			} 
			return fFileLabelProvider.getImage(element);
		}
		if (element instanceof IFolder) {
			return fFileLabelProvider.getImage(element);
		}
		if (element instanceof IFile) {
			return fFileLabelProvider.getImage(element);
		}
		return null;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof ISeamContextVariable ||
				element instanceof ISeamJavaSourceReference) {
			return fSeamLabelProvider.getText(element);
		}
		if (element instanceof ISeamElement) {
			return fSeamLabelProvider.getText(element);
		}
		if (element instanceof IProject) {
			ISeamProject seamProject = SeamCorePlugin.getSeamProject((IProject)element, false);
			if (seamProject != null) {
				return fSeamLabelProvider.getText(seamProject);
			} 
			return fFileLabelProvider.getText(element);
		}
		
		if (element instanceof IFile) {
			return fFileLabelProvider.getText(element);
		}
		if (element instanceof IFolder) {
			return fFileLabelProvider.getText(element);
		}
		return null;
	}

}

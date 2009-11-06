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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.ui.viewsupport.JavaUILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.search.internal.ui.text.FileLabelProvider;
import org.eclipse.search.ui.text.AbstractTextSearchViewPage;
import org.eclipse.swt.graphics.Image;
import org.jboss.tools.common.java.IJavaSourceReference;
import org.jboss.tools.seam.core.ISeamContextVariable;
import org.jboss.tools.seam.core.ISeamElement;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.ui.views.SeamLabelProvider;

/**
 * Seam search view label provider
 * 
 * @author Jeremy
 *
 */
public class SeamSearchViewLabelProvider extends LabelProvider {
	private FileLabelProvider fFileLabelProvider;
	private SeamLabelProvider fSeamLabelProvider;
	private JavaUILabelProvider fJavaLabelProvider;
	private AbstractTextSearchViewPage fPage;
	private int fOrderFlag;
	
	/**
	 * Constructs SeamSearchViewLabelProvider for a given search results page
	 * 
	 * @param page
	 * @param orderFlag
	 */
	public SeamSearchViewLabelProvider(AbstractTextSearchViewPage page, int orderFlag) {
		fPage = page;
		fOrderFlag = orderFlag;
		fFileLabelProvider = new FileLabelProvider(page, orderFlag);
		fSeamLabelProvider = new SeamLabelProvider();
		fJavaLabelProvider = new JavaUILabelProvider();
	}
	
	@Override
	public Image getImage(Object element) {
		if (element instanceof ISeamContextVariable ||
				element instanceof IJavaSourceReference) {
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
		if (element instanceof IJavaElement) {
			return fJavaLabelProvider.getImage(element);
		}
		return null;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof ISeamContextVariable ||
				element instanceof IJavaSourceReference) {
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
		if (element instanceof IJavaElement) {
			return fJavaLabelProvider.getText(element);
		}

		return null;
	}

}

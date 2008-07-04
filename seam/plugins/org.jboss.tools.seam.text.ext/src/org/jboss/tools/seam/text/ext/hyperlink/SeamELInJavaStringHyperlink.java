/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.seam.text.ext.hyperlink;

import java.text.MessageFormat;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.jboss.tools.common.text.ext.hyperlink.xpl.Messages;
import org.jboss.tools.seam.text.ext.SeamExtPlugin;

public class SeamELInJavaStringHyperlink implements IHyperlink {

	private final IRegion fRegion; 
	private final IJavaElement[] fElements;

	/**
	 * Creates a new Seam EL in Java string hyperlink.
	 */
	SeamELInJavaStringHyperlink(IRegion region, IJavaElement[] elements) {
		Assert.isNotNull(region);
		Assert.isNotNull(elements);

		fRegion = region;
		fElements = elements;
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.javaeditor.IHyperlink#getHyperlinkRegion()
	 * @since 3.1
	 */
	public IRegion getHyperlinkRegion() {
		return fRegion;
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.javaeditor.IHyperlink#open()
	 * @since 3.1
	 */
	public void open() {
		try {
			IEditorPart part = null;
			for (int i = 0; fElements != null && i < fElements.length; i++) {
				part = JavaUI.openInEditor(fElements[i]);
				if (part != null) {
					if (fElements[i] != null)
						JavaUI.revealInEditor(part, fElements[i]);
					break;
				} 
			}
			
		} catch (PartInitException e) {
			SeamExtPlugin.getPluginLog().logError(e);  
		} catch (JavaModelException e) {
			// Ignore. It is probably because of Java element is not found 
		}
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.javaeditor.IHyperlink#getTypeLabel()
	 * @since 3.1
	 */
	public String getTypeLabel() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IHyperlink#getHyperlinkText()
	 */
	public String getHyperlinkText() {
		if (fElements == null || fElements.length == 0)
			return  MessageFormat.format(Messages.OpenA, Messages.Bean);
		
		return MessageFormat.format(Messages.Open, fElements[0].getElementName());
	}

	
}

/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.seam.text.ext.hyperlink;

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.IEditorPart;
import org.jboss.tools.common.text.ext.hyperlink.AbstractHyperlink;
import org.jboss.tools.common.text.ext.hyperlink.xpl.Messages;

/**
 * @author Jeremy
 */
public class SeamBeanHyperlink extends AbstractHyperlink {

	/**
	 * @see com.ibm.sse.editor.AbstractHyperlink#doHyperlink(org.eclipse.jface.text.IRegion)
	 */
	protected void doHyperlink(IRegion region) {
		try {
			List<IJavaElement> elements = SeamBeanHyperlinkPartitioner.findJavaElements(getDocument(), region);

			IEditorPart part = null;
			if (elements != null) {
				for (IJavaElement element : elements) {
					part = JavaUI.openInEditor(element);
					if (part != null) {
						if (element != null)
							JavaUI.revealInEditor(part, element);
						break;
					} 
				}
			}
			
			if (part == null) {
				// could not open editor
				openFileFailed();
			}
		} catch (CoreException x) {
			// could not open editor
			openFileFailed();
		}
	}

	IRegion fLastRegion = null;
	/**
	 * @see com.ibm.sse.editor.AbstractHyperlink#doGetHyperlinkRegion(int)
	 */
	protected IRegion doGetHyperlinkRegion(int offset) {
		fLastRegion = SeamBeanHyperlinkPartitioner.getWordRegion(getDocument(), offset);
		return fLastRegion;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see IHyperlink#getHyperlinkText()
	 */
	public String getHyperlinkText() {
		
		String beanName;
		try {
			beanName = getDocument().get(fLastRegion.getOffset(), fLastRegion.getLength());
		} catch (BadLocationException e) {
			beanName = null;
		}
		
		if (beanName == null)
			return  MessageFormat.format(Messages.NotFound, Messages.Bean);
		
		return MessageFormat.format(Messages.OpenBean, beanName);
	}
}
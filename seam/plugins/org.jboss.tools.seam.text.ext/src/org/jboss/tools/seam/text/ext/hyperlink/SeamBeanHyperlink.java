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

import java.util.List;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.IEditorPart;
import org.jboss.tools.common.text.ext.hyperlink.AbstractHyperlink;
import org.jboss.tools.seam.text.ext.SeamExtPlugin;

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
		} catch (Exception x) {
			// could not open editor
			openFileFailed();
		}
	}

	/**
	 * @see com.ibm.sse.editor.AbstractHyperlink#doGetHyperlinkRegion(int)
	 */
	protected IRegion doGetHyperlinkRegion(int offset) {
		IRegion region = SeamBeanHyperlinkPartitioner.getWordRegion(getDocument(), offset);
		return region;
	}

}
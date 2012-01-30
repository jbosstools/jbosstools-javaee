/*******************************************************************************
 * Copyright (c) 2007-2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.jsf.text.ext.hyperlink;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.jboss.tools.common.text.ext.hyperlink.IHyperlinkRegion;
import org.jboss.tools.common.text.ext.util.StructuredModelWrapper;
import org.jboss.tools.jsf.project.JSFNature;
import org.jboss.tools.jsf.text.ext.JSFExtensionsPlugin;
import org.jboss.tools.jst.text.ext.hyperlink.jsp.JSPLinkHyperlinkPartitioner;

public class JSFJSPLinkHyperlinkPartitioner extends JSPLinkHyperlinkPartitioner {
	public static final String JSF_JSP_LINK_PARTITION = "org.jboss.tools.common.text.ext.jsp.JSF_JSP_LINK"; //$NON-NLS-1$

	private String[] JSF_PROJECT_NATURES = {
		JSFNature.NATURE_ID
	};

	/**
	 * @see org.jboss.tools.common.text.ext.hyperlink.XMLLinkHyperlinkPartitioner#getPartitionType()
	 */
	protected String getPartitionType() {
		return JSF_JSP_LINK_PARTITION;
	}

	/**
	 * @see org.jboss.tools.common.text.ext.hyperlink.XMLContextParamLinkHyperlinkPartitioner#recognizeNature(org.eclipse.jface.text.IDocument)
	 */
	protected boolean recognizeNature(IDocument document) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		smw.init(document);
		try {
			IFile documentFile = smw.getFile();
			if (documentFile == null)
				return false;

			IProject project = documentFile.getProject();

			for (int i = 0; i < JSF_PROJECT_NATURES.length; i++) {
				if (project.getNature(JSF_PROJECT_NATURES[i]) != null) 
					return true;
			}
			return false;
		} catch (CoreException x) {
			JSFExtensionsPlugin.log("", x); //$NON-NLS-1$
			return false;
		} finally {
			smw.dispose();
		}
	}

	/**
	 * @see com.ibm.sse.editor.extensions.hyperlink.IHyperlinkPartitionRecognizer#recognize(org.eclipse.jface.text.IDocument, com.ibm.sse.editor.extensions.hyperlink.IHyperlinkRegion)
	 */
	public boolean recognize(IDocument document, int offset, IHyperlinkRegion region) {
		return recognizeNature(document) ? super.recognize(document, offset, region) : false;
	}
}

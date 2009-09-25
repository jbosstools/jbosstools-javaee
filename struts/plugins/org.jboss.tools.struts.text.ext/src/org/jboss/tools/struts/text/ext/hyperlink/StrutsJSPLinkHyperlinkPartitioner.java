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
package org.jboss.tools.struts.text.ext.hyperlink;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;

import org.jboss.tools.struts.StrutsProject;
import org.jboss.tools.struts.text.ext.StrutsExtensionsPlugin;
import org.jboss.tools.common.text.ext.hyperlink.IHyperlinkRegion;
import org.jboss.tools.jst.text.ext.hyperlink.jsp.JSPLinkHyperlinkPartitioner;
import org.jboss.tools.common.text.ext.util.StructuredModelWrapper;
import org.w3c.dom.Document;

public class StrutsJSPLinkHyperlinkPartitioner extends JSPLinkHyperlinkPartitioner {
	public static final String STRUTS_JSP_LINK_PARTITION = "org.jboss.tools.common.text.ext.jsp.STRUTS_JSP_LINK";

	private String[] STRUTS_PROJECT_NATURES = {StrutsProject.NATURE_ID };

	/**
	 * @see org.jboss.tools.common.text.ext.hyperlink.XMLLinkHyperlinkPartitioner#getPartitionType()
	 */
	protected String getPartitionType() {
		return STRUTS_JSP_LINK_PARTITION;
	}

	/**
	 * @see org.jboss.tools.common.text.ext.hyperlink.XMLContextParamLinkHyperlinkPartitioner#recognizeNature(org.eclipse.jface.text.IDocument)
	 */
	protected boolean recognizeNature(IDocument document) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		smw.init(document);
		try {
			Document xmlDocument = smw.getDocument();
			if(xmlDocument == null) return false;
			IFile documentFile = smw.getFile();
			if (documentFile == null)
				return false;

			IProject project = documentFile.getProject();

			for (int i = 0; i < STRUTS_PROJECT_NATURES.length; i++) {
				if (project.getNature(STRUTS_PROJECT_NATURES[i]) != null) 
					return true;
			}
			return false;
		} catch (CoreException x) {
			StrutsExtensionsPlugin.getPluginLog().logError(x);
			return false;
		} finally {
			smw.dispose();
		}
	}

	/**
	 * @see com.ibm.sse.editor.extensions.hyperlink.IHyperlinkPartitionRecognizer#recognize(org.eclipse.jface.text.IDocument, com.ibm.sse.editor.extensions.hyperlink.IHyperlinkRegion)
	 */
	public boolean recognize(IDocument document, IHyperlinkRegion region) {
		return recognizeNature(document) ? super.recognize(document, region) : false;
	}

}

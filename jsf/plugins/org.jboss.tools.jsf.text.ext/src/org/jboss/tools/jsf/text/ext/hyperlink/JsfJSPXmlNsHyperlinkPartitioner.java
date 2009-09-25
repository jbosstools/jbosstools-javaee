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
package org.jboss.tools.jsf.text.ext.hyperlink;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.jboss.tools.common.text.ext.hyperlink.IHyperlinkRegion;
import org.jboss.tools.jst.text.ext.hyperlink.jsp.JSPTagAttributeValueHyperlinkPartitioner;
import org.jboss.tools.common.text.ext.util.StructuredModelWrapper;
import org.jboss.tools.common.text.ext.util.Utils;
import org.jboss.tools.jsf.project.JSFNature;
import org.jboss.tools.jsf.text.ext.JSFExtensionsPlugin;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class JsfJSPXmlNsHyperlinkPartitioner extends JSPTagAttributeValueHyperlinkPartitioner {

	public static final String JSF_JSP_XMLNS_PARTITION = "org.jboss.tools.common.text.ext.jsp.JSF_JSP_XMLNS"; //$NON-NLS-1$

	private String[] JSF_PROJECT_NATURES = {
		JSFNature.NATURE_ID
	};	

	/**
	 * @see org.jboss.tools.common.text.ext.hyperlink.JSPTagAttributeValueHyperlinkPartitioner#getPartitionType()
	 */
	protected String getPartitionType() {
		return JSF_JSP_XMLNS_PARTITION;
	}
	
	/**
	 * @see com.ibm.sse.editor.extensions.hyperlink.IHyperlinkPartitionRecognizer#recognize(org.eclipse.jface.text.IDocument, com.ibm.sse.editor.extensions.hyperlink.IHyperlinkRegion)
	 */
	public boolean recognize(IDocument document, IHyperlinkRegion region) {
		if (!recognizeNature(document)) 
			return false;

		StructuredModelWrapper smw = new StructuredModelWrapper();
		smw.init(document);
		try {
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return false;
			
			Node n = Utils.findNodeForOffset(xmlDocument, region.getOffset());
			if (!(n instanceof Attr)) return false; 
			Attr xmlnsAttr = (Attr)n;
			if (xmlnsAttr.getName() == null || !xmlnsAttr.getName().startsWith("xmlns:")) return false; //$NON-NLS-1$
			Element rootElem = xmlnsAttr.getOwnerElement();
			if (!rootElem.getNodeName().equals("jsp:root")) return false; //$NON-NLS-1$
			return true;
		} finally {
			smw.dispose();
		}
	}
	
	/**
	 * @see org.jboss.tools.common.text.ext.hyperlink.IDOMContextParamLinkHyperlinkPartitioner#recognizeNature(org.eclipse.jface.text.IDocument)
	 */
	protected boolean recognizeNature(IDocument document) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		try {
			smw.init(document);
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

	public IRegion getRegion(IDocument document, final int offset) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		try {
			smw.init(document);
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;
			
			Node n = Utils.findNodeForOffset(xmlDocument, offset);

			if (n instanceof Attr) n = ((Attr)n).getOwnerElement();
			if (n == null) return null;
			
			final int propStart = Utils.getValueStart(n);
			final int propLength = Utils.getValueEnd(n) - propStart;
			
			if (propStart > offset || propStart + propLength < offset) return null;

			return new Region(propStart,propLength);
		} finally {
			smw.dispose();
		}
		
	}


}

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
package org.jboss.tools.struts.text.ext.hyperlink;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.jboss.tools.common.text.ext.hyperlink.AbstractHyperlinkPartitioner;
import org.jboss.tools.common.text.ext.hyperlink.HyperlinkRegion;
import org.jboss.tools.common.text.ext.hyperlink.IHyperlinkPartitionRecognizer;
import org.jboss.tools.common.text.ext.hyperlink.IHyperlinkRegion;
import org.jboss.tools.common.text.ext.util.StructuredModelWrapper;
import org.jboss.tools.common.text.ext.util.Utils;
import org.jboss.tools.jst.text.ext.hyperlink.jsp.JSPRootHyperlinkPartitioner;
import org.jboss.tools.struts.StrutsProject;
import org.jboss.tools.struts.text.ext.StrutsExtensionsPlugin;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * @author Jeremy
 */
public class StrutsJSPTagNameHyperlinkPartitioner extends AbstractHyperlinkPartitioner implements IHyperlinkPartitionRecognizer {
	public static final String STRUTS_JSP_TAG_NAME_PARTITION = "org.jboss.tools.common.text.ext.jsp.STRUTS_JSP_TAG_NAME";

	private String[] STRUTS_PROJECT_NATURES = {StrutsProject.NATURE_ID };
	
	/**
	 * @see com.ibm.sse.editor.hyperlink.AbstractHyperlinkPartitioner#parse(org.eclipse.jface.text.IDocument, com.ibm.sse.editor.extensions.hyperlink.IHyperlinkRegion)
	 */
	protected IHyperlinkRegion parse(IDocument document, int offset, IHyperlinkRegion superRegion) {
		if(document == null || superRegion == null) return null;
		StructuredModelWrapper smw = new StructuredModelWrapper();
		smw.init(document);
		try {
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;
			
			IRegion r = getRegion(document, offset);
			if (r == null) return null;
			
			String axis = getAxis(document, superRegion);
			String contentType = superRegion.getContentType();
			String type = STRUTS_JSP_TAG_NAME_PARTITION;
			
			return new HyperlinkRegion(r.getOffset(), r.getLength(), axis, contentType, type);
		} finally {
			smw.dispose();
		}
	}

	/**
	 * @see com.ibm.sse.editor.extensions.hyperlink.IHyperlinkPartitionRecognizer#recognize(org.eclipse.jface.text.IDocument, com.ibm.sse.editor.extensions.hyperlink.IHyperlinkRegion)
	 */
	public boolean recognize(IDocument document, int offset, IHyperlinkRegion region) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		smw.init(document);
		try {
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

	protected IRegion getRegion(IDocument document, final int offset) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		smw.init(document);
		try {
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;
			
			Node n = Utils.findNodeForOffset(xmlDocument, offset);
			if (n == null || !(n instanceof IDOMElement)) return null;
			
			IDOMElement elem = (IDOMElement)n;
			String tagName = elem.getTagName();
			int start = elem.getStartOffset();

			final int nameStart = start + (elem.isEndTag() ? "</" : "<").length();
			final int nameEnd = nameStart + tagName.length();
			if (nameStart > offset || nameEnd <= offset) return null;

			return new Region(nameStart, nameEnd-nameStart);
		} finally {
			smw.dispose();
		}
		
	}

	protected String getAxis(IDocument document, IHyperlinkRegion superRegion) {
		if (superRegion.getAxis() == null || superRegion.getAxis().length() == 0) {
			return JSPRootHyperlinkPartitioner.computeAxis(document, superRegion.getOffset()) + "/";
		}
		return superRegion.getAxis();
	}
}

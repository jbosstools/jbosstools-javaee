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
package org.jboss.tools.cdi.seam.text.ext.hyperlink;

import java.text.MessageFormat;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.ui.PartInitException;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.seam.text.ext.CDISeamExtMessages;
import org.jboss.tools.common.text.ext.hyperlink.AbstractHyperlink;
import org.jboss.tools.jst.text.ext.hyperlink.jsp.JSPRootHyperlinkPartitioner;
import org.jboss.tools.common.text.ext.util.StructuredModelWrapper;
import org.jboss.tools.common.text.ext.util.Utils;
import org.jboss.tools.common.util.EclipseJavaUtil;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.jboss.tools.cdi.seam.config.core.CDISeamConfigExtension;
import org.jboss.tools.cdi.seam.config.core.definition.SeamBeansDefinition;
import org.jboss.tools.cdi.seam.config.core.definition.SeamFieldDefinition;
import org.jboss.tools.cdi.seam.config.core.definition.SeamMemberDefinition;
import org.jboss.tools.cdi.seam.config.core.definition.SeamMethodDefinition;
import org.jboss.tools.cdi.seam.config.core.util.Util;

/**
 * @author Jeremy
 */
public class SeamConfigTagNameHyperlink extends AbstractHyperlink {

	/**
	 * @see com.ibm.sse.editor.AbstractHyperlink#doHyperlink(org.eclipse.jface.text.IRegion)
	 */
	protected void doHyperlink(IRegion region) {
		IFile documentFile = getFile();
		if(documentFile == null) return;
		
		IProject project = documentFile.getProject();
		CDICoreNature cdi = CDICorePlugin.getCDI(project, true);
		if(cdi == null) return;

		String tagName = getTagName(region);
		if(tagName == null) return;
		int at = tagName.indexOf('@');
		String attrName = null;
		if(at >= 0) {
			attrName = tagName.substring(at + 1);
			tagName = tagName.substring(0, at);
		}
		String uri = getURI(region);

		IJavaElement element = null;
		
		IType type = Util.resolveType(tagName, uri, cdi);
		if(type != null) {
			element = type;
			if(attrName != null) {
				try {				
					if(type.isAnnotation()) {
						IMethod m = type.getMethod(attrName, new String[0]);
						if(m != null && m.exists()) element = m;
					} else {
						IField f = type.getField(attrName);
						if(f != null && f.exists()) {
							element = f;
						}
					}
				} catch (CoreException e) {
					
				}
			}
		} else {
			SeamMemberDefinition def = find(cdi, region, documentFile);
			if(def instanceof SeamFieldDefinition) {
				element = ((SeamFieldDefinition)def).getField();
			} else if(def instanceof SeamMethodDefinition) {
				element = ((SeamMethodDefinition)def).getMethod();
			}
		}
		if(element != null) {
			try {
				JavaUI.openInEditor(element);
			} catch (JavaModelException e) {
			} catch (PartInitException e) {
				
			}
		}
	}

	SeamMemberDefinition find(CDICoreNature cdi, IRegion region, IFile documentFile) {
		CDISeamConfigExtension ext = CDISeamConfigExtension.getExtension(cdi);
		if(ext == null) return null;		
		SeamBeansDefinition def = ext.getContext().getDefinition(documentFile.getFullPath());
		if(def == null) return null;		
		return def.findExactly(region.getOffset());
	}

	private IType resolve(IJavaProject jp, String tagName, String uri) {
		return null;
	}

	private String getURI(IRegion region) {
		return getURI(region, getDocument());
	}
	
	public static String getURI(IRegion region, IDocument document) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		smw.init(document);
		try {
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;
			Node n = Utils.findNodeForOffset(xmlDocument, region.getOffset());
			if(n instanceof Attr) {
				n = ((Attr)n).getOwnerElement();
			}
			if (!(n instanceof Element)) return null;			
			return getURI(n, document, region.getOffset());
		} finally {
			smw.dispose();
		}
	}

	public static String getURI(Node node, IDocument document, int offset) {
		String nodeName = node.getNodeName();
		if (nodeName.indexOf(':') == -1) return null;
		String nodePrefix = nodeName.substring(0, nodeName.indexOf(":")); //$NON-NLS-1$
		if (nodePrefix == null || nodePrefix.length() == 0) return null;		
		Map trackers = JSPRootHyperlinkPartitioner.getTrackersMap(document, offset);		
		return (String)(trackers == null ? null : trackers.get(nodePrefix));
	}
	
	private String getTagName(IRegion region) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		smw.init(getDocument());
		try {
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;
			
			Node node = Utils.findNodeForOffset(xmlDocument, region.getOffset());
			
			Attr attr = null;
			Element elem = null;

			if (node instanceof Element) {
				elem = (Element)node;
			} else if(node instanceof Attr) {
				attr = (Attr)node;
				elem = attr.getOwnerElement();
			}

			if(elem == null) return null;
			
			String tagName = elem.getNodeName();
			if (tagName.indexOf(':') == -1) return null;
			
			tagName = tagName.substring(tagName.indexOf(':') + 1);
			if(attr != null) {
				tagName += "@" + attr.getName();
			}
			
			return tagName;
		} finally {
			smw.dispose();
		}
	}
	
	IRegion fLastRegion = null;
	/**
	 * @see com.ibm.sse.editor.AbstractHyperlink#doGetHyperlinkRegion(int)
	 */
	protected IRegion doGetHyperlinkRegion(int offset) {
		fLastRegion = getRegion(offset);
		return fLastRegion;
	}
	
	protected IRegion getRegion (int offset) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		smw.init(getDocument());
		try {
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;
			
			Node n = Utils.findNodeForOffset(xmlDocument, offset);

			if (n == null || !(n instanceof IDOMElement)) return null;
			
			IDOMElement elem = (IDOMElement)n;
			String tagName = elem.getTagName();
			int start = elem.getStartOffset();
			final int nameStart = start + (elem.isEndTag() ? "</" : "<").length(); //$NON-NLS-1$ //$NON-NLS-2$
			final int nameEnd = nameStart + tagName.length();

			if (nameStart > offset || nameEnd <= offset) return null;
			
			return new Region(nameStart,nameEnd - nameStart);
		} finally {
			smw.dispose();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see IHyperlink#getHyperlinkText()
	 */
	public String getHyperlinkText() {
		String tagName = getTagName(fLastRegion);
		if (tagName == null)
			return CDISeamExtMessages.CDI_SEAM_CONFIG_OPEN_TAG;
		
		return MessageFormat.format(CDISeamExtMessages.CDI_SEAM_CONFIG_OPEN_TAG, tagName);
	}

}

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

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.text.ext.hyperlink.AbstractHyperlink;
import org.jboss.tools.jst.text.ext.hyperlink.jsp.JSPRootHyperlinkPartitioner;
import org.jboss.tools.common.text.ext.hyperlink.xpl.Messages;
import org.jboss.tools.common.text.ext.util.StructuredModelWrapper;
import org.jboss.tools.common.text.ext.util.StructuredSelectionHelper;
import org.jboss.tools.common.text.ext.util.Utils;
import org.jboss.tools.jst.web.project.WebProject;
import org.jboss.tools.jst.web.tld.TaglibMapping;
import org.jboss.tools.struts.text.ext.StrutsExtensionsPlugin;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * @author Jeremy
 */
public class StrutsBeanNameHyperlink extends AbstractHyperlink {
	private static final String[] TAGS_TO_FIND = {
			"cookie",
			"define",
			"header",
			"include",
			"message", 
			"page", 
			"parameter", 
			"resource", 
			"size",
			"struts"
		};

	/**
	 * @see com.ibm.sse.editor.AbstractHyperlink#doHyperlink(org.eclipse.jface.text.IRegion)
	 */
	protected void doHyperlink(IRegion region) {
		if(region == null) {
			openFileFailed();
			return;
		}
		
		String forID = getForId(region);
		String prefix = getPrefix(region);
			
		IRegion elementByID = findElementByIDBackward(forID, region.getOffset(), prefix);
		if (elementByID != null) {
			StructuredSelectionHelper.setSelectionAndRevealInActiveEditor(elementByID);
			return;
		}
		StrutsFormBeanHyperlink openOn = new StrutsFormBeanHyperlink();
		openOn.setDocument(getDocument());
		openOn.setOffset(getOffset());
		openOn.doHyperlink(region);
	}
	
	private IRegion findElementByIDBackward (String id, int endOffset, String prefix) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		smw.init(getDocument());
		try {
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;

			Node n = Utils.findNodeForOffset(xmlDocument, endOffset);

			if (n == null) return null;
			if (n instanceof Attr) n = ((Attr)n).getOwnerElement();
			if (!(n instanceof Node)) return null;

			Element element = null;
			for (Node parent = n;parent != null && element == null; parent = parent.getParentNode()) {
				element = findElementByIDBackward(xmlDocument.getChildNodes(), id, endOffset, prefix); 
			}

			if (!(element instanceof IDOMElement)) return null;
			
			final int offset = Utils.getValueStart(element);
			final int length = ((IDOMElement)element).getStartStructuredDocumentRegion().getLength();
			return new IRegion () {
				public boolean equals(Object arg) {
					if (!(arg instanceof IRegion)) return false;
					IRegion region = (IRegion)arg;
					
					if (getOffset() != region.getOffset()) return false;
					if (getLength() != region.getLength()) return false;
					return true;
				}

				public int getLength() {
					return length;
				}

				public int getOffset() {
					return offset;
				}

				public String toString() {
					return "IRegion [" + getOffset() +", " + getLength()+ "]";
				}
			};
		} finally {
			smw.dispose();
		}
	}
	
	private Element findElementByIDBackward(NodeList list, String id, int endOffset, String prefix) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		smw.init(getDocument());
		try {
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;

			Map trackersMap = JSPRootHyperlinkPartitioner.getTrackersMap(getDocument(), endOffset);
			XModel xModel = smw.getXModel();
			TaglibMapping tm = (xModel == null) ? null : WebProject.getInstance(xModel).getTaglibMapping();

			for (int i = list.getLength() - 1; list != null && i >= 0; i--) {
				if(!(list.item(i) instanceof Element)) continue;
				Element element = (Element)list.item(i);
				int start = Utils.getValueStart(element);
				if (start < 0 || start >= endOffset) continue;
					
				String elementExtracted = JSPRootHyperlinkPartitioner.extractName(element.getNodeName(), trackersMap, tm);
				if (isInList(elementExtracted, trackersMap, tm, prefix)) {
						
					Attr idAttr = element.getAttributeNode("id");
					if (idAttr != null) {
						String val = Utils.trimQuotes(idAttr.getNodeValue());
						if (id.equals(val)) {
							return element;
						}
					}
				}
					
				if (element.hasChildNodes()) {
					Element child = findElementByIDBackward(element.getChildNodes(), id, endOffset, prefix);
					if (child != null) return child;
				}
			}
		} finally {
			smw.dispose();
		}
		return null;
	}

	private boolean isInList(String extractedName, Map trackersMap, TaglibMapping tm, String prefix) {
		for (int i = 0; i < TAGS_TO_FIND.length; i++) {
			String extractedFromList = JSPRootHyperlinkPartitioner.extractName(prefix + ":" + TAGS_TO_FIND[i], trackersMap, tm);
			if (extractedName.equals(extractedFromList)) 
				return true;
			for (Iterator iter = trackersMap.keySet().iterator(); iter.hasNext(); ) {
				String prefixFromMap = (String)iter.next();
				if (((String)trackersMap.get(prefixFromMap)).toLowerCase().indexOf("bean") == -1) continue;
				extractedFromList = JSPRootHyperlinkPartitioner.extractName(prefixFromMap + ":" + TAGS_TO_FIND[i], trackersMap, tm);
				if (extractedName.equals(extractedFromList)) 
					return true;
			}
		}
		return false;
	}
	
	String getForId(IRegion region) {
		try {
			return Utils.trimQuotes(getDocument().get(region.getOffset(), region.getLength()));
		} catch (BadLocationException x) {
			StrutsExtensionsPlugin.getPluginLog().logError(x);
			return null;
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

	private IRegion getRegion(int offset) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		try {
			smw.init(getDocument());
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;
			
			Node n = Utils.findNodeForOffset(xmlDocument, offset);

			if (n == null || !(n instanceof Attr || n instanceof Text)) return null;
			
			int start = Utils.getValueStart(n);
			int end = Utils.getValueEnd(n);

			if (start < 0 || start > offset || end < offset) return null;

			String text = getDocument().get(start, end - start);
			StringBuffer sb = new StringBuffer(text);

			//find start and end of path property
			int bStart = 0;
			int bEnd = text.length() - 1;

			while (bStart < bEnd && (Character.isWhitespace(sb.charAt(bStart)) 
					|| sb.charAt(bStart) == '\"' || sb.charAt(bStart) == '\"')) { 
				bStart++;
			}
			while (bEnd > bStart && (Character.isWhitespace(sb.charAt(bEnd)) 
					|| sb.charAt(bEnd) == '\"' || sb.charAt(bEnd) == '\"')) { 
				bEnd--;
			}
			bEnd++;

			final int propStart = bStart + start;
			final int propLength = bEnd - bStart;
			
			if (propStart > offset || propStart + propLength < offset) return null;
	
			
			IRegion region = new IRegion () {
				public boolean equals(Object arg) {
					if (!(arg instanceof IRegion)) return false;
					IRegion region = (IRegion)arg;
					
					if (getOffset() != region.getOffset()) return false;
					if (getLength() != region.getLength()) return false;
					return true;
				}

				public int getLength() {
					return propLength;
				}

				public int getOffset() {
					return propStart;
				}

				public String toString() {
					return "IRegion [" + getOffset() +", " + getLength()+ "]";
				}
			};
			
			return region;
		} catch (BadLocationException x) {
			StrutsExtensionsPlugin.getPluginLog().logError(x);
			return null;
		} finally {
			smw.dispose();
		}
	}

	private String getPrefix(IRegion region) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		try {
			smw.init(getDocument());
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;
			
			Node n = Utils.findNodeForOffset(xmlDocument, region.getOffset());
			if (n == null) return null;
			if (n instanceof Attr) n = ((Attr)n).getOwnerElement();
			if (n == null) return null;
			
			Node node = n;
			if (node.getNodeName().indexOf(":") == -1) return null;
			return node.getNodeName().substring(0, node.getNodeName().indexOf(":"));
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
		String forId = getForId(fLastRegion);
		if (forId == null)
			return  MessageFormat.format(Messages.BrowseFor, Messages.BeanId);
		
		return MessageFormat.format(Messages.BrowseForBeanId, forId);
	}

}
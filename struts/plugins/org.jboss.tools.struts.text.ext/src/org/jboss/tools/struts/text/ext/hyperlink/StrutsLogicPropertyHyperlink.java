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
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.text.ext.hyperlink.AbstractHyperlink;
import org.jboss.tools.jst.text.ext.hyperlink.jsp.JSPRootHyperlinkPartitioner;
import org.jboss.tools.common.text.ext.hyperlink.xpl.Messages;
import org.jboss.tools.common.text.ext.util.StructuredModelWrapper;
import org.jboss.tools.common.text.ext.util.Utils;
import org.jboss.tools.jst.web.project.WebProject;
import org.jboss.tools.jst.web.project.list.WebPromptingProvider;
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
public class StrutsLogicPropertyHyperlink extends AbstractHyperlink {
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
		IFile documentFile = getFile();
		XModel xModel = getXModel(documentFile);
		if (xModel == null) {
			openFileFailed();
			return;
		}
		
		StructuredModelWrapper smw = new StructuredModelWrapper();
		smw.init(getDocument());
		try {
			String forID = getForId(region);
			String prefix = getPrefix(region);
			
			IRegion elementByID = findElementByIDBackward(forID, region.getOffset(), prefix);
			if (elementByID != null) {
				openFileFailed();
				return;
			}

			WebPromptingProvider provider = WebPromptingProvider.getInstance();

			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) {
				openFileFailed();
				return;
			}
			
			Node n = Utils.findNodeForOffset(xmlDocument, region.getOffset());

			if (n instanceof Attr) n = ((Attr)n).getOwnerElement();
			if ((n == null) || !(n instanceof Node)) {
				openFileFailed();
				return;
			}
			
			Properties p = new Properties();

			p.setProperty(WebPromptingProvider.PROPERTY, getFormBeanProperty(region));
			p.put(WebPromptingProvider.FILE, documentFile);

			List<Object> list = provider.getList(xModel, WebPromptingProvider.STRUTS_OPEN_FORM_BEAN, getFormBeanName(region, n), p);
			if (list != null && list.size() >= 1) {
				openFileInEditor((String)list.get(0));
				return;
			}
			String error = p.getProperty(WebPromptingProvider.ERROR); 
			if ( error != null && error.length() > 0) {
				openFileFailed();
			}
		} finally {
			smw.dispose();
		}

	}
	
	private String getFormBeanName(IRegion region, Node node) {
		if(region == null || node == null) return null;
		try {
			Attr attr = (Attr)node.getAttributes().getNamedItem("name");
			return Utils.getTrimmedValue(getDocument(), attr);
		} catch (BadLocationException x) {
			StrutsExtensionsPlugin.getPluginLog().logError(x);
			return null;
		}
	}

	private String getFormBeanProperty(IRegion region) {
		if(region == null || getDocument() == null) return "";
		try {
			return Utils.trimQuotes(getDocument().get(region.getOffset(), region.getLength()));
		} catch (BadLocationException x) {
			StrutsExtensionsPlugin.getPluginLog().logError(x);
			return "";
		}
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
			XModel xModel = null;
			TaglibMapping tm = null;
			if(xModel != null) {
				tm = WebProject.getInstance(xModel).getTaglibMapping();
			}

			for (int i = list.getLength() - 1; list != null && i >= 0; i--) {
				if(!(list.item(i) instanceof Element)) continue;
				Element element = (Element)list.item(i);
				int start = Utils.getValueStart(element);
				if (start < 0 || start >= endOffset) continue;
					
				String elementExtracted = JSPRootHyperlinkPartitioner.extractName(element.getNodeName(), trackersMap, tm);
				if (isInList(elementExtracted, trackersMap, tm, prefix)) {
					
					Attr idAttr = (Attr)element.getAttributeNode("id");
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
		if(region == null || getDocument() == null) return null;
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
		smw.init(getDocument());
		try {
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;
			
			Node n = Utils.findNodeForOffset(xmlDocument, offset);

			if (n == null || !(n instanceof Attr || n instanceof Text)) return null;
			
			int start = Utils.getValueStart(n);
			int end = Utils.getValueEnd(n);

			if (start > offset || end < offset) return null;

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
		if(region == null) return null;
		StructuredModelWrapper smw = new StructuredModelWrapper();
		smw.init(getDocument());
		try {
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;
			
			Node n = Utils.findNodeForOffset(xmlDocument, region.getOffset());
			if (n == null) return null;
			if (n instanceof Attr) n = ((Attr)n).getOwnerElement();
			if (!(n instanceof Node)) return null;
			
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
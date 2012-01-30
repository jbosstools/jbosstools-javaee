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

import java.text.MessageFormat;
import java.util.Properties;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.jboss.tools.common.text.ext.hyperlink.xpl.Messages;
import org.jboss.tools.common.text.ext.util.StructuredModelWrapper;
import org.jboss.tools.common.text.ext.util.Utils;
import org.jboss.tools.jst.web.project.list.WebPromptingProvider;
import org.jboss.tools.struts.text.ext.StrutsExtensionsPlugin;
import org.jboss.tools.struts.text.ext.StrutsTextExtMessages;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * @author Jeremy
 *
 */
public class StrutsConfigForwardPathHyperlink extends StrutsXModelBasedHyperlink {
	
	private static final String ACTION_TAGNAME = "action";
	private static final String NAME_ATTRNAME = "name";
	private static final String TYPE_ATTRNAME = "type";
	private static final String PATH_ATTRNAME = "path";
	private static final String FORWARD_ATTRNAME = "forward";
	private static final String INCLUDE_ATTRNAME = "include";
	private static final String INPUT_ATTRNAME = "input";

	
	protected String getRequestMethod() {
		return WebPromptingProvider.STRUTS_OPEN_FORWARD_PATH;
	}

	protected Properties getRequestProperties(IRegion region) {
		Properties p = new Properties();
		String path = getPath(region);
		if (path != null)
			p.setProperty(WebPromptingProvider.MODEL_OBJECT_PATH, path);
		p.setProperty("prefix", getAttributeValue(region));
		return p;
	}
	
	private String getAttributeValue(IRegion region) {
		if(region == null || getDocument() == null) return null;
		try {
			return Utils.trimQuotes(getDocument().get(region.getOffset(), region.getLength()));
		} catch (BadLocationException x) {
			StrutsExtensionsPlugin.getPluginLog().logError(x);
			return null;
		}
	}
	
	private String getPath(IRegion region) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		smw.init(getDocument());
		try {
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;
			
			Node n = Utils.findNodeForOffset(xmlDocument, region.getOffset());

			if (n == null || !(n instanceof Attr)) return null;
			
			Node node = ((Attr)n).getOwnerElement();
			Node parentNode = node.getParentNode();
			if (parentNode == null) return null;
			
			if (ACTION_TAGNAME.equals(node.getNodeName())) {
				Node actionNode = node;
				if (actionNode == null) return null;
				
				if (actionNode.getAttributes().getNamedItem(FORWARD_ATTRNAME) == null &&
						actionNode.getAttributes().getNamedItem(INCLUDE_ATTRNAME) == null &&
						actionNode.getAttributes().getNamedItem(INPUT_ATTRNAME) == null)
					return null; // To insure that we're in action@forward attribute
				
				Node actionParentNode = actionNode.getParentNode();
				if (actionParentNode == null) return null;
				
				Attr actionPathAttr = (Attr)actionNode.getAttributes().getNamedItem(PATH_ATTRNAME);
				String actionPath = Utils.getTrimmedValue(getDocument(), actionPathAttr);
				if(actionPath == null) return null;
				return actionParentNode.getNodeName() + "/" + actionPath.replace('/', '#');
			}
			if (ACTION_TAGNAME.equals(parentNode.getNodeName())) {
				Node actionNode = parentNode;
				if (actionNode == null) return null;
				
				Node actionParentNode = actionNode.getParentNode();
				if (actionParentNode == null) return null;
				
				Attr actionPathAttr = (Attr)actionNode.getAttributes().getNamedItem(PATH_ATTRNAME);
				String actionPath = Utils.getTrimmedValue(getDocument(), actionPathAttr);
				Attr forwardNameAttr = (Attr)node.getAttributes().getNamedItem(NAME_ATTRNAME);
				String forwardName = Utils.getTrimmedValue(getDocument(), forwardNameAttr);
				if(actionPath == null || forwardName == null) return null;
				
				return actionParentNode.getNodeName() + "/" + actionPath.replace('/', '#') + "/" + forwardName;
			}

			// Global forwards or exceptions
			Attr typeAttr = (Attr)node.getAttributes().getNamedItem(TYPE_ATTRNAME);
			Attr nameAttr = (Attr)node.getAttributes().getNamedItem(NAME_ATTRNAME);
			String nameOrType = null;
			
			if (typeAttr != null)
				nameOrType = Utils.getTrimmedValue(getDocument(), typeAttr);
			else if (nameAttr != null)
				nameOrType = Utils.getTrimmedValue(getDocument(), nameAttr);
			if (nameOrType == null || nameOrType.trim().length() == 0) 
				return null;
			
			return parentNode.getNodeName() + "/" + nameOrType;
		} catch (BadLocationException x) {
			StrutsExtensionsPlugin.getPluginLog().logError(x);
			return null;
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
		String path = getPath(getHyperlinkRegion());
		if (path == null)
			return  MessageFormat.format(Messages.OpenA, StrutsTextExtMessages.ForwardPath);
		
		return MessageFormat.format(StrutsTextExtMessages.OpenForwardPath, path);
	}
}

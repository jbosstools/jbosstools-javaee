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

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.IEditorPart;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.FindObjectHelper;
import org.jboss.tools.common.text.ext.hyperlink.AbstractHyperlink;
import org.jboss.tools.common.text.ext.util.StructuredModelWrapper;
import org.jboss.tools.common.text.ext.util.Utils;
import org.jboss.tools.jsf.text.ext.JSFTextExtMessages;
import org.jboss.tools.jst.web.ui.internal.text.ext.hyperlink.jsp.JSPRootHyperlinkPartitioner;
import org.jboss.tools.jst.web.kb.KbProjectFactory;
import org.jboss.tools.jst.web.kb.internal.taglib.AbstractComponent;
import org.jboss.tools.jst.web.kb.taglib.IComponent;
import org.jboss.tools.jst.web.kb.taglib.ITagLibrary;
import org.jboss.tools.jst.web.project.list.IWebPromptingProvider;
import org.jboss.tools.jst.web.project.list.WebPromptingProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Jeremy
 */
public class JsfJSPTagNameHyperlink extends AbstractHyperlink {
	public JsfJSPTagNameHyperlink(IRegion region){
		setRegion(region);
	}

	/**
	 * @see com.ibm.sse.editor.AbstractHyperlink#doHyperlink(org.eclipse.jface.text.IRegion)
	 */
	protected void doHyperlink(IRegion region) {
		IFile documentFile = getFile();
		XModel xModel = getXModel(documentFile);
		if (xModel == null) {
			openFileFailed();
			return;
		}
		WebPromptingProvider provider = WebPromptingProvider.getInstance();

		Properties p = getRequestProperties(region);
		p.put(WebPromptingProvider.FILE, documentFile);

		List<Object> list = provider.getList(xModel, WebPromptingProvider.JSF_OPEN_TAG_LIBRARY, p.getProperty("prefix"), p); //$NON-NLS-1$
		if (list != null && list.size() >= 1) {
			openFileInEditor((String)list.get(0));
			return;
		}

		String error = p.getProperty(WebPromptingProvider.ERROR);

		if(error != null) {
			String error1 = openJSF2Component(documentFile, p);
			if(error1 != null) error = error1;
		}
		if ( error != null && error.length() > 0) {
			openFileFailed();
		}
	}

	protected String openJSF2Component(IFile documentFile, Properties p) {
		String uri = p.getProperty("prefix");
		if(uri == null || !uri.startsWith("http://java.sun.com/jsf/composite/")) {
			return null;
		}
		ITagLibrary[] ls = KbProjectFactory.getKbProject(documentFile.getProject(), true).getTagLibraries(uri);
		if(ls == null || ls.length == 0) {
			return "Cannot find JSF 2 library " + uri;
		}
		String error = "";
		String tagName = p.getProperty(IWebPromptingProvider.NAME);
//		String attributeName = p.getProperty(IWebPromptingProvider.ATTRIBUTE);
		IComponent c = ls[0].getComponent(tagName);
		if(c != null) {
			IResource r = c.getResource();
			if(r instanceof IFile) {
				IEditorPart part = null;
				IFile f = (IFile)r;
				if(f.getFullPath() != null && f.getFullPath().toString().endsWith(".jar")) {
					Object id = ((AbstractComponent)c).getId();
					if(id instanceof XModelObject) {
						XModelObject o = (XModelObject)id;
						int q = FindObjectHelper.findModelObject(o, FindObjectHelper.IN_EDITOR_ONLY);
						if(q == 1) {
							error = "Cannot open resource " + r.getName();
						}
					}
				} else {
					part = openFileInEditor(f);
					if(part == null) {
						error = "Cannot open file " + r;
					}
				}
			} else {
				error = "Cannot find file for tag " + tagName;
			}
		} else {
			error = "Component " + tagName + " not found in library " + p.getProperty("prefix");
		}
		return error;
	}

	protected Properties getRequestProperties(IRegion region) {
		Properties p = new Properties();
		
		String value = getURI(region);
		if (value != null) {
			p.setProperty("prefix", value); //$NON-NLS-1$
		}
		value = getTagName(region);
		if (value != null) {
			p.setProperty(WebPromptingProvider.NAME, value);
		}
		
		return p;
	}
	
	private String getURI(IRegion region) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		smw.init(getDocument());
		try {
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;

			Node n = Utils.findNodeForOffset(xmlDocument, region.getOffset());

			if (!(n instanceof Element)) return null;
			
			Node node = n;

			String nodeName = node.getNodeName();
			if (nodeName.indexOf(':') == -1) return null;

			String nodePrefix = nodeName.substring(0, nodeName.indexOf(":")); //$NON-NLS-1$
			if (nodePrefix == null || nodePrefix.length() == 0) return null;

			
			Map trackers = JSPRootHyperlinkPartitioner.getTrackersMap(getDocument(), region.getOffset());
			
			return (String)(trackers == null ? null : trackers.get(nodePrefix));
		} finally {
			smw.dispose();
		}
	}
	
	private String getTagName(IRegion region) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		smw.init(getDocument());
		try {
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;
			
			Node n = Utils.findNodeForOffset(xmlDocument, region.getOffset());

			if (!(n instanceof Element)) return null;
			
			Node node = n;

			String tagName = node.getNodeName();
			if (tagName.indexOf(':') == -1) return null;
			
			return tagName.substring(tagName.indexOf(':') + 1);
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
		String tagName = getTagName(getHyperlinkRegion());
		if (tagName == null)
			return JSFTextExtMessages.OpenTagLibraryForATag;
		
		return MessageFormat.format(JSFTextExtMessages.OpenTagLibraryForTagName, tagName);
	}
}

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

import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IRegion;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.text.ext.util.StructuredModelWrapper;
import org.jboss.tools.common.text.ext.util.Utils;
import org.jboss.tools.common.text.ext.hyperlink.XModelBasedHyperlink;
import org.jboss.tools.jsf.text.ext.JSFExtensionsPlugin;
import org.jboss.tools.jst.web.project.WebProject;
import org.jboss.tools.jst.web.project.list.WebPromptingProvider;
import org.jboss.tools.jst.web.tld.TaglibMapping;

public class JsfJSPXmlNsHyperlink extends XModelBasedHyperlink {
	
	protected String getRequestMethod() {
		return WebPromptingProvider.JSF_OPEN_TAG_LIBRARY;
	}

	protected Properties getRequestProperties(IRegion region) {
		Properties p = new Properties();
		StructuredModelWrapper smw = new StructuredModelWrapper();
		try {
			smw.init(getDocument());
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;

			IFile file = smw.getFile();

			XModel xModel = getXModel(file);

			WebProject wp = WebProject.getInstance(xModel);
			TaglibMapping tm = wp.getTaglibMapping();

			Node n = Utils.findNodeForOffset(xmlDocument, region.getOffset());

			if (n instanceof Attr) {
				Attr attr = (Attr)n;
				
				String name = attr.getName();
				if (name.indexOf("xmlns:") == 0) {
					
					String prefix = name.substring(name.indexOf(':') + 1);
					String uri = Utils.trimQuotes(attr.getValue());
					if (prefix != null && prefix.trim().length() > 0) {
						p.setProperty("prefix", tm.resolveURI(uri));
						
					}
				}
			}
		} catch (Exception x) {
			JSFExtensionsPlugin.log("", x);
		} finally {
			smw.dispose();
		}

		return p;
	}

	protected IRegion getRegion(final int offset) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		try {
			smw.init(getDocument());
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;
			
			
			Node n = Utils.findNodeForOffset(xmlDocument, offset);
			if (!(n instanceof IDOMAttr)) return null; 
			IDOMAttr xmlnsAttr = (IDOMAttr)n;
			if (xmlnsAttr.getName() == null || !xmlnsAttr.getName().startsWith("xmlns:")) return null;
			Element rootElem = xmlnsAttr.getOwnerElement();
			if (!rootElem.getNodeName().equals("jsp:root")) return null;

			final int taglibLength = xmlnsAttr.getValueRegionText().length();
			final int taglibOffset = xmlnsAttr.getValueRegionStartOffset();
			
			IRegion region = new IRegion () {
				public int getLength() {
					return taglibLength;
				}

				public int getOffset() {
					return taglibOffset;
				}
				
				public boolean equals(Object arg) {
					if (!(arg instanceof IRegion)) return false;
					IRegion region = (IRegion)arg;
					
					if (getOffset() != region.getOffset()) return false;
					if (getLength() != region.getLength()) return false;
					return true;
				}

			};
			return region;
		} catch (Exception x) {
			JSFExtensionsPlugin.log("Error while getting region", x);
			return null;
		} finally {
			smw.dispose();
		}
		
	}

}

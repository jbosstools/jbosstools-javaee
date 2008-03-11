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

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import org.jboss.tools.common.text.ext.util.StructuredModelWrapper;
import org.jboss.tools.common.text.ext.util.Utils;
import org.jboss.tools.common.text.ext.hyperlink.XModelBasedHyperlink;
import org.jboss.tools.jsf.text.ext.JSFExtensionsPlugin;
import org.jboss.tools.jst.web.project.list.WebPromptingProvider;

/**
 * @author Jeremy
 *
 */
public class JsfTaglibDirectiveHyperlink extends XModelBasedHyperlink {
	
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
			
			Node n = Utils.findNodeForOffset(xmlDocument, region.getOffset());

			if (n instanceof Attr) n = ((Attr)n).getOwnerElement();
			if ((n == null) || !(n instanceof Node)) return null;
			
			Node node = n;
			
			String uri = getAttributeValue(getDocument(), node, "uri");
			if (uri != null) {
				p.setProperty("prefix", uri);
			}
		} catch (Exception x) {
			JSFExtensionsPlugin.log("", x);
		} finally {
			smw.dispose();
		}

		return p;
	}

	private String getAttributeValue (IDocument document, Node node, String attrName) {
		try {
			Attr attr = (Attr)node.getAttributes().getNamedItem(attrName);
			return Utils.getTrimmedValue(document, attr);
		} catch (Exception x) {
			JSFExtensionsPlugin.log("", x);
			return null;
		}
	}

	protected IRegion getRegion(final int offset) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		try {
			smw.init(getDocument());
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;
			
			Node n = Utils.findNodeForOffset(xmlDocument, offset);

			if (n == null || !(n instanceof Attr || n instanceof Node)) return null;
			
			if (n instanceof Attr) n = ((Attr)n).getOwnerElement();
			if ((n == null) || !(n instanceof Node)) return null;
			
			final int propStart = Utils.getValueStart(n);
			if(propStart < 0) return null;
			final int propLength = Utils.getValueEnd(n) - propStart;
			
			if (propStart > offset || propStart + propLength < offset) return null;
			
			IRegion region = new IRegion () {

				public int getLength() {
					return propLength;
				}

				public int getOffset() {
					return propStart;
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
			JSFExtensionsPlugin.log("", x);
			return null;
		} finally {
			smw.dispose();
		}
		
	}

}

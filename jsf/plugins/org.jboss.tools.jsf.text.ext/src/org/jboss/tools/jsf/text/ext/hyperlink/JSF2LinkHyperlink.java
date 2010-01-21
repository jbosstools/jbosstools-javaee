/*******************************************************************************
 * Copyright (c) 2007-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.text.ext.hyperlink;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.jboss.tools.common.text.ext.util.StructuredModelWrapper;
import org.jboss.tools.common.text.ext.util.Utils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Opens hyperlinks in the following form:
 * {@code <h:a_jsf_2_tag lib="a_lib_name" name="SELECTED_HYPERLINK">}.
 *
 * @see JSF2JSPLinkHyperlinkPartitioner
 * @see <a href="https://jira.jboss.org/jira/browse/JBIDE-5382">JBIDE-5382</a>
 * 
 * @author yradtsevich
 */
public class JSF2LinkHyperlink  extends JSFLinkHyperlink {
	private static final String JSF2_RESOURCES_FOLDER = "/resources"; //$NON-NLS-1$

	/* (non-Javadoc)
	 * @see org.jboss.tools.common.text.ext.hyperlink.LinkHyperlink#getFilePath(org.eclipse.jface.text.IRegion)
	 */
	@Override
	protected String getFilePath(IRegion region) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		smw.init(getDocument());
		try {
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) {
				return null;
			}
			
			Node n = Utils.findNodeForOffset(xmlDocument, region.getOffset());
			if (!(n instanceof Attr) ) {
				return null; 
			}
			
			Element element = ((Attr)n).getOwnerElement();
			Attr libraryAttr = element.getAttributeNode("library"); //$NON-NLS-1$
			String name = getDocument().get(region.getOffset(), region.getLength());
			if (libraryAttr != null && libraryAttr.getNodeValue() != null) {
				String library = libraryAttr.getNodeValue().trim();
				if (library.length() != 0) {
					return JSF2_RESOURCES_FOLDER + '/' + library + '/' + name;
				}
			}
			return JSF2_RESOURCES_FOLDER + '/' + name;
		} catch (BadLocationException x) {
			//ignore
			return null;
		} finally {
			smw.dispose();
		}
	}
}

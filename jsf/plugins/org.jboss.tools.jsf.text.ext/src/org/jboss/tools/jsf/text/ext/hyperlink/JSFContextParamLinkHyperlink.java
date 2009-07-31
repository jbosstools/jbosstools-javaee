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

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import org.jboss.tools.common.text.ext.util.StructuredModelWrapper;
import org.jboss.tools.common.text.ext.util.Utils;
import org.jboss.tools.jsf.text.ext.JSFExtensionsPlugin;

/**
 * @author Jeremy
 *
 */
public class JSFContextParamLinkHyperlink extends JSFLinkHyperlink {
	protected IRegion getRegion (int offset) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		smw.init(getDocument());
		try {
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;
			
			Node n = Utils.findNodeForOffset(xmlDocument, offset);
			
			if (n == null || !(n instanceof Text)) return null;

			int start = Utils.getValueStart(n);
			int end = Utils.getValueEnd(n);
			if (start < 0 || start > offset) return null;

			String attrText = getDocument().get(start, end - start);

			StringBuffer sb = new StringBuffer(attrText);
			//find start of bean property
			int bStart = offset - start;
			while (bStart >= 0) { 
				if (!Character.isJavaIdentifierPart(sb.charAt(bStart)) &&
						sb.charAt(bStart) != '\\' && sb.charAt(bStart) != '/' &&
						sb.charAt(bStart) != ':' && sb.charAt(bStart) != '-' &&
						sb.charAt(bStart) != '.' && sb.charAt(bStart) != '_' &&
						sb.charAt(bStart) != '%' && sb.charAt(bStart) != '?' &&
						sb.charAt(bStart) != '&' && sb.charAt(bStart) != '=') {
					bStart++;
					break;
				}
			
				if (bStart == 0) break;
				bStart--;
			}
			// find end of bean property
			int bEnd = offset - start;
			while (bEnd < sb.length()) { 
				if (!Character.isJavaIdentifierPart(sb.charAt(bEnd)) &&
						sb.charAt(bEnd) != '\\' && sb.charAt(bEnd) != '/' &&
						sb.charAt(bEnd) != ':' && sb.charAt(bEnd) != '-' &&
						sb.charAt(bEnd) != '.' && sb.charAt(bEnd) != '_' &&
						sb.charAt(bEnd) != '%' && sb.charAt(bEnd) != '?' &&
						sb.charAt(bEnd) != '&' && sb.charAt(bEnd) != '=') {
					break;
				}
				bEnd++;
			}
			
			final int propStart = bStart + start;
			final int propLength = bEnd - bStart;
			
			if (propStart > offset || propStart + propLength < offset) return null;

			return new Region(propStart,propLength);
		} catch (BadLocationException x) {
			JSFExtensionsPlugin.log("", x); //$NON-NLS-1$
			return null;
		} finally {
			smw.dispose();
		}
	}

}

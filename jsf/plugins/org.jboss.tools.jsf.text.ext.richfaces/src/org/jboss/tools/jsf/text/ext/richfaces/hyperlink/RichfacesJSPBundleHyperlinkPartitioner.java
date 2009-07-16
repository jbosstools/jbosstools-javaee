/*******************************************************************************
 * Copyright (c) 2009 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.jsf.text.ext.richfaces.hyperlink;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.text.IDocument;
import org.jboss.tools.common.text.ext.util.TaglibManagerWrapper;
import org.jboss.tools.jsf.text.ext.hyperlink.JSPBundleHyperlinkPartitioner;
import org.jboss.tools.jsf.text.ext.hyperlink.JsfJSPBundleHyperlinkPartitioner;

/**
 * 
 * @author Victor Rubezhny
 *
 */
public class RichfacesJSPBundleHyperlinkPartitioner extends JSPBundleHyperlinkPartitioner {
	public static final String JSP_RICHFACES_BUNDLE_PARTITION = "org.jboss.tools.common.text.ext.jsp.JSP_RICHFACES_BUNDLE";
	
	public final static String[] LoadBundleURIs = {
		"http://richfaces.org/a4j",
		"https://ajax4jsf.dev.java.net/ajax"	
	};

	/**
	 * @Override 
	 */
	protected String getPartitionType() {
		return JSP_RICHFACES_BUNDLE_PARTITION;
	}

	/**
	 * @Override 
	 */
	protected String[] getLoadBundleTagPrefixes(IDocument document, int offset) {
		ArrayList<String> prefixes = new ArrayList<String>();
		TaglibManagerWrapper tmw = new TaglibManagerWrapper();
		tmw.init(document, offset);
		if(tmw.exists()) {
			for (String uri : LoadBundleURIs) {
				String prefix = tmw.getPrefix(uri);
				if (prefix != null)
					prefixes.add(prefix);
			}
		}
		// JBIDE-4559: For XHTML pages we should use alternate way to get the prefixes
		Map<String, Set<String>> namespaces = JsfJSPBundleHyperlinkPartitioner.getNameSpaces(document, offset);
		
		if (namespaces != null) {
			for (String uri : LoadBundleURIs) {
				Set<String> altPrefixes = namespaces.get(uri);
				if (altPrefixes != null) 
					prefixes.addAll(altPrefixes);
			}
		}
		
		return (prefixes.size() == 0 ? null : (String[])prefixes.toArray(new String[prefixes.size()]));
	}

}

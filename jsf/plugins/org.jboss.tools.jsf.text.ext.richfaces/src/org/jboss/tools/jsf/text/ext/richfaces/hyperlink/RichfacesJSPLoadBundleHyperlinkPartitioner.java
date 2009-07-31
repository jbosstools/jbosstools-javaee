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

import org.eclipse.jface.text.IDocument;
import org.jboss.tools.common.text.ext.util.TaglibManagerWrapper;
import org.jboss.tools.jsf.text.ext.hyperlink.JSPLoadBundleHyperlinkPartitioner;

/**
 * 
 * @author Victor Rubezhny
 *
 */
public class RichfacesJSPLoadBundleHyperlinkPartitioner extends
		JSPLoadBundleHyperlinkPartitioner {

	public static final String JSP_RICHFACES_LOADBUNDLE_PARTITION = "org.jboss.tools.common.text.ext.jsp.JSP_RICHFACES_LOADBUNDLE"; //$NON-NLS-1$
	
	/**
	 * @Override 
	 */
	protected String getPartitionType() {
		return JSP_RICHFACES_LOADBUNDLE_PARTITION;
	}

	/**
	 * @Override 
	 */
	protected String[] getLoadBundleTagPrefixes(IDocument document, int offset) {
		TaglibManagerWrapper tmw = new TaglibManagerWrapper();
		tmw.init(document, offset);
		if(!tmw.exists()) return null;

		ArrayList<String> prefixes = new ArrayList<String>();
		for (String uri : RichfacesJSPBundleHyperlinkPartitioner.LoadBundleURIs) {
			String prefix = tmw.getPrefix(uri);
			if (prefix != null)
				prefixes.add(prefix);
		}
		return (String[])prefixes.toArray(new String[prefixes.size()]);
	}

}

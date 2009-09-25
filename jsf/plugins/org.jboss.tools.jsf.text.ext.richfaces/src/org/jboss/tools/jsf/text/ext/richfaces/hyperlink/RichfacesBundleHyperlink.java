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

import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.IEditorPart;
import org.jboss.tools.jst.text.ext.util.TaglibManagerWrapper;
import org.jboss.tools.jsf.text.ext.hyperlink.BundleHyperlink;
import org.jboss.tools.jsf.text.ext.richfaces.RichfacesExtensionsPlugin;
import org.jboss.tools.jst.web.tld.TaglibData;
import org.jboss.tools.jst.web.tld.VpeTaglibManager;
import org.jboss.tools.jst.web.tld.VpeTaglibManagerProvider;

/**
 * 
 * @author Victor Rubezhny
 *
 */
public class RichfacesBundleHyperlink extends BundleHyperlink {

	protected String[] getLoadBundleTagPrefixes(IRegion region) {
		ArrayList<String> prefixes = new ArrayList<String>();
		TaglibManagerWrapper tmw = new TaglibManagerWrapper();
		tmw.init(getDocument(), region.getOffset());
		if(tmw.exists()) {
			for (String uri : RichfacesJSPBundleHyperlinkPartitioner.LoadBundleURIs) {
				String prefix = tmw.getPrefix(uri);
				if (prefix != null)
					prefixes.add(prefix);
			}
		} else {
			VpeTaglibManager taglibManager = getTaglibManager();
			if(taglibManager == null) return null;
			TaglibData[] data = (TaglibData[])taglibManager.getTagLibs().toArray(new TaglibData[0]);
			for (int i = 0; i < data.length; i++) {
				for (String uri : RichfacesJSPBundleHyperlinkPartitioner.LoadBundleURIs) {
					if(uri.equals(data[i].getUri())) 
						prefixes.add(data[i].getPrefix());
				}
			}			
		}		
		return (String[])prefixes.toArray(new String[prefixes.size()]);
	}

	private VpeTaglibManager getTaglibManager() {
		IEditorPart editor = RichfacesExtensionsPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if(editor instanceof VpeTaglibManagerProvider) {
			return ((VpeTaglibManagerProvider)editor).getTaglibManager();
		}
		return null;
	}

}

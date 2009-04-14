package org.jboss.tools.jsf.text.ext.richfaces.hyperlink;

import java.util.ArrayList;

import org.eclipse.jface.text.IDocument;
import org.jboss.tools.common.text.ext.util.TaglibManagerWrapper;
import org.jboss.tools.jsf.text.ext.hyperlink.JSPBundleHyperlinkPartitioner;

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
		TaglibManagerWrapper tmw = new TaglibManagerWrapper();
		tmw.init(document, offset);
		if(!tmw.exists()) return null;

		ArrayList<String> prefixes = new ArrayList<String>();
		for (String uri : LoadBundleURIs) {
			String prefix = tmw.getPrefix(uri);
			if (prefix != null)
				prefixes.add(prefix);
		}
		return (String[])prefixes.toArray(new String[prefixes.size()]);
	}

}

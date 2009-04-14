package org.jboss.tools.jsf.text.ext.richfaces.hyperlink;

import java.util.ArrayList;

import org.eclipse.jface.text.IDocument;
import org.jboss.tools.common.text.ext.util.TaglibManagerWrapper;
import org.jboss.tools.jsf.text.ext.hyperlink.JSPLoadBundleHyperlinkPartitioner;


public class RichfacesJSPLoadBundleHyperlinkPartitioner extends
		JSPLoadBundleHyperlinkPartitioner {

	public static final String JSP_RICHFACES_LOADBUNDLE_PARTITION = "org.jboss.tools.common.text.ext.jsp.JSP_RICHFACES_LOADBUNDLE";
	
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

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
package org.jboss.tools.struts.text.ext.hyperlink;

import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.text.ext.hyperlink.AbstractHyperlink;
import org.jboss.tools.common.text.ext.util.Utils;
import org.jboss.tools.jst.web.project.list.WebPromptingProvider;
import org.jboss.tools.struts.text.ext.StrutsExtensionsPlugin;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;

/**
 * @author Jeremy
 */
public abstract class StrutsXModelBasedHyperlink extends AbstractHyperlink {

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

		List<Object> list = provider.getList(xModel, getRequestMethod(), p.getProperty("prefix"), p);
		if (list != null && list.size() >= 1) {
			openFileInEditor((String)list.get(0));
			return;
		}
		String error = p.getProperty(WebPromptingProvider.ERROR); 
		if ( error != null && error.length() > 0) {
			openFileFailed();
		}
	}
	
	protected abstract String getRequestMethod();

	protected abstract Properties getRequestProperties(IRegion region);
	
	protected String getAttributeValue (IDocument document, Node node, String attrName) {
		if(document == null || node == null || attrName == null) return null;
		try {
			Attr attr = (Attr)node.getAttributes().getNamedItem(attrName);
			return Utils.getTrimmedValue(document, attr);
		} catch (BadLocationException x) {
			StrutsExtensionsPlugin.getPluginLog().logError(x);
			return null;
		}
	}
}

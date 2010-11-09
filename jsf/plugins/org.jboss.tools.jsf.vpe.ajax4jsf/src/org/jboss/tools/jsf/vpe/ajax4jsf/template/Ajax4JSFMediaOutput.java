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
package org.jboss.tools.jsf.vpe.ajax4jsf.template;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.jboss.tools.jsf.vpe.ajax4jsf.Activator;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.editor.util.VisualDomUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * The template for {@code <a4j:mediaOutput>} tag.
 *
 * @author dmaliarevich
 * @author dsakovich
 * @author yradtsevich
 */
public class Ajax4JSFMediaOutput extends VpeAbstractTemplate {
    private static final String IMG_PATH = "mediaOutput/mediaOutput.jpg"; //$NON-NLS-1$
    
    private static final List<String> SAME_ATTRIBUTES_LIST;
    static {
    	String []sameAttributes = {HTML.ATTR_ALIGN, HTML.ATTR_BORDER, HTML.ATTR_DIR,
    		HTML.ATTR_HSPACE, HTML.ATTR_ID,  HTML.ATTR_STYLE, HTML.ATTR_VSPACE};
    	for (int i = 0; i < sameAttributes.length; i++) {
    		sameAttributes[i] = sameAttributes[i].toLowerCase();
    	}
    	SAME_ATTRIBUTES_LIST = Arrays.asList(sameAttributes);
    }

	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {

		Element sourceElement = (Element) sourceNode;
		nsIDOMElement mainTag = createMainTag(visualDocument, sourceElement);

		String uriAttribute = sourceElement.hasAttribute(Ajax4JSF.ATTR_URI_ATTRIBUTE) ? 
				sourceElement.getAttribute(Ajax4JSF.ATTR_URI_ATTRIBUTE) : HTML.ATTR_SRC;
		
		mainTag.setAttribute(uriAttribute, "file:///" + getAbsoluteResourcePath(IMG_PATH).replace('\\', '/')); //$NON-NLS-1$

		VisualDomUtil.copyAttributes(sourceElement, mainTag, SAME_ATTRIBUTES_LIST);
		
		if (sourceElement.hasAttribute(Ajax4JSF.ATTR_STYLE_CLASS)) {
			String styleClass = sourceElement.getAttribute(Ajax4JSF.ATTR_STYLE_CLASS);
			mainTag.setAttribute(HTML.ATTR_CLASS, styleClass);
		}

		return new VpeCreationData(mainTag);
	}

	public static String getAbsoluteResourcePath(String resourcePathInPlugin) {
		String pluginPath = Activator.getPluginResourcePath();
		IPath pluginFile = new Path(pluginPath);
		File file = pluginFile.append(resourcePathInPlugin).toFile();
		if (file.exists()) {
			return file.getAbsolutePath();
		} else {
			throw new RuntimeException("Can't get path for " + resourcePathInPlugin);  //$NON-NLS-1$
		}
	}

	/**
	 * Creates HTML element with tag-name specified in {@code element} attribute of
	 * {@code sourceElement}, or if the attribute is not present {@code 'img'} is used.
	 */
	private static nsIDOMElement createMainTag(nsIDOMDocument visualDocument,
			Element sourceElement) {
		
		String element = sourceElement.hasAttribute(Ajax4JSF.ATTR_ELEMENT) ? 
				sourceElement.getAttribute(Ajax4JSF.ATTR_ELEMENT) : HTML.TAG_IMG;

		nsIDOMElement mainTag = visualDocument.createElement(element);
		return mainTag;
	}
}

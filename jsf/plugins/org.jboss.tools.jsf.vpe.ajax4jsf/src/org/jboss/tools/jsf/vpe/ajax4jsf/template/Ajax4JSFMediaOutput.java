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

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.jboss.tools.jsf.vpe.ajax4jsf.Activator;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class Ajax4JSFMediaOutput extends VpeAbstractTemplate {

	public static final String ALT_MEDIA_OTPUT = "	mediaOutput";

	public static final String HTML_TAG_DIV = "DIV";
	public static final String HTML_TAG_IMG = "IMG";
	
    public static final String ATTR_WIDTH = "WIDTH";
    public static final String ATTR_HEIGHT = "HEIGHT";
    public static final String ATTR_SRC = "src";
    public static final String ATTR_ALT = "alt";
	
    public static final String IMG_PATH = "mediaOutput/mediaOutput.jpg";
	
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {
		
		nsIDOMElement mainDiv = visualDocument
			.createElement(HTML_TAG_DIV);
		nsIDOMElement img = visualDocument
			.createElement(HTML_TAG_IMG);
		
		img.setAttribute(ATTR_SRC, "file:///" + getAbsoluteResourcePath(IMG_PATH).replace('\\', '/'));
		img.setAttribute(ATTR_ALT, ALT_MEDIA_OTPUT);
		img.setAttribute(ATTR_WIDTH, "100");
		img.setAttribute(ATTR_HEIGHT, "50");
		
		mainDiv.appendChild(img);
		
		return new VpeCreationData(mainDiv);
	}
	
	public boolean isRecreateAtAttrChange(VpePageContext pageContext,
			Element sourceElement, nsIDOMDocument visualDocument,
			nsIDOMElement visualNode, Object data, String name, String value) {
		return true;
	}
	
	public static String getAbsoluteResourcePath(String resourcePathInPlugin) {
		String pluginPath = Activator.getPluginResourcePath();
		IPath pluginFile = new Path(pluginPath);
		File file = pluginFile.append(resourcePathInPlugin).toFile();
		if (file.exists()) {
			return file.getAbsolutePath();
		} else {
			throw new RuntimeException("Can't get path for "
					+ resourcePathInPlugin);
		}
	}
	
}

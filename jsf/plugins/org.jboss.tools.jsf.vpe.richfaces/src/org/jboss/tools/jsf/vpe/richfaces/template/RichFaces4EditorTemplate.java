/*******************************************************************************
  * Copyright (c) 2007-2011 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.jsf.vpe.richfaces.template;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.Constants;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.editor.util.VpeStyleUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class RichFaces4EditorTemplate extends VpeAbstractTemplate {
	
	private static final String STYLE_PATH = "editor/editor4.css"; //$NON-NLS-1$
	
	private static final String CSS_CKE_SKIN_RF_CLASS = "cke_skin_richfaces"; //$NON-NLS-1$
	private static final String CSS_CKE_WRAPPER_CLASS = "cke_wrapper"; //$NON-NLS-1$
	private static final String CSS_CKE_EDITOR_CLASS = "cke_editor"; //$NON-NLS-1$
	private static final String CSS_CKE_TOP_CLASS = "cke_top"; //$NON-NLS-1$
	private static final String CSS_CKE_CONTENTS_CLASS = "cke_contents"; //$NON-NLS-1$
	private static final String CSS_CKE_BOTTOM_CLASS = "cke_bottom"; //$NON-NLS-1$
	private static final String CSS_CKE_TOOLBOX_CLASS = "cke_toolbox"; //$NON-NLS-1$
	private static final String CSS_CKE_TOOLBOX_COLLAPSER_CLASS = "cke_toolbox_collapser"; //$NON-NLS-1$
	private static final String CSS_CKE_RESIZER_CLASS = "cke_resizer"; //$NON-NLS-1$
	private static final String CSS_CKE_PATH_CLASS = "cke_path"; //$NON-NLS-1$
	private static final String CSS_CKE_TOOLBAR_CLASS = "cke_toolbar"; //$NON-NLS-1$
	private static final String CSS_CKE_TOOLGROUP_CLASS = "cke_toolgroup"; //$NON-NLS-1$
	private static final String CSS_TOOLBAR_IMAGE_CLASS = "jbds_rf_editor_toolbar_image_internal_css_class"; //$NON-NLS-1$
	private static final String CSS_TEXTAREA_CLASS = "jbds_rf_editor_content_textarea_internal_css_class"; //$NON-NLS-1$
	
	@Override
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {
		Element sourceElement = (Element) sourceNode;
		/*
		 * Add required css file 
		 */
		ComponentUtil.setCSSLink(pageContext, STYLE_PATH, "editor4"); //$NON-NLS-1$
		
		nsIDOMElement topSpan = visualDocument.createElement(HTML.TAG_SPAN); 
		nsIDOMElement spanWrapper = visualDocument.createElement(HTML.TAG_SPAN); 
		nsIDOMElement table = visualDocument.createElement(HTML.TAG_TABLE); 
		nsIDOMElement tbody = visualDocument.createElement(HTML.TAG_TBODY); 
		nsIDOMElement tr1 = visualDocument.createElement(HTML.TAG_TR); 
		nsIDOMElement tr2 = visualDocument.createElement(HTML.TAG_TR); 
		nsIDOMElement tr3 = visualDocument.createElement(HTML.TAG_TR); 
		nsIDOMElement td1 = visualDocument.createElement(HTML.TAG_TD); 
		nsIDOMElement td2 = visualDocument.createElement(HTML.TAG_TD); 
		nsIDOMElement td3 = visualDocument.createElement(HTML.TAG_TD); 
		/*
		 * Process Source Element attributes
		 */
		if (sourceElement.hasAttribute(HTML.ATTR_WIDTH)) {
			topSpan.setAttribute(HTML.ATTR_STYLE, HTML.ATTR_WIDTH + Constants.COLON +
					VpeStyleUtil.addPxIfNecessary(sourceElement.getAttribute(HTML.ATTR_WIDTH)));
		}
		if (sourceElement.hasAttribute(HTML.ATTR_HEIGHT)) {
			td2.setAttribute(HTML.ATTR_STYLE, HTML.ATTR_HEIGHT + Constants.COLON +
				VpeStyleUtil.addPxIfNecessary(sourceElement.getAttribute(HTML.ATTR_HEIGHT)));
		}
		/*
		 * TOP ROW
		 */
		nsIDOMElement divToolbox = visualDocument.createElement(HTML.TAG_DIV); 
		nsIDOMElement a = visualDocument.createElement(HTML.TAG_SPAN); 
		
		nsIDOMElement span1 = visualDocument.createElement(HTML.TAG_SPAN); 
		nsIDOMElement span2 = visualDocument.createElement(HTML.TAG_SPAN); 
		nsIDOMElement span3img = visualDocument.createElement(HTML.TAG_SPAN); 
		span1.setAttribute(HTML.ATTR_CLASS, CSS_CKE_TOOLBAR_CLASS);
		span2.setAttribute(HTML.ATTR_CLASS, CSS_CKE_TOOLGROUP_CLASS);
		span3img.setAttribute(HTML.ATTR_CLASS, CSS_TOOLBAR_IMAGE_CLASS);
		span2.appendChild(span3img);
		span1.appendChild(span2);
		divToolbox.appendChild(span1);
		divToolbox.appendChild(a);
		/*
		 * MIDDLE TEXT AREA ROW
		 */
		nsIDOMElement textArea = visualDocument.createElement(HTML.TAG_TEXTAREA); 
		/*
		 * BOTTOM ROW
		 */
		nsIDOMElement divResizer = visualDocument.createElement(HTML.TAG_DIV); 
		nsIDOMElement divPath = visualDocument.createElement(HTML.TAG_DIV); 
		/*
		 * Add css style classes
		 */
		topSpan.setAttribute(HTML.ATTR_CLASS, CSS_CKE_SKIN_RF_CLASS);
		spanWrapper.setAttribute(HTML.ATTR_CLASS, CSS_CKE_WRAPPER_CLASS);
		table.setAttribute(HTML.ATTR_CLASS, CSS_CKE_EDITOR_CLASS);
		td1.setAttribute(HTML.ATTR_CLASS, CSS_CKE_TOP_CLASS);
		td2.setAttribute(HTML.ATTR_CLASS, CSS_CKE_CONTENTS_CLASS);
		td3.setAttribute(HTML.ATTR_CLASS, CSS_CKE_BOTTOM_CLASS);
		divToolbox.setAttribute(HTML.ATTR_CLASS, CSS_CKE_TOOLBOX_CLASS);
		a.setAttribute(HTML.ATTR_CLASS, CSS_CKE_TOOLBOX_COLLAPSER_CLASS);
		textArea.setAttribute(HTML.ATTR_CLASS, CSS_TEXTAREA_CLASS);
		divResizer.setAttribute(HTML.ATTR_CLASS, CSS_CKE_RESIZER_CLASS);
		divPath.setAttribute(HTML.ATTR_CLASS, CSS_CKE_PATH_CLASS);
		/*
		 * Nesting the elements
		 */
		td1.appendChild(divToolbox);
		td1.appendChild(a);
		td2.appendChild(textArea);
		td3.appendChild(divResizer);
		td3.appendChild(divPath);
		tr1.appendChild(td1);
		tr2.appendChild(td2);
		tr3.appendChild(td3);
		tbody.appendChild(tr1);
		tbody.appendChild(tr2);
		tbody.appendChild(tr3);
		table.appendChild(tbody);
		spanWrapper.appendChild(table);
		topSpan.appendChild(spanWrapper);
		/*
		 * Create VpeCreationData
		 */
		VpeCreationData creationData = new VpeCreationData(topSpan);
		return creationData;
	}
}
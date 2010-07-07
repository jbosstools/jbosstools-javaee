/*******************************************************************************
  * Copyright (c) 2007-2008 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.jsf.vpe.ajax4jsf.template;

import org.jboss.tools.jsf.vpe.jsf.template.util.model.VpeElementProxyData;
import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.template.util.RichFaces;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.mapping.AttributeData;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.editor.util.VisualDomUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMText;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author yradtsevich
 *
 */
public class Ajax4JSFStatusTemplate extends VpeAbstractTemplate {

	/*
	 *<vpe:tag name="a4j:status" case-sensitive="yes">
	 *	<vpe:template children="yes" modify="yes">
	 *		<span class="{@styleClass};{@stopStyleClass}" style="{@style};{@stopStyle}" title="{tagstring()}">
	 *			<vpe:value expr=" {jsfvalue(@stopText)}"/>
	 *		</span>
	 *	</vpe:template>
	 *</vpe:tag>
	 */
	
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {
		Creator creator = new Creator(pageContext, (Element)sourceNode, visualDocument);
		return creator.create();
	}
	
	private static class Creator {
		private static final String ATTR_STOP_TEXT = "stopText";//$NON-NLS-1$
		private static final String STOP_FACET_NAME = "stop";//$NON-NLS-1$
		private static final String START_FACET_NAME = "start";//$NON-NLS-1$
		private static final String ATTR_STOP_STYLE_CLASS = "stopStyleClass"; //$NON-NLS-1$
		private static final String ATTR_STOP_STYLE = "stopStyle"; //$NON-NLS-1$
		private final VpePageContext pageContext;
		private final Element sourceElement;
		private final nsIDOMDocument visualDocument;
		private VpeCreationData vpeCreationData;

		public Creator(VpePageContext pageContext, Element sourceElement,
				nsIDOMDocument visualDocument) {
			this.pageContext = pageContext;
			this.sourceElement = sourceElement;
			this.visualDocument = visualDocument;
		}


		public VpeCreationData create() {
			nsIDOMElement mainElement = createMainElement();			
			vpeCreationData = new VpeCreationData(mainElement);
			final VpeChildrenInfo childrenInfo = new VpeChildrenInfo(mainElement);
			vpeCreationData.addChildrenInfo(childrenInfo);

			Element stopFacet = ComponentUtil.getFacet(sourceElement, STOP_FACET_NAME);
			if (stopFacet != null) {
				childrenInfo.addSourceChild(stopFacet);
			} else {
				nsIDOMElement stopTextElement = createStopTextElement();
				if (stopTextElement != null) {
					mainElement.appendChild(createStopTextElement());
				}
			}

			return vpeCreationData;
		}
		
		private nsIDOMElement createMainElement() {
			nsIDOMElement span = visualDocument.createElement(HTML.TAG_SPAN);
			
			// set class of the span to "{@styleClass} {@stopStyleClass}"
			{
				StringBuffer spanClass = new StringBuffer();
				if (sourceElement.hasAttribute(RichFaces.ATTR_STYLE_CLASS)) {
					String styleClass = sourceElement.getAttribute(RichFaces.ATTR_STYLE_CLASS);
					spanClass.append(styleClass);
				}
				if (sourceElement.hasAttribute(ATTR_STOP_STYLE_CLASS)) {
					String stopStyleClass = sourceElement.getAttribute(ATTR_STOP_STYLE_CLASS);
					spanClass.append(HTML.VALUE_CLASS_DELIMITER).append(stopStyleClass);
				}
				span.setAttribute(HTML.ATTR_CLASS, spanClass.toString());
			}

			// set style of the span to "{@style};{@stopStyle}"
			{
				StringBuffer spanStyle = new StringBuffer();
				if (sourceElement.hasAttribute(RichFaces.ATTR_STYLE)) {
					String style = sourceElement.getAttribute(RichFaces.ATTR_STYLE);
					spanStyle.append(style).append(HTML.VALUE_STYLE_DELIMITER); 
				}
				if (sourceElement.hasAttribute(ATTR_STOP_STYLE)) {
					String stopStyle = sourceElement.getAttribute(ATTR_STOP_STYLE);
					spanStyle.append(stopStyle).append(HTML.VALUE_STYLE_DELIMITER);
				}
				span.setAttribute(HTML.ATTR_STYLE, spanStyle.toString());
			}
			return span;
		}
		
		/**
		 * Creates new {@code nsIDOMElement} and inserts text of {@code 'stopText'} attribute
		 * into this element. Also it registers created element in {@link #vpeCreationData}
		 * 
		 * @return created {@code nsIDOMElement} or {@code null} if {@link #sourceElement}
		 * does not have {@code 'stopText'} attribute
		 */
		private nsIDOMElement createStopTextElement() {
			nsIDOMElement visualElement = null;
			
			Attr stopTextAttribute = sourceElement.getAttributeNode(ATTR_STOP_TEXT);
			if (stopTextAttribute != null) {
				visualElement = VisualDomUtil.createBorderlessContainer(visualDocument);
				nsIDOMText textNode = visualDocument.createTextNode(
						stopTextAttribute.getNodeValue());
				visualElement.appendChild(textNode);
				// add attribute for ability of editing
				VpeElementProxyData elementData = new VpeElementProxyData();
				elementData.addNodeData(new AttributeData(stopTextAttribute, textNode, true));
				vpeCreationData.setElementData(elementData);
			}
			
			return visualElement;
		}
		
		/**
		 * Creates new object of {@link VpeChildrenInfo}, initializes it,
		 * and adds it to {@link #vpeCreationData}
		 * 
		 * @param visualParent visual parent for {@code VpeChildrenInfo}
		 */
		private void addChildrenInfo(nsIDOMElement visualParent) {
			final VpeChildrenInfo childrenInfo = new VpeChildrenInfo(visualParent);
			vpeCreationData.addChildrenInfo(childrenInfo);
			NodeList children = sourceElement.getChildNodes();
			int childrenLength = children.getLength();
			for (int i = 0; i < childrenLength; i++) {
				Node child = children.item(i);
				if (child.getNodeType() == Node.ELEMENT_NODE
						&& child.getNodeName().endsWith(RichFaces.TAG_FACET)) {
					Element facet = (Element)child;
					String facetName = facet.getAttribute(RichFaces.ATTR_NAME);
					if (START_FACET_NAME.equalsIgnoreCase(facetName)) {
						// just skip
					} else if (STOP_FACET_NAME.equalsIgnoreCase(facetName)) {
						childrenInfo.addSourceChild(facet);
					}
				}
			}
		}
	}
}

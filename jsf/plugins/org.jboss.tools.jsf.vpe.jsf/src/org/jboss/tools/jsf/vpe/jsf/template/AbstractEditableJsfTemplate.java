/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.jsf.template;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.mapping.VpeElementMapping;
import org.jboss.tools.vpe.editor.mapping.VpeNodeMapping;
import org.jboss.tools.vpe.editor.template.EditableTemplateAdapter;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.editor.util.TemplateManagingUtil;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMKeyEvent;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsISelection;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * general class for jsf templates
 * 
 * @author Sergey Dzmitrovich
 * 
 */
public abstract class AbstractEditableJsfTemplate extends EditableTemplateAdapter {

	// general jsf attributes
	static private Map<String, String> attributes = new HashMap<String, String>();

	static {
		attributes.put("style", HTML.ATTR_STYLE); //$NON-NLS-1$
		attributes.put("styleClass", HTML.ATTR_CLASS); //$NON-NLS-1$
	}

	/**
	 * copy general
	 * 
	 * @param visualElement
	 * @param sourceElement
	 */
	protected void copyGeneralJsfAttributes(nsIDOMElement visualElement,
			Element sourceElement) {

		Set<String> jsfAttributes = attributes.keySet();

		for (String key : jsfAttributes) {

			copyAttribute(visualElement, sourceElement, key, attributes
					.get(key));
		}

	}

	/**
	 * copy attribute
	 * 
	 * @param visualElement
	 * @param sourceElement
	 * @param sourceAttributeName
	 * @param targetAtttributeName
	 */
	protected void copyAttribute(nsIDOMElement visualElement,
			Element sourceElement, String sourceAttributeName,
			String targetAtttributeName) {

		if (sourceElement.hasAttribute(sourceAttributeName))
			visualElement.setAttribute(targetAtttributeName, sourceElement
					.getAttribute(sourceAttributeName));

	}

	protected boolean handleCharacter(VpePageContext pageContext,
			nsIDOMKeyEvent keyEvent) {

		// get selection
		nsISelection selection = getCurrentSelection(pageContext);

		// get visual node which is focused
		nsIDOMNode visualNode = selection.getFocusNode();

		VpeElementMapping elementMapping = getElmentMapping(pageContext,
				visualNode);
		if (elementMapping != null)
			return super.handleCharacter(pageContext, keyEvent);
		else
			return true;
	}

	protected boolean handleLeftDelete(VpePageContext pageContext,
			nsIDOMKeyEvent keyEvent) {

		// get selection
		nsISelection selection = getCurrentSelection(pageContext);

		// get visual node which is focused
		nsIDOMNode visualNode = selection.getFocusNode();

		VpeElementMapping elementMapping = getElmentMapping(pageContext,
				visualNode);
		if (elementMapping != null)
			return super.handleLeftDelete(pageContext, keyEvent);
		else
			return true;

	}

	protected boolean handleRightDelete(VpePageContext pageContext,
			nsIDOMKeyEvent keyEvent) {

		// get selection
		nsISelection selection = getCurrentSelection(pageContext);

		// get visual node which is focused
		nsIDOMNode visualNode = selection.getFocusNode();

		VpeElementMapping elementMapping = getElmentMapping(pageContext,
				visualNode);
		if (elementMapping != null)
			return super.handleRightDelete(pageContext, keyEvent);
		else
			return true;
	}

	@Override
	public void setSelection(VpePageContext pageContext, nsISelection selection) {

		nsIDOMNode focusedVisualNode = selection.getFocusNode();

		VpeElementMapping elementMapping = pageContext.getDomMapping()
				.getNearElementMapping(focusedVisualNode);

		if (elementMapping != null)
			super.setSelection(pageContext, selection);
		else {

			VpeNodeMapping insertedMapping = pageContext.getDomMapping()
					.getNearNodeMappingAtVisualNode(focusedVisualNode);

			if (insertedMapping != null) {

				Node insertedNode = insertedMapping.getSourceNode();

				int offset = ((IDOMNode) insertedNode).getStartOffset();

				Node realNode = TemplateManagingUtil.getSourceNodeByPosition(
						pageContext, offset);

				VpeElementMapping mappingRealNode = pageContext.getDomMapping()
						.getNearElementMapping(realNode);

				if (mappingRealNode != null) {

					Node focusedNode = getFocusedNode(realNode, mappingRealNode
							.getElementData(), offset);

					setSourceSelection(pageContext, focusedNode, 0,
							getLengthNode(focusedNode));

					pageContext.getVisualBuilder().setSelectionRectangle(
							(nsIDOMElement) mappingRealNode.getVisualElement()
									.queryInterface(
											nsIDOMElement.NS_IDOMELEMENT_IID));
				}

			}
		}
	}
}

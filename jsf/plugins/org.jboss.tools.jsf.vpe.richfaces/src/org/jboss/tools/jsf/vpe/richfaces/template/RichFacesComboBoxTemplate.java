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
package org.jboss.tools.jsf.vpe.richfaces.template;

import org.jboss.tools.vpe.editor.VpeVisualDomBuilder;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.template.VpeToggableTemplate;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * The Class RichFacesComboBoxTemplate.
 *
 * @author Eugene Stherbin
 */
public class RichFacesComboBoxTemplate extends AbstractEditableRichFacesTemplate implements VpeToggableTemplate {

	/**
	 * @see org.jboss.tools.vpe.editor.template.VpeToggableTemplate#stopToggling(org.w3c.dom.Node)
	 */
	public void stopToggling(final Node sourceNode) {		
	}

	/**
	 * @see org.jboss.tools.vpe.editor.template.VpeToggableTemplate#toggle(org.jboss.tools.vpe.editor.VpeVisualDomBuilder, org.w3c.dom.Node, java.lang.String)
	 * @see RichFacesComboBoxTemplateHelper#toggle(VpeVisualDomBuilder, Node, String)
	 */
	public void toggle(final VpeVisualDomBuilder builder, final Node sourceNode, final String toggleId) {
		RichFacesComboBoxTemplateHelper.toggle(builder, sourceNode, toggleId);
	}

	/**
	 * @see org.jboss.tools.vpe.editor.template.VpeTemplate#create(org.jboss.tools.vpe.editor.context.VpePageContext, org.w3c.dom.Node, org.mozilla.interfaces.nsIDOMDocument)
	 * @see RichFacesComboBoxTemplateHelper
	 */
	public VpeCreationData create(final VpePageContext pageContext, final Node sourceNode,
			final nsIDOMDocument visualDocument) {	
		final RichFacesComboBoxTemplateHelper creator = 
			new RichFacesComboBoxTemplateHelper(pageContext, sourceNode, visualDocument);
		
		return creator.getVpeCreationData();
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.vpe.editor.template.VpeAbstractTemplate#isRecreateAtAttrChange(org.jboss.tools.vpe.editor.context.VpePageContext, org.w3c.dom.Element, org.mozilla.interfaces.nsIDOMDocument, org.mozilla.interfaces.nsIDOMElement, java.lang.Object, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean recreateAtAttrChange(VpePageContext pageContext,
			Element sourceElement, nsIDOMDocument visualDocument,
			nsIDOMElement visualNode, Object data, String name, String value) {
		return true;
	}
}

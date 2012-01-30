/*******************************************************************************
 * Copyright (c) 2009-2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.seam.text.ext.hyperlink;

import org.eclipse.jface.text.IDocument;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.jboss.tools.common.text.ext.hyperlink.AbstractHyperlinkPartitioner;
import org.jboss.tools.common.text.ext.hyperlink.HyperlinkRegion;
import org.jboss.tools.common.text.ext.hyperlink.IHyperlinkRegion;
import org.jboss.tools.common.text.ext.util.StructuredModelWrapper;
import org.jboss.tools.common.text.ext.util.Utils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class ComponentsHyperlinkPartitioner extends
		AbstractHyperlinkPartitioner {
	public static final String BPM_DEFINITION_PARTITION = "org.jboss.tools.seam.text.ext.BPM_DEFINITION";
	public static final String DROOLS_RULE_PARTITION = "org.jboss.tools.seam.text.ext.DROOLS_RULE_DEFINITION";

	static final String textNodeName = "#text";
	static final String valueNodeName = "value";

	static final String processDefinitionsNodeName = "bpm:process-definitions";
	static final String pageflowDefinitionsNodeName = "bpm:pageflow-definitions";
	
	static final String propertyNodeName = "property";
	static final String propertyAttributeName = "name";
	
	static final String propertyAtt1 = "processDefinitions";
	static final String propertyAtt2 = "pageflowDefinitions";

	static final String droolsRuleFileNodeName = "drools:rule-files";

	public static Node getNode(IDocument document, int superOffset) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		try {
			smw.init(document);
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null)
				return null;

			Node node = Utils.findNodeForOffset(xmlDocument, superOffset); // #text

			return node;
		} finally {
			smw.dispose();
		}
	}

	public static String getType(Node node) {
		Node valueNode = node.getParentNode(); // value node
		if (valueNode == null)
			return null;

		Node parentNode = valueNode.getParentNode(); // parent node
		if (parentNode == null)
			return null;

		if (node.getNodeName().equalsIgnoreCase(textNodeName)
				&& valueNode.getNodeName().equalsIgnoreCase(valueNodeName)) {
			if (parentNode.getNodeName().equalsIgnoreCase(
					processDefinitionsNodeName)
					|| parentNode.getNodeName().equalsIgnoreCase(
							pageflowDefinitionsNodeName)) {
				return BPM_DEFINITION_PARTITION;
			} else if (parentNode.getNodeName().equalsIgnoreCase(
					droolsRuleFileNodeName)) {
				return DROOLS_RULE_PARTITION;
			} else if(parentNode.getNodeName().equalsIgnoreCase(
					propertyNodeName)) {
				Node attribute = parentNode.getAttributes().getNamedItem(propertyAttributeName);
				if(attribute != null){
					if(attribute.getNodeValue().equalsIgnoreCase(propertyAtt1)
							|| attribute.getNodeValue().equalsIgnoreCase(propertyAtt2)){
						return BPM_DEFINITION_PARTITION;
					}
				}
			}
		}

		return null;
	}

	@Override
	protected IHyperlinkRegion parse(IDocument document, int offset,
			IHyperlinkRegion superRegion) {
		Node node = getNode(document, offset);
		String type = getType(node);
		if (type == null)
			return null;

		IndexedRegion text = (IndexedRegion) node;

		int textLength = text.getLength();
		int textOffset = text.getStartOffset();

		String contentType = superRegion.getContentType();
		String axis = getAxis(document, superRegion);

		IHyperlinkRegion hyperRegion = new HyperlinkRegion(textOffset, textLength,
				axis, contentType, type);
		return hyperRegion;
	}
}

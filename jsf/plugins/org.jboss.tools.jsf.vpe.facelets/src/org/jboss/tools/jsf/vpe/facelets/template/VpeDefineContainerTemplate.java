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
package org.jboss.tools.jsf.vpe.facelets.template;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.jboss.tools.vpe.editor.VpeIncludeInfo;
import org.jboss.tools.vpe.editor.VpeVisualDomBuilder;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.mapping.VpeElementMapping;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.template.VpeCreatorUtil;
import org.jboss.tools.vpe.editor.template.VpeTemplate;
import org.jboss.tools.vpe.editor.template.VpeTemplateManager;

public abstract class VpeDefineContainerTemplate extends VpeAbstractTemplate {
	private static final String ATTR_TEMPLATE = "template";
	private static Set defineContainer = new HashSet();
	
	protected void init(Element templateElement) {
		children = true;
		modify = false;
		initTemplateSections(templateElement, false, true, false, false, false);
	}


	public VpeCreationData create(VpePageContext pageContext, Node sourceNode, Document visualDocument) {
		String fileName = null;
		Attr attr = ((Element)sourceNode).getAttributeNode(ATTR_TEMPLATE);
		if (attr != null && attr.getNodeValue().trim().length() > 0) {
			fileName = attr.getNodeValue().trim();
			IFile file = VpeCreatorUtil.getFile(fileName, pageContext);
			if (file != null) {
				if (!pageContext.getVisualBuilder().isFileInIncludeStack(file)) {
					registerDefine(pageContext, sourceNode);
					Document document = VpeCreatorUtil.getDocumentForRead(file, pageContext);
					VpeCreationData creationData = createInclude(document, visualDocument);
					creationData.setData(new TemplateFileInfo(file));
					pageContext.getVisualBuilder().pushIncludeStack(new VpeIncludeInfo((Element)sourceNode, file, document));
					defineContainer.add(sourceNode);
					return creationData;
				}
			}
		}
		VpeCreationData creationData = createStub(fileName, (Element)sourceNode, visualDocument);
		creationData.setData(null);
		return creationData;
	}

	public void validate(VpePageContext pageContext, Node sourceNode, Document visualDocument, VpeCreationData creationData) {
		TemplateFileInfo templateFileInfo = (TemplateFileInfo)creationData.getData();
		if (templateFileInfo != null) {
			VpeIncludeInfo includeInfo = pageContext.getVisualBuilder().popIncludeStack();
			if (includeInfo != null) {
				VpeCreatorUtil.releaseDocumentFromRead(includeInfo.getDocument());
			}
		}
		defineContainer.remove(sourceNode);
	}

	public void beforeRemove(VpePageContext pageContext, Node sourceNode, Node visualNode, Object data) {
		TemplateFileInfo templateFileInfo = (TemplateFileInfo)data;
		if (templateFileInfo != null && templateFileInfo.templateFile != null) {
			pageContext.getEditPart().getController().getIncludeList().removeIncludeModel(templateFileInfo.templateFile);
		}
	}

	public boolean isRecreateAtAttrChange(VpePageContext pageContext, Element sourceElement, Document visualDocument, Node visualNode, Object data, String name, String value) {
		return true;
	}
	
	private void registerDefine(VpePageContext pageContext, Node defineContainer) {
		VpeTemplate template = null;
		NodeList sourceChildren = defineContainer.getChildNodes();
		int len = sourceChildren.getLength();
		for (int i = 0; i < len; i++) {
			Node sourceChild = sourceChildren.item(i);
			if (sourceChild.getNodeType() == Node.ELEMENT_NODE && "define".equals(sourceChild.getLocalName())) {
				if (template == null) {
					VpeTemplateManager templateManager = pageContext.getVisualBuilder().getTemplateManager(); 
					template = templateManager.getTemplate(pageContext, (Element)sourceChild, null);
					if (template == null) {
						break;
					}
				}
				pageContext.getVisualBuilder().registerNodes(new VpeElementMapping((Element)sourceChild, null, null, template, null, null));
			}
		}
	}
	
	private VpeCreationData createInclude(Document sourceDocument, Document visualDocument) {
		Element visualNewElement = visualDocument.createElement("div");
		VpeVisualDomBuilder.markIncludeElement(visualNewElement);
		VpeCreationData creationData = new VpeCreationData(visualNewElement);
		VpeChildrenInfo childrenInfo = new VpeChildrenInfo(visualNewElement);
		NodeList sourceChildren = sourceDocument.getChildNodes();
		int len = sourceChildren.getLength();
		for (int i = 0; i < len; i++) {
			childrenInfo.addSourceChild(sourceChildren.item(i));
		}
		creationData.addChildrenInfo(childrenInfo);
		return creationData;
	}

	public boolean containsText() {
		return false;
	}
	
	public static boolean isDefineContainer(Node sourceNode) {
		return defineContainer.contains(sourceNode);
		
	}
	
	protected abstract VpeCreationData createStub(String fileName, Node sourceElement, Document visualDocument);

	static class TemplateFileInfo {
		IFile templateFile;
		
		TemplateFileInfo(IFile templateFile) {
			this.templateFile = templateFile;
		}
	}
}

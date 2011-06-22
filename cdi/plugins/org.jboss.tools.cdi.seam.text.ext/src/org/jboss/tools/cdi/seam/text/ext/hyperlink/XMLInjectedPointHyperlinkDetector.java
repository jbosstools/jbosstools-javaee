/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.seam.text.ext.hyperlink;

import java.util.ArrayList;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.CDIUtil;
import org.jboss.tools.cdi.seam.config.core.CDISeamConfigExtension;
import org.jboss.tools.cdi.seam.config.core.definition.SeamBeansDefinition;
import org.jboss.tools.cdi.seam.config.core.definition.SeamFieldDefinition;
import org.jboss.tools.cdi.seam.config.core.definition.SeamMemberDefinition;
import org.jboss.tools.cdi.seam.config.core.definition.SeamMethodDefinition;
import org.jboss.tools.cdi.seam.config.core.util.Util;
import org.jboss.tools.cdi.seam.text.ext.CDISeamExtPlugin;
import org.jboss.tools.cdi.text.ext.hyperlink.InjectedPointHyperlinkDetector;
import org.jboss.tools.common.model.ui.editor.EditorPartWrapper;
import org.jboss.tools.common.text.ext.util.StructuredModelWrapper;
import org.jboss.tools.common.text.ext.util.Utils;
import org.jboss.tools.jst.text.ext.hyperlink.jsp.JSPRootHyperlinkPartitioner;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class XMLInjectedPointHyperlinkDetector extends InjectedPointHyperlinkDetector{
	private static final String INJECT_NAME = "Inject";
	private static final String INJECT_URI = "urn:java:ee";

	public IHyperlink[] detectHyperlinks(ITextViewer textViewer,
			IRegion region, boolean canShowMultipleHyperlinks) {
		
		this.viewer = textViewer;
		
		if (region == null || !canShowMultipleHyperlinks)
			return null;
		
		IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		
		if(!(editor instanceof EditorPartWrapper))
			return null;
		
		IEditorInput input = ((EditorPartWrapper)editor).getEditorInput();
		
		if(!(input instanceof FileEditorInput))
			return null;
		
		IFile file = ((FileEditorInput)input).getFile();

		if(file == null)
			return null;
		
		CDICoreNature cdiNature = CDIUtil.getCDINatureWithProgress(file.getProject());
		if(cdiNature == null)
			return null;
		
		document = textViewer.getDocument();
		
		ArrayList<IHyperlink> hyperlinks = new ArrayList<IHyperlink>();
		
		Node node = getTagNode(region.getOffset() );
		if(node == null) return null;
		
		int offset= ((IndexedRegion)node).getStartOffset();
		
		String uri = getURI(node);
		IJavaElement element = null;
		IType type = Util.resolveType(node.getLocalName(), uri, cdiNature);
		if(type != null) {
			element = type;
		} else {
			SeamMemberDefinition def = find(cdiNature, offset, file);
			if(def instanceof SeamFieldDefinition) {
				element = ((SeamFieldDefinition)def).getField();
			} else if(def instanceof SeamMethodDefinition) {
				element = ((SeamMethodDefinition)def).getMethod();
			}
		}
		if(element != null) {
			IFile elementFile = null;
			try{
				elementFile = (IFile)element.getUnderlyingResource();
			}catch(JavaModelException ex){
				CDISeamExtPlugin.log(ex);
			}
			
		if(elementFile != null)
			findInjectedBeans(cdiNature, element, offset, elementFile, hyperlinks);
		
			if (hyperlinks != null && !hyperlinks.isEmpty()) {
				return (IHyperlink[])hyperlinks.toArray(new IHyperlink[hyperlinks.size()]);
			}
		}
		return null;
	}
	
	private Node getTagNode(int offset) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		smw.init(document);
		try {
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;
			
			Node node = Utils.findNodeForOffset(xmlDocument, offset);
			if(node == null) return null;
			
			if(node instanceof IDOMElement){
				if(INJECT_NAME.equals(node.getLocalName()) && 
					INJECT_URI.equals(node.getNamespaceURI())){
						return node.getParentNode();
				}else if(offset >= ((IDOMElement)node).getStartOffset() && offset <= ((IDOMElement)node).getStartEndOffset())
					return node;
			}
			return null;
		} finally {
			smw.dispose();
		}
	}
	
	SeamMemberDefinition find(CDICoreNature cdi, int offset, IFile documentFile) {
		CDISeamConfigExtension ext = CDISeamConfigExtension.getExtension(cdi);
		if(ext == null) return null;		
		SeamBeansDefinition def = ext.getContext().getDefinition(documentFile.getFullPath());
		if(def == null) return null;		
		return def.findExactly(offset);
	}
	
	private String getURI(Node node) {
		String nodeName = node.getNodeName();
		if (nodeName.indexOf(':') == -1) return null;
		String nodePrefix = nodeName.substring(0, nodeName.indexOf(":")); //$NON-NLS-1$
		if (nodePrefix == null || nodePrefix.length() == 0) return null;		
		Map trackers = JSPRootHyperlinkPartitioner.getTrackersMap(document, ((IndexedRegion)node).getStartOffset());		
		return (String)(trackers == null ? null : trackers.get(nodePrefix));
	}
}
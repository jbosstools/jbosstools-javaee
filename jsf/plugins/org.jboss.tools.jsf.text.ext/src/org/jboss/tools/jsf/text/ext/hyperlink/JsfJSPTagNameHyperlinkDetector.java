/*******************************************************************************
 * Copyright (c) 2011 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.jsf.text.ext.hyperlink;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.jboss.tools.common.core.resources.XModelObjectEditorInput;
import org.jboss.tools.common.el.core.resolver.ELContext;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.text.ext.util.StructuredModelWrapper;
import org.jboss.tools.common.text.ext.util.Utils;
import org.jboss.tools.jst.jsp.jspeditor.JSPTextEditor;
import org.jboss.tools.jst.jsp.jspeditor.JSPTextEditor.JSPStructuredTextViewer;
import org.jboss.tools.jst.text.ext.util.TaglibManagerWrapper;
import org.jboss.tools.jst.web.kb.IPageContext;
import org.jboss.tools.jst.web.kb.KbQuery;
import org.jboss.tools.jst.web.kb.PageContextFactory;
import org.jboss.tools.jst.web.kb.PageProcessor;
import org.jboss.tools.jst.web.kb.internal.taglib.AbstractAttribute;
import org.jboss.tools.jst.web.kb.internal.taglib.AbstractComponent;
import org.jboss.tools.jst.web.kb.internal.taglib.TLDTag;
import org.jboss.tools.jst.web.kb.taglib.IAttribute;
import org.jboss.tools.jst.web.kb.taglib.IComponent;
import org.jboss.tools.jst.web.kb.taglib.INameSpace;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class JsfJSPTagNameHyperlinkDetector extends AbstractHyperlinkDetector {

	public IHyperlink[] detectHyperlinks(ITextViewer textViewer,
			IRegion region, boolean canShowMultipleHyperlinks) {
		
		if(!(textViewer instanceof JSPStructuredTextViewer))
			return null;
		
		JSPStructuredTextViewer viewer = (JSPStructuredTextViewer) textViewer;
		
		JSPTextEditor editor = viewer.getEditor();
		
		XModelObjectEditorInput xInput = (XModelObjectEditorInput) editor.getEditorInput();
		
		IFile file = xInput.getFile();
		
		StructuredModelWrapper smw = new StructuredModelWrapper();
		smw.init(textViewer.getDocument());
		
		Document xmlDocument = smw.getDocument();
		if (xmlDocument == null)
			return null;
		
		Node n = Utils.findNodeForOffset(xmlDocument, region.getOffset());
		
		IRegion reg = getRegion(n, region.getOffset());
		
		if(reg != null && n instanceof IDOMElement) {
			String tagName = n.getNodeName();
			int i = tagName.indexOf(":");
			KbQuery query = new KbQuery();
			query.setType(KbQuery.Type.TAG_NAME);
			
			if(i > 0) query.setPrefix(tagName.substring(0, i));
			else query.setPrefix("");
			
			query.setOffset(reg.getOffset());
			query.setValue(tagName);
			query.setUri(getURI(region, textViewer.getDocument()));
			query.setMask(false);
			
			ELContext context = PageContextFactory.createPageContext(file);
			
			if(context instanceof IPageContext){
				IComponent[] components = PageProcessor.getInstance().getComponents(query, (IPageContext)context);
				ArrayList<IHyperlink> hyperlinks = new ArrayList<IHyperlink>();
				for(IComponent component : components){
					if(validateComponent(component, ((IPageContext)context).getNameSpaces(reg.getOffset()), query.getPrefix())){
						TLDTagHyperlink link = new TLDTagHyperlink((AbstractComponent)component, reg);
						link.setDocument(textViewer.getDocument());
						hyperlinks.add(link);
					}
				}
				sortHyperlinks(hyperlinks);
				if(hyperlinks.size() > 0)
					return (IHyperlink[]) hyperlinks.toArray(new IHyperlink[hyperlinks.size()]);
			}
		} else if(reg != null && n instanceof IDOMAttr) {
			String tagName = ((IDOMAttr)n).getOwnerElement().getNodeName();
			int i = tagName.indexOf(":");
			KbQuery query = new KbQuery();
			query.setType(KbQuery.Type.ATTRIBUTE_NAME);
			
			if(i > 0) query.setPrefix(tagName.substring(0, i));
			else query.setPrefix("");
			
			query.setUri(getURI(region, textViewer.getDocument()));
			query.setParentTags(new String[]{tagName});
			query.setParent(tagName);
			query.setOffset(reg.getOffset());
			query.setValue(n.getNodeName());
			query.setMask(false);
			
			ELContext context = PageContextFactory.createPageContext(file);
			
			if(context instanceof IPageContext){
				IAttribute[] components = PageProcessor.getInstance().getAttributes(query, (IPageContext)context);
				ArrayList<IHyperlink> hyperlinks = new ArrayList<IHyperlink>();
				for(IAttribute attribute : components){
					if(validateComponent(attribute.getComponent(), ((IPageContext)context).getNameSpaces(reg.getOffset()), query.getPrefix())){
						TLDAttributeHyperlink link = new TLDAttributeHyperlink((AbstractAttribute)attribute, reg);
						link.setDocument(textViewer.getDocument());
						hyperlinks.add(link);
					}
				}
				sortHyperlinks(hyperlinks);
				if(hyperlinks.size() > 0)
					return (IHyperlink[]) hyperlinks.toArray(new IHyperlink[hyperlinks.size()]);
			}
		}
		
		return parse(textViewer.getDocument(), xmlDocument, region);
	}
	
	private boolean validateComponent(IComponent component, Map<String, List<INameSpace>> nameSpaces, String prefix){
		if(!validateNameSpace(component, nameSpaces, prefix))
			return false;
		
		if(component instanceof AbstractComponent){
			IFile file = TLDTagHyperlink.getFile((AbstractComponent)component);
			
			if(file != null && file.getFullPath() != null && file.getFullPath().toString().endsWith(".jar")) {
				XModelObject xmodelObject = TLDTagHyperlink.getXModelObject((AbstractComponent)component);
				if(xmodelObject != null)
					if(TLDTagHyperlink.getFileName(xmodelObject) != null)
						return true;
			}else if(file != null)
				return true;
		}
		return false;
	}
	
	private boolean validateNameSpace(IComponent component, Map<String, List<INameSpace>> nameSpaces, String prefix){
		String uri = component.getTagLib().getURI();
		List<INameSpace> list = nameSpaces.get(uri);
		if(list != null){
			for(INameSpace nameSpace : list){
				if(nameSpace.getPrefix().equals(prefix))
					return true;
			}
		}
		return false;
	}
	
	private void sortHyperlinks(ArrayList<IHyperlink> hyperlinks){
		for(IHyperlink link : hyperlinks){
			if(link instanceof TLDTagHyperlink){
				AbstractComponent tag = ((TLDTagHyperlink)link).getComponent();
				if(tag instanceof TLDTag){
					int index = hyperlinks.indexOf(link); 
					if(index != 0){
						IHyperlink first = hyperlinks.get(0);
						hyperlinks.set(0,link);
						hyperlinks.set(index, first);
					}
				}
			}
		}
	}
	
	private IHyperlink[] parse(IDocument document, Document xmlDocument, IRegion superRegion) {
		Node n = Utils.findNodeForOffset(xmlDocument, superRegion.getOffset());
		IRegion r = getRegion(n, superRegion.getOffset());
		if (r == null) return null;
		
		JsfJSPTagNameHyperlink link = new JsfJSPTagNameHyperlink(r);
		link.setDocument(document);
		return new IHyperlink[]{link};
	}
	
	private IRegion getRegion(Node n, int offset) {
		if (n == null || !(n instanceof IDOMNode)) return null;
		
		int nameStart, nameEnd;

		if(n instanceof IDOMAttr) {
			IDOMAttr attr = (IDOMAttr)n;
			String attrName = attr.getName();
			int start = attr.getStartOffset();
			nameStart = start;
			nameEnd = nameStart + attrName.length();
		} else if(n instanceof IDOMElement) {
			IDOMElement elem = (IDOMElement)n;		
			String tagName = elem.getTagName();
			if(offset >= elem.getStartOffset() && offset <= elem.getStartEndOffset()){
				int start = elem.getStartOffset();
				nameStart = start + "<".length(); //$NON-NLS-1$
				nameEnd = nameStart + tagName.length();
			}else if(offset >= elem.getEndStartOffset() && offset <= (elem.getEndStartOffset() + elem.getLength())) {
				int start = elem.getEndStartOffset();
				nameStart = start + "</".length(); //$NON-NLS-1$
				nameEnd = nameStart + tagName.length();
			}else
				return null;
		} else
			return null;
		
		if(offset < nameEnd)
			return new Region(nameStart, nameEnd - nameStart);
		else
			return null;
	}

	private String getURI(IRegion region, IDocument document) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		smw.init(document);
		try {
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;

			Node n = Utils.findNodeForOffset(xmlDocument, region.getOffset());

			Node node = null;
			if (n instanceof Attr) {
				node = ((Attr)n).getOwnerElement();
			} else {
				node = n;
			}
			if (!(node instanceof Element)) return null;
			
			String nodeName = node.getNodeName();
			if (nodeName.indexOf(':') == -1) return null;

			String nodePrefix = nodeName.substring(0, nodeName.indexOf(":")); //$NON-NLS-1$
			if (nodePrefix == null || nodePrefix.length() == 0) return null;

			TaglibManagerWrapper tmw = new TaglibManagerWrapper();
			tmw.init(document, region.getOffset());
			
			if (!tmw.exists()) return null;
			
			return tmw.getUri(nodePrefix);
		} finally {
			smw.dispose();
		}
	}
	
}

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

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.jboss.tools.common.core.resources.XModelObjectEditorInput;
import org.jboss.tools.common.el.core.resolver.ELContext;
import org.jboss.tools.common.text.ext.util.StructuredModelWrapper;
import org.jboss.tools.common.text.ext.util.Utils;
import org.jboss.tools.jst.jsp.jspeditor.JSPTextEditor;
import org.jboss.tools.jst.jsp.jspeditor.JSPTextEditor.JSPStructuredTextViewer;
import org.jboss.tools.jst.web.kb.IPageContext;
import org.jboss.tools.jst.web.kb.KbQuery;
import org.jboss.tools.jst.web.kb.PageContextFactory;
import org.jboss.tools.jst.web.kb.PageProcessor;
import org.jboss.tools.jst.web.kb.internal.taglib.AbstractComponent;
import org.jboss.tools.jst.web.kb.internal.taglib.FaceletTag;
import org.jboss.tools.jst.web.kb.internal.taglib.TLDTag;
import org.jboss.tools.jst.web.kb.taglib.IComponent;
import org.w3c.dom.Document;
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
		
		if(reg != null){
			KbQuery query = new KbQuery();
			query.setType(KbQuery.Type.TAG_NAME);
			
			query.setOffset(reg.getOffset());
			query.setValue(n.getNodeName());
			query.setMask(false);
			
			ELContext context = PageContextFactory.createPageContext(file);
			
			if(context instanceof IPageContext){
				IComponent[] components = PageProcessor.getInstance().getComponents(query, (IPageContext)context);
				ArrayList<IHyperlink> hyperlinks = new ArrayList<IHyperlink>();
				for(IComponent component : components){
					if(component instanceof TLDTag || component instanceof FaceletTag){
						TLDTagHyperlink link = new TLDTagHyperlink((AbstractComponent)component, reg);
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
		if (n == null || !(n instanceof IDOMElement)) return null;
		
		IDOMElement elem = (IDOMElement)n;
		
		String tagName = elem.getTagName();
		
		int start = elem.getStartOffset();
		int nameStart = start + "<".length(); //$NON-NLS-1$
		int nameEnd = nameStart + tagName.length();

		if(offset > nameEnd){
			start = elem.getEndStartOffset();
			nameStart = start + "</".length(); //$NON-NLS-1$
			nameEnd = nameStart + tagName.length();
		}

		return new Region(nameStart,nameEnd - nameStart);
	}

}

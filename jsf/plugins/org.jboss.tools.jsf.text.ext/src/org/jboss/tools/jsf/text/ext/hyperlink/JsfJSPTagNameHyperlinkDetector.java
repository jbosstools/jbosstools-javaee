package org.jboss.tools.jsf.text.ext.hyperlink;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.jboss.tools.common.core.resources.XModelObjectEditorInput;
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
		
		IDOMNode node = (IDOMNode)Utils.findNodeForOffset((Node)xmlDocument, region.getOffset());
		if(node != null){
			KbQuery query = new KbQuery();
			query.setType(KbQuery.Type.TAG_NAME);
			
			
			IStructuredDocumentRegion sRegion = node.getStartStructuredDocumentRegion();
			
			if(sRegion == null)
				return null;
			
			if(region.getOffset() > (sRegion.getStartOffset()+sRegion.getLength()))
				sRegion = node.getEndStructuredDocumentRegion();
			
			final IStructuredDocumentRegion reg = sRegion;
			
			if(reg != null){
				query.setOffset(sRegion.getStartOffset());
				query.setValue(node.getNodeName());
				query.setMask(false);
				
				IPageContext context = (IPageContext)PageContextFactory.createPageContext(file);
				
				IComponent[] components = PageProcessor.getInstance().getComponents(query, context);
				ArrayList<IHyperlink> hyperlinks = new ArrayList<IHyperlink>();
				for(IComponent component : components){
					if(component instanceof TLDTag || component instanceof FaceletTag){
						TLDTagHyperlink link = new TLDTagHyperlink((AbstractComponent)component, new IRegion(){
							public int getLength() {
								return reg.getLength();
							}

							public int getOffset() {
								return reg.getStartOffset();
							}
						});
						link.setDocument(textViewer.getDocument());
						hyperlinks.add(link);
					}
				}
				sortHyperlinks(hyperlinks);
				if(hyperlinks.size() > 0)
					return (IHyperlink[]) hyperlinks.toArray(new IHyperlink[hyperlinks.size()]);
			}
			
		}
		
		return null;
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

}

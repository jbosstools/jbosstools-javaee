package org.jboss.tools.jsf.text.ext.richfaces.hyperlink;

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.jboss.tools.common.text.ext.hyperlink.xpl.Messages;
import org.jboss.tools.jst.text.ext.hyperlink.CSSClassHyperlink;
import org.jboss.tools.jst.web.kb.ICSSContainerSupport;
import org.jboss.tools.jst.web.kb.IPageContext;
import org.jboss.tools.jst.web.kb.PageContextFactory;
import org.jboss.tools.jst.web.kb.PageContextFactory.CSSStyleSheetDescriptor;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;

public class RichfacesCSSClassHyperlink  extends CSSClassHyperlink {

	@Override
	protected void doHyperlink(IRegion region) {
		ICSSContainerSupport cssContainerSupport = null;
		IPageContext context = PageContextFactory.createPageContext(getFile(), region.getOffset(), getContentType(getDocument()));
		if (!(context instanceof ICSSContainerSupport)) {
			openFileFailed();
			return;
		}
		cssContainerSupport = (ICSSContainerSupport)context;
		List<CSSStyleSheetDescriptor> descrs = cssContainerSupport.getCSSStyleSheetDescriptors();

		for (int i = (descrs == null) ? -1 : descrs.size() - 1; descrs != null && i >= 0; i--) {
			CSSStyleSheetDescriptor descr = descrs.get(i);
			CSSRuleList rules = descr.sheet.getCssRules();
			for (int r = 0; rules != null && r < rules.getLength(); r++) {
				if (isRuleMatch(rules.item(r), getStyleName(region))) {
					CSSRule rule = rules.item(r);
					System.out.println();
					showRegion(
							PageContextFactory.getFileFromProject(descr.source, getFile()), 
							new Region(((IndexedRegion)rule).getStartOffset(), ((IndexedRegion)rule).getLength()));
					return;
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IHyperlink#getHyperlinkText()
	 */
	public String getHyperlinkText() {
		String styleName = getStyleName(fLastRegion);
		if (styleName == null)
			return MessageFormat.format(Messages.OpenA, Messages.CSSStyle);

		return MessageFormat.format(Messages.OpenCSSStyle, styleName);
	}

	
	/**
	 * Returns the content type of document
	 * 
	 * @param document -
	 *            assumes document is not null
	 * @return String content type of given document
	 */
	@SuppressWarnings("restriction")
	private String getContentType(IDocument document) {
		String type = null;
		
		IModelManager mgr = StructuredModelManager.getModelManager();
		IStructuredModel model = null;
		try {
			model = mgr.getExistingModelForRead(document);
			if (model != null) {
				type = model.getContentTypeIdentifier();
			}
		} finally {
			if (model != null) {
				model.releaseFromRead();
			}
		}
		return type;
	}
}

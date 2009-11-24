/*******************************************************************************
 * Copyright (c) 2009 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.jsf.text.ext.richfaces.hyperlink;

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.jboss.tools.common.el.core.resolver.ELContext;
import org.jboss.tools.common.text.ext.hyperlink.xpl.Messages;
import org.jboss.tools.jst.text.ext.hyperlink.CSSClassHyperlink;
import org.jboss.tools.jst.web.kb.ICSSContainerSupport;
import org.jboss.tools.jst.web.kb.PageContextFactory;
import org.jboss.tools.jst.web.kb.PageContextFactory.CSSStyleSheetDescriptor;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;

/**
 * 
 * @author Victor Rubezhny
 * 
 */
@SuppressWarnings("restriction")
public class RichfacesCSSClassHyperlink  extends CSSClassHyperlink {
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
}

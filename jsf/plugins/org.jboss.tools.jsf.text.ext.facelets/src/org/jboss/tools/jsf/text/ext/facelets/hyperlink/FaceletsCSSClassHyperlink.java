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

package org.jboss.tools.jsf.text.ext.facelets.hyperlink;

import java.text.MessageFormat;

import org.jboss.tools.common.text.ext.hyperlink.xpl.Messages;
import org.jboss.tools.jst.web.ui.internal.text.ext.hyperlink.CSSClassHyperlink;

/**
 * 
 * @author Victor Rubezhny
 * 
 */
public class FaceletsCSSClassHyperlink  extends CSSClassHyperlink {
	/*
	 * (non-Javadoc)
	 * 
	 * @see IHyperlink#getHyperlinkText()
	 */
	public String getHyperlinkText() {
		String styleName = getStyleName(getHyperlinkRegion());
		if (styleName == null)
			return MessageFormat.format(Messages.OpenA, Messages.CSSStyle);

		return MessageFormat.format(Messages.OpenCSSStyle, styleName);
	}
}

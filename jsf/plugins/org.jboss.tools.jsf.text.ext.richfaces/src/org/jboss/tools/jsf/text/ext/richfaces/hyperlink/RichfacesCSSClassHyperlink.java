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

import org.jboss.tools.common.text.ext.hyperlink.xpl.Messages;
import org.jboss.tools.jst.text.ext.hyperlink.CSSClassHyperlink;

/**
 * 
 * @author Victor Rubezhny
 * 
 */
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

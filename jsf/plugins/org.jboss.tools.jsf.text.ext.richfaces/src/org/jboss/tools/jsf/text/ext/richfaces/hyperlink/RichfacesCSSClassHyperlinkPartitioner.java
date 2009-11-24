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

import org.eclipse.jface.text.IDocument;
import org.jboss.tools.common.text.ext.hyperlink.IHyperlinkRegion;
import org.jboss.tools.jst.text.ext.hyperlink.CSSClassHyperlinkPartitioner;

/**
 * 
 * @author Victor Rubezhny
 *
 */
public class RichfacesCSSClassHyperlinkPartitioner extends CSSClassHyperlinkPartitioner {
	public static final String RICHFACES_CSS_CLASS_PARTITION = "org.jboss.tools.common.text.ext.RICHFACES_CSS_CLASS"; //$NON-NLS-1$
	private static final String RICHFACES_CSS_CLASS_TOKEN = "/styleClass/"; //$NON-NLS-1$

	@Override
	public boolean recognize(IDocument document, IHyperlinkRegion region) {
		if (region.getAxis() != null
				&& region.getAxis().endsWith(RICHFACES_CSS_CLASS_TOKEN))
			return true;
		return false;
	}

	@Override
	protected String getPartitionType(String axis) {
		return RICHFACES_CSS_CLASS_PARTITION;
	}
	
}

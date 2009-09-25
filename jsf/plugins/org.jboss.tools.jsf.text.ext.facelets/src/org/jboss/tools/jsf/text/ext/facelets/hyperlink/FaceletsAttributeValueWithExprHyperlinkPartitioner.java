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
package org.jboss.tools.jsf.text.ext.facelets.hyperlink;

import org.eclipse.jface.text.IDocument;

import org.jboss.tools.common.text.ext.hyperlink.IHyperlinkRegion;
import org.jboss.tools.jsf.text.ext.hyperlink.JSPExprHyperlinkPartitioner;
import org.jboss.tools.jst.text.ext.hyperlink.jsp.JSPElementAttributeValueHyperlinkPartitioner;

public class FaceletsAttributeValueWithExprHyperlinkPartitioner extends JSPElementAttributeValueHyperlinkPartitioner {
	public static final String FACELETS_ATTRIBUTE_VALUE_WITH_EXPR_PARTITION = "org.jboss.tools.common.text.ext.jsp.FACELETS_ATTRIBUTE_VALUE_WITH_EXPR"; //$NON-NLS-1$

	protected String getPartitionType() {
		return FACELETS_ATTRIBUTE_VALUE_WITH_EXPR_PARTITION;
	}

	public boolean excludes(String partitionType, IDocument document, IHyperlinkRegion superRegion) {
		if (JSPExprHyperlinkPartitioner.JSP_EXPRESSION_PARTITION.equals(partitionType))
			return true;
		return false;
	}

	public String getExclusionPartitionType() {
		return null;
	}

}

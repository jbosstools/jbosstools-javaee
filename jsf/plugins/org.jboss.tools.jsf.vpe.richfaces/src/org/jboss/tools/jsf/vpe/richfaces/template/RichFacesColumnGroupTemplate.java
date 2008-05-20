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
package org.jboss.tools.jsf.vpe.richfaces.template;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;

public class RichFacesColumnGroupTemplate extends RichFacesSubTableTemplate {

	public static RichFacesColumnGroupTemplate DEFAULT_INSTANCE = new RichFacesColumnGroupTemplate();

	public RichFacesColumnGroupTemplate() {
		super();
	}

	protected String getHeaderClass() {
		return "dr-table-header rich-table-header";
	}

	protected String getHeaderContinueClass() {
		return "dr-table-header-continue rich-table-header-continue";
	}

	protected String getFooterClass() {
		return "dr-table-footer rich-table-footer";
	}

	protected String getFooterContinueClass() {
		return "dr-table-footer-continue rich-table-footer-continue";
	}

	protected String getCellClass() {
		return "dr-table-cell rich-table-cell";
	}

	protected String getHeaderBackgoundImgStyle() {
		return ComponentUtil.getHeaderBackgoundImgStyle();
	}
}
/*******************************************************************************
 * Copyright (c) 2007-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.text.ext.hyperlink;

/**
 * Partitioner for the {@code name} attribute of
 * {@code h:outputStylesheet, h:outputScript, h:graphicImage} tags.
 *
 * @see JSF2LinkHyperlink
 * @see <a href="https://jira.jboss.org/jira/browse/JBIDE-5382">JBIDE-5382</a>
 * 
 * @author yradtsevich
 */
public class JSF2JSPLinkHyperlinkPartitioner extends JSFJSPLinkHyperlinkPartitioner {
	public static final String JSF2_JSP_LINK_PARTITION = "org.jboss.tools.common.text.ext.jsp.JSF2_JSP_LINK"; //$NON-NLS-1$

	@Override
	protected String getPartitionType() {
		return JSF2_JSP_LINK_PARTITION;
	}
}

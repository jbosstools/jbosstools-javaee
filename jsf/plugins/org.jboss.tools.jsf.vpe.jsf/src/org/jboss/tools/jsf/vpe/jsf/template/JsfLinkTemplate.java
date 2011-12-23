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
package org.jboss.tools.jsf.vpe.jsf.template;

import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.w3c.dom.Element;

/**
 * Template for h:link
 * 
 * @author yradtsevich
 */
public class JsfLinkTemplate extends AbstarctLinkJsfTemplate {

	@Override
	protected boolean hasParentForm(VpePageContext pageContext, Element sourceElement) {
		/*
		 * Fixes https://issues.jboss.org/browse/JBIDE-8009
		 * <h:link> should not depend on <form> tag
		 */
		return true;
	}
	
}

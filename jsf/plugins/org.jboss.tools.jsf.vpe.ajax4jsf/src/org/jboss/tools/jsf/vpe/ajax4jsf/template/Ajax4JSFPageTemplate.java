/*******************************************************************************
 * Copyright (c) 2007-2008 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.ajax4jsf.template;

import java.util.Map;
import java.util.TreeMap;

import org.jboss.tools.vpe.editor.template.VpeAbstractBodyTemplate;
import org.jboss.tools.vpe.editor.util.HTML;

/**
 * The template intended for processing {@code <a4j:page>} elements.
 *
 * @author dmaliarevich
 * @author yradtsevich
 */
public class Ajax4JSFPageTemplate extends VpeAbstractBodyTemplate {

	private static final Map<String, String> ATTRIBUTES_MAP = new TreeMap<String, String>();
	{
		ATTRIBUTES_MAP.put(Ajax4JSF.ATTR_DIR, HTML.ATTR_DIR);
		ATTRIBUTES_MAP.put(Ajax4JSF.ATTR_ID, HTML.ATTR_ID);
		ATTRIBUTES_MAP.put(Ajax4JSF.ATTR_STYLE, HTML.ATTR_STYLE);
		ATTRIBUTES_MAP.put(Ajax4JSF.ATTR_STYLE_CLASS, HTML.ATTR_CLASS);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getTargetAttributeName(String sourceAttributeName) {
		return ATTRIBUTES_MAP.get(sourceAttributeName);
	}
}

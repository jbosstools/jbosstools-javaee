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
package org.jboss.tools.jsf.vpe.jsf.template;

import org.jboss.tools.vpe.editor.template.VpeAbstractBodyTemplate;
import org.jboss.tools.vpe.editor.util.HTML;

/**
 * The template of h:body. 
 * 
 * Omits all attributes except 'id' 
 * 
 * @see VpeAbstractBodyTemplate
 * @author yradtsevich
 */
public class JsfBodyTemplate extends VpeAbstractBodyTemplate {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getTargetAttributeName(String sourceAttributeName) {
		if (sourceAttributeName.equals(JSF.ATTR_ID)) {
			return HTML.ATTR_ID;
		} else {
			return null;
		}
	}
}

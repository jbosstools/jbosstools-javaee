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
package org.jboss.tools.jsf.vpe.jsf.template.selectitem;

import org.jboss.tools.jsf.vpe.jsf.template.AbstractOutputJsfTemplate;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

/**
 * This class is the base class for all templates of 
 * {@code <h:selectItem>} and {@code <h:selectItems>}.
 * 
 * @author yradtsevich
 */
public abstract class AbstractSelectItemTemplate extends AbstractOutputJsfTemplate {

	/**
	 * This field is used to differ templates of 
	 * {@code <h:selectItem>} and {@code <h:selectItems>}.
	 * 
	 * @see SelectItemType
	 */
	protected final SelectItemType selectItemType;
	
	protected AbstractSelectItemTemplate(SelectItemType selectItemType) {
		this.selectItemType = selectItemType;
	}
	
	@Override
	public final Attr getOutputAttributeNode(Element element) {
		return selectItemType.getOutputAttributeNode(element);
	}
}

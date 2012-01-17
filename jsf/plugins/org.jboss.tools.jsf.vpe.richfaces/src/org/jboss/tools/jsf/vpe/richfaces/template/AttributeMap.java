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
package org.jboss.tools.jsf.vpe.richfaces.template;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.w3c.dom.Element;

/**
 * A wrapper of DOM Element that can extract its attributes as objects of different types.
 * The methods of the class never throw any exception. In case if parsing is impossible they
 * always return a correct value(see methods descriptions).
 * 
 * @author yradtsevich
 * @see Element
 */
public class AttributeMap {
	private static final String FALSE_VALUE = "false"; //$NON-NLS-1$
	private static final String TRUE_VALUE = "true"; //$NON-NLS-1$
	private Element element;
	
	public AttributeMap(Element element) {
		this.element = element;
	}
	
	/**
	 * A wrapper of <code>ComponentUtil.getAttribute(sourceElement, attributeName)</code>
	 * 
	 * @param attributeName attribute name
	 * @return <code>ComponentUtil.getAttribute(sourceElement, attributeName)</code>
	 * 
	 * @see ComponentUtil
	 */
	public String getString(String attributeName) {
		return ComponentUtil.getAttribute(element, attributeName);
	}
	
	/**
	 * Returns Boolean representation of the attribute. 
	 *  
	 * @param attributeName attribute name
	 * @return attribute value. 
	 * <code>Boolean.TRUE</code>, if the attribute equals "true",
	 * <code>Boolean.False</code>, if the attribute equals "false", 
	 * <code>null</code> otherwise.
	 */
	public Boolean getBoolean(String attributeName) {
		String attribute = element.getAttribute(attributeName);
		
		Boolean ret;
		
		if (TRUE_VALUE.equals(attribute)) {
			ret = Boolean.TRUE;
		} else if (FALSE_VALUE.equals(attribute)) {
			ret = Boolean.FALSE;
		} else {
			ret = null;
		}
		
		return ret;
	}
	
	public void setAttribute(String name, boolean value) {
		if (value) {
			element.setAttribute(name, TRUE_VALUE);
		} else {
			element.setAttribute(name, FALSE_VALUE);
		}
	}
	
	/**
	 * Tests if its parameter is blank string
	 * 
	 * @param attributeName
	 * @return <code>true</code> if and only if <code>attributeName</code> is
	 * either <code>null</code>, or blank, or contains non-printable characters only
	 */
	public boolean isBlank(String attributeName) {
		String value = getString(attributeName);
		return ComponentUtil.isBlank(value);
	}
	
}

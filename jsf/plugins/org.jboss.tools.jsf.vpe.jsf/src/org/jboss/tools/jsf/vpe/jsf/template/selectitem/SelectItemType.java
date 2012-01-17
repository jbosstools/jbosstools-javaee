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
import org.jboss.tools.jsf.vpe.jsf.template.ComponentUtil;
import org.jboss.tools.jsf.vpe.jsf.template.JSF;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

/**
 * @author yradtsevich
 */
public enum SelectItemType {
	/**
	 * Reflects the tag-type {@code <h:selectItem>} and provides operations on it. 
	 */
	SELECT_ITEM {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public Attr getOutputAttributeNode(Element element) {
			Attr outputAttributeNode;
			
			if (element.hasAttribute(JSF.ATTR_ITEM_LABEL)) {
				outputAttributeNode = element.getAttributeNode(JSF.ATTR_ITEM_LABEL);
			} else if (element.hasAttribute(JSF.ATTR_ITEM_VALUE)) {
				outputAttributeNode = element.getAttributeNode(JSF.ATTR_ITEM_VALUE);
			} else if (element.hasAttribute(JSF.ATTR_VALUE)) {
				outputAttributeNode = element.getAttributeNode(JSF.ATTR_VALUE);
			} else {
				outputAttributeNode = null;
			}
			return outputAttributeNode;
		}
		
		/**
		 * Returns value of the attribute {@code disabledItem} of the {@code element}
		 */
		@Override
		public boolean isDisabledItem(Element element) {
			return ComponentUtil.string2boolean(ComponentUtil
					.getAttribute(element, JSF.ATTR_ITEM_DISABLED));
		}
	},

	/**
	 * Reflects the tag-type {@code <h:selectItems>} and provides operations on it.
	 */
	SELECT_ITEMS {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public Attr getOutputAttributeNode(Element element) {
			Attr outputAttributeNode;

			if (element.hasAttribute(JSF.ATTR_VALUE)) {
				outputAttributeNode = element.getAttributeNode(JSF.ATTR_VALUE);
			} else {
				outputAttributeNode = null;
			}
			return outputAttributeNode;
		}
		
		/**
		 * As the tag {@code <h:selectItems>} does not support attribute {@code disabledItem}, 
		 * it always returns {@code true} 
		 */
		@Override
		public boolean isDisabledItem(Element element) {
			return false;
		}
	},
	
	/**
	 * Reflects the tag-type {@code <s:enumItem>} and provides operations on it. 
	 */
	ENUM_ITEM {
	    
	    private final String LABEL = "label"; //$NON-NLS-1$
	    private final String ENUM_VALUE = "enumValue"; //$NON-NLS-1$
		/**
		 * {@inheritDoc}
		 */
		@Override
		public Attr getOutputAttributeNode(Element element) {
			Attr outputAttributeNode;
			
			if (element.hasAttribute(JSF.ATTR_ITEM_LABEL)) {
				outputAttributeNode = element.getAttributeNode(JSF.ATTR_ITEM_LABEL);
			} else if (element.hasAttribute(LABEL)) {
				outputAttributeNode = element.getAttributeNode(LABEL);
			} else if (element.hasAttribute(JSF.ATTR_ITEM_VALUE)) {
			    outputAttributeNode = element.getAttributeNode(JSF.ATTR_ITEM_VALUE);
			} else if (element.hasAttribute(ENUM_VALUE)) {
				outputAttributeNode = element.getAttributeNode(ENUM_VALUE);
			} else if (element.hasAttribute(JSF.ATTR_VALUE)) {
				outputAttributeNode = element.getAttributeNode(JSF.ATTR_VALUE);
			} else {
				outputAttributeNode = null;
			}
			return outputAttributeNode;
		}
		
		/**
		 * Returns value of the attribute {@code disabledItem} of the {@code element}
		 */
		@Override
		public boolean isDisabledItem(Element element) {
			return ComponentUtil.string2boolean(ComponentUtil
					.getAttribute(element, JSF.ATTR_ITEM_DISABLED));
		}
	};
	
	/**
	 * @see AbstractOutputJsfTemplate#getOutputAttributeNode(Element)
	 */
	public abstract Attr getOutputAttributeNode(Element element);
	/**
	 * Returns {@code true} if the {@code element} should be disabled
	 * according to its {@code disabledItem} attribute. 
	 */
	public abstract boolean isDisabledItem(Element element);
}

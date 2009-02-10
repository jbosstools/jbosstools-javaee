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

package org.jboss.tools.jsf.vpe.jstl;


import org.jboss.tools.vpe.editor.util.Constants;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;


/**
 * The Class ComponentUtil.
 */
public class ComponentUtil {

    /**
     * Returns value of attribute.
     * 
     * @param attributeName the attribute name
     * @param sourceElement the source element
     * 
     * @return the attribute
     */
    public static String getAttribute(Element sourceElement, String attributeName) {
        String attribute = sourceElement.getAttribute(attributeName);
        if (attribute == null) {
            attribute = Constants.EMPTY;
        }
        return attribute;
    }

    /**
     * Returns value of attribute.
     * 
     * @param attributeName the attribute name
     * @param sourceElement the source element
     * @param defaultValue the default value
     *
     * @return the attribute
     */
    public static String getAttribute(Element sourceElement, String attributeName, String defaultValue) {
        String attribute = sourceElement.getAttribute(attributeName);
        if (attribute == null) {
            attribute = defaultValue;
        }
        return attribute;
    }

    /**
     * Returns value of attribute.
     * 
     * @param attributeName the attribute name
     * @param sourceElement the source element
     * 
     * @return the attribute
     */
    public static String getAttribute(nsIDOMElement sourceElement, String attributeName) {
        String attribute = sourceElement.getAttribute(attributeName);
        if (attribute == null) {
            attribute = Constants.EMPTY;
        }
        return attribute;
    }
}
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
package org.jboss.tools.common.meta.constraint.impl;

import org.eclipse.osgi.util.NLS;

import org.jboss.tools.struts.messages.StrutsUIMessages;

public class XAttributeConstraintStrutsProperty extends XAttributeConstraintImpl {

    public XAttributeConstraintStrutsProperty() {}

    public boolean accepts(String value) {
        if(value == null) return false;
        if(value.length() == 0) return true;
        if(!isStartChar(value.charAt(0))) return false;
        for (int i = 1; i < value.length(); i++) {
            if(!isPartChar(value.charAt(i))) return false;
        }
        return true;
    }

    public String getError(String value) {
        if(accepts(value)) return null;
        if(!isStartChar(value.charAt(0))) return NLS.bind(StrutsUIMessages.MAY_NOT_START_WITH_CHARACTER, "" +value.charAt(0)); //$NON-NLS-2$
        for (int i = 1; i < value.length(); i++)
          if(!isPartChar(value.charAt(i))) return NLS.bind(StrutsUIMessages.MAY_NOT_CONTAIN_CHARACTER, "" + value.charAt(i)); //$NON-NLS-2$
        return null;
    }

    private boolean isStartChar(char c) {
        return Character.isLetter(c) || (c == '_');
    }

    private boolean isPartChar(char c) {
        return isStartChar(c) || (c == '-') || Character.isDigit(c);
    }

}

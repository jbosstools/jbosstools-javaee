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

import org.jboss.tools.struts.StrutsModelPlugin;
import org.jboss.tools.struts.messages.StrutsUIMessages;

public class XAttributeConstraintFileSize extends XAttributeConstraintImpl {

    public boolean accepts(String value) {
        if(value == null || value.length() == 0) return true;
        String v = value.toLowerCase();
        if(v.endsWith("k") || v.endsWith("m") || v.endsWith("g")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            v = value.substring(0, value.length() - 1);
        }
        try {
            int i = Integer.parseInt(v);
            return (i >= 0);
        } catch (Exception e) {
        	StrutsModelPlugin.getPluginLog().logError(e);
            return false;
        }
    }

    public String getError(String value) {
        return accepts(value) ? null :
           StrutsUIMessages.CAN_BE_EXPRESSED;
    }

}

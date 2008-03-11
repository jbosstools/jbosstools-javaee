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
package org.jboss.tools.common.meta.impl.adapters;

import org.jboss.tools.common.meta.constraint.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.struts.StrutsConstants;

public class XAdapterStrutsItemElementType extends XAdapterElementType implements StrutsConstants {

    public String getProperty(XProperty object) {
        XModelObject o = (XModelObject)object;
        String type = o.getAttributeValue(ATT_TYPE);
        if(type != null && type.length() > 0) return type;
        return super.getProperty(object);
    }

}


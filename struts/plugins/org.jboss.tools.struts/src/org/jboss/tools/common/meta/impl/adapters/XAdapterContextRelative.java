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
import org.jboss.tools.struts.model.helpers.*;
import org.jboss.tools.struts.webprj.pattern.UrlPattern;

public class XAdapterContextRelative extends XAdapter implements StrutsConstants {

    public void setProperty(XProperty object, String value) {
        String ov = getProperty(object);
        boolean ob = "true".equals(ov) || "yes".equals(ov);
        super.setProperty(object, value);
        boolean nb = "true".equals(value) || "yes".equals(value);
        if(ob == nb) return;
        XModelObject forward = (XModelObject)object;
        if(!forward.isActive()) return;
        String module = StrutsProcessStructureHelper.instance.getProcessModule(forward);
        if(module == null || module.length() == 0) return;
        String path = forward.get(ATT_PATH);
		UrlPattern up = StrutsProcessStructureHelper.instance.getUrlPattern(forward);
        ob = path.indexOf(module + "/") >= 0;
        if(nb == ob) return;
        String nv = (nb) ? up.getContextRelativePath(path, module) 
                         : up.getModuleRelativePath(path, module);
        forward.set(ATT_PATH, nv);
    }

}


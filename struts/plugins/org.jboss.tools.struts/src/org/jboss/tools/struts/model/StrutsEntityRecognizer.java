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
package org.jboss.tools.struts.model;

import org.jboss.tools.common.xml.XMLEntityResolver;
import org.jboss.tools.struts.*;
import org.jboss.tools.common.model.loaders.*;

public class StrutsEntityRecognizer implements EntityRecognizer, StrutsConstants {

    static {
        try {
            Class<?> c = StrutsEntityRecognizer.class;
            XMLEntityResolver.registerPublicEntity(DOC_PUBLICID_10, c, "/meta/struts-config_1_0.dtd");
            XMLEntityResolver.registerPublicEntity(DOC_PUBLICID_11, c, "/meta/struts-config_1_1.dtd");
            XMLEntityResolver.registerPublicEntity(DOC_PUBLICID_12, c, "/meta/struts-config_1_2.dtd");
        } catch (Exception e) {
            StrutsModelPlugin.getPluginLog().logError(e);
        }
    }
    
    public StrutsEntityRecognizer() {}

    public String getEntityName(String ext, String body) {
        if (body == null) return null;
        if(body.indexOf(DOC_PUBLICID_PR) < 0) return null;
        if (body.indexOf(DOC_PUBLICID_10) > 0) return ENT_STRUTSCONFIG+VER_SUFFIX_10;
        if (body.indexOf(DOC_PUBLICID_11) > 0) return ENT_STRUTSCONFIG+VER_SUFFIX_11;
        if (body.indexOf(DOC_PUBLICID_12) > 0) return ENT_STRUTSCONFIG+VER_SUFFIX_12;
        return null;
    }

}

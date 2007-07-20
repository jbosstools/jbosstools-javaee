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
package org.jboss.tools.struts.validators.model;

import org.jboss.tools.common.model.loaders.*;
import org.jboss.tools.common.xml.XMLEntityResolver;
import org.jboss.tools.struts.StrutsModelPlugin;

public class FileValidatorRecognizer implements EntityRecognizer, ValidatorConstants {
    static {
        try {
            XMLEntityResolver.registerPublicEntity(DOC_PUBLICID, FileValidatorRecognizer.class, "/meta/validator_1_0.dtd");
            XMLEntityResolver.registerPublicEntity(DOC_PUBLICID_11, FileValidatorRecognizer.class, "/meta/validator_1_1_3.dtd");
        } catch (Exception e) {
            StrutsModelPlugin.getPluginLog().logError(e);
        }
    }

    public FileValidatorRecognizer() {}

    public String getEntityName(String ext, String body) {
        if(body == null) return null;
        if(body.indexOf(DOC_PUBLICID) >= 0) return "FileValidationRules";
        if(body.indexOf(DOC_PUBLICID_11) >= 0) return "FileValidationRules11";
        return null;
    }

}

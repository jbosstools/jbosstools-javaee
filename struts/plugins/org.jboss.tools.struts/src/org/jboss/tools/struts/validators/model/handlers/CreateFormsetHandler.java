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
package org.jboss.tools.struts.validators.model.handlers;

import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.filesystems.impl.CreateFileHandler;
import org.jboss.tools.struts.validators.model.ValidatorConstants;
import org.jboss.tools.struts.validators.model.XModelEntityResolver;

public class CreateFormsetHandler extends CreateFileHandler {

    protected XModelObject modifyCreatedObject(XModelObject o) {
    	String entity = XModelEntityResolver.resolveEntity(o, ValidatorConstants.ENT_FORMSET);
        XModelObject formset = o.getModel().createModelObject(entity, null);
        o.addChild(formset);
        return o;
    }

}


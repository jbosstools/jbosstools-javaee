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

import org.jboss.tools.common.meta.XAdoptManager;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.meta.action.impl.handlers.*;
import org.jboss.tools.struts.StrutsConstants;
import org.jboss.tools.struts.validators.model.ValidatorConstants;
import org.jboss.tools.struts.validators.model.XModelEntityResolver;

public class FormsetAdopt implements XAdoptManager {

    public FormsetAdopt() {}

    public boolean isAdoptable(XModelObject target, XModelObject object) {
        String entity = object.getModelEntity().getName();
        return isFormBean(entity) || isAction(entity);
    }

    protected boolean isFormBean(String entity) {
        return entity.startsWith(StrutsConstants.ENT_FORMBEAN);
    }

    protected boolean isAction(String entity) {
        return entity.startsWith(StrutsConstants.ENT_ACTION);
    }

    public void adopt(XModelObject target, XModelObject object, java.util.Properties p) {
    	String entity = XModelEntityResolver.resolveEntity(target, ValidatorConstants.ENT_FORM);
        XModelObject o = object.getModel().createModelObject(entity, null);
        String attr = (isAction(object.getModelEntity().getName())) ? "path" : "name";
        o.setAttributeValue("name", object.getAttributeValue(attr));
        DefaultCreateHandler.addCreatedObject(target, o, p);
    }

}

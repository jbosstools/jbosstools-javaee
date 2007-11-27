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
package org.jboss.tools.struts.model.handlers;

import java.util.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.struts.model.helpers.*;

public class EditGlobalElementPath extends CreateForwardHandler {
    StrutsProcessStructureHelper h = new StrutsProcessStructureHelper();

    public EditGlobalElementPath() {}

    public void executeHandler0(XModelObject object, Properties prop) throws Exception {
        Properties p = extractProperties(data[0]);
        setOtherProperties(object, p);
        modifyCreatedObject(object);
        String path = p.getProperty(ATT_PATH);
        object.getModel().changeObjectAttribute(object, ATT_PATH, path);
    }

    protected XModelObject getCreatedItemOnProcess() {
        XModelObject o = SelectOnDiagramHandler.getItemOnProcess(createdObject);
        return (o == null) ? null : h.getElementTarget(o);
    }

}

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

import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.impl.GroupOrderedChildren;

public class ValidatorGrouppedChildren extends GroupOrderedChildren {
    String firstEntity = null;

    protected void loadFirstEntity(XModelObject o) {
        if(firstEntity != null) return;
        XModelObject p = o.getParent();
        if(p != null) firstEntity = p.getModelEntity().getChildren()[0].getName();
    }
    
    protected int getGroup(XModelObject o) {
        loadFirstEntity(o);
        return (o.getModelEntity().getName().equals(firstEntity)) ? 0 : 1;
    }

}


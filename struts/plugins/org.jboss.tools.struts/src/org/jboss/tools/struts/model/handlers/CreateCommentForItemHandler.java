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
import org.jboss.tools.common.meta.action.impl.handlers.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.util.*;
import org.jboss.tools.struts.*;
import org.jboss.tools.struts.model.helpers.*;

public class CreateCommentForItemHandler extends DefaultCreateHandler implements StrutsConstants {

    public CreateCommentForItemHandler() {}

    public void executeHandler(XModelObject object, Properties prop) throws Exception {
        if(!isEnabled(object)) return;
        String entity =  data[0].getModelEntity().getName();
        Properties p = extractProperties(data[0]);
        setOtherProperties(object, p);
        XModelObject c = XModelObjectLoaderUtil.createValidObject(object.getModel(), entity, p);
        addCreatedObject(object.getParent(), c, p);
		StrutsProcessStructureHelper.instance.showComments(object);
    }

    protected void setOtherProperties(XModelObject object, Properties p) {
        String name = XModelObjectUtil.createNewChildName("comment", object.getParent());
        p.setProperty("name", name);
        p.setProperty(ATT_TARGET, object.getAttributeValue("name"));
        StrutsProcessStructureHelper h = new StrutsProcessStructureHelper();
        int[] is = h.asIntArray(object, "shape");
        if(is == null || is.length < 2) is = new int[]{0, 0};
        is[0] += 50;
        is[1] = (is[1] < 110) ? is[1] + 100 : is[1] - 100;
        p.setProperty("shape", "" + is[0] + "," + is[1] + ",0,0"); 
    }

}

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
import org.jboss.tools.struts.model.helpers.StrutsProcessStructureHelper;

public class CreateCommentHandler  extends DefaultCreateHandler implements StrutsConstants {
    protected Properties pc = null;

    public CreateCommentHandler() {}

    public void executeHandler(XModelObject object, Properties p) throws Exception {
        pc = p;
        super.executeHandler(object, p);
		StrutsProcessStructureHelper.instance.showComments(object);
        pc = null;
    }

    protected void setOtherProperties(XModelObject object, Properties p) {
        String name = XModelObjectUtil.createNewChildName("comment", object);
        p.setProperty("name", name);
    }

    protected XModelObject modifyCreatedObject(XModelObject o) {
        setShape(o, pc);
        return o;
    }

    protected void setShape(XModelObject o, Properties p) {
        if(p == null) return;
        String x = p.getProperty("process.mouse.x");
        String y = p.getProperty("process.mouse.y");
        if(x == null || y == null) return;
        o.setAttributeValue("shape", "" + x + "," + y + ",0,0");
    }

}

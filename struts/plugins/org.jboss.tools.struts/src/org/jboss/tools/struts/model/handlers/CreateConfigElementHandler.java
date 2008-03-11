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
import org.jboss.tools.struts.model.*;
import org.jboss.tools.struts.model.helpers.*;
import org.jboss.tools.struts.webprj.model.helpers.*;

public class CreateConfigElementHandler extends DefaultCreateHandler implements StrutsConstants {
    protected XModelObject createdObject = null;

    public CreateConfigElementHandler() {}

    public void executeHandler(XModelObject object, Properties p) throws Exception {
        createdObject = null;
        if(!createUnconfirmedAction(object, p)) {
            executeHandler0(object, p);
        }
        setShape(object, p);
    }
    
    Properties runProperties = null;

    public void executeHandler0(XModelObject object, Properties p) throws Exception {
		runProperties = p;
		try {
			super.executeHandler(object, p);
		} finally {
			runProperties = null;
		}
    }

    protected XModelObject modifyCreatedObject(XModelObject o) {
        createdObject = o;
        if(runProperties != null && o != null) {
			String shape = getShape(runProperties);
			if(shape != null) {
				o.set("_shape", shape);
				setItemShape(o, shape);
			}		
        }
        return o;
    }

    protected void setOtherProperties(XModelObject object, Properties p) {
        if("CreateAction".equals(action.getName())) validatePathAttr(p);
    }

    public void validatePathAttr(Properties p) {
        String path = p.getProperty(ATT_PATH);
        if(path != null && path.length() > 0 && !path.startsWith("/") && !StrutsProcessHelper.isHttp(path)) {
            HUtil.find(data, 0, ATT_PATH).setValue("/" + path);
            p.setProperty(ATT_PATH, "/" + path);
        }
    }

    protected void setShape(XModelObject object, Properties p) {
        setItemShape(getCreatedItemOnProcess(), p);
    }

    static String getShape(Properties p) {
		if(p == null) return null;
		String x = p.getProperty("process.mouse.x");
		String y = p.getProperty("process.mouse.y");
		return (x == null || y == null) ? null : "" + x + "," + y + ",0,0";
    }

    public static void setItemShape(XModelObject object, Properties p) {
        String shape = getShape(p);
        if(object != null && shape != null) setItemShape(object, shape);
    }
    
    static void setItemShape(XModelObject object, String shape) {
		if(object.getModelEntity().getAttribute("shape") != null)
		  object.setAttributeValue("shape", shape);
		object.set("_shape", shape);
    } 

    protected XModelObject getCreatedItemOnProcess() {
        XModelObject o = SelectOnDiagramHandler.getItemOnProcess(createdObject);
        if(o == null) return o;
        String entity = o.getModelEntity().getName();
        if(entity.equals("StrutsProcessItem")) return o;
        return new StrutsProcessStructureHelper().getItemOutputTarget(o);
    }

    // create unconfirmed action

    private boolean createUnconfirmedAction(XModelObject object, Properties p) throws Exception {
        if(!"CreateAction".equals(action.getName())) return false;
        Properties p2 = extractProperties(data[0]);
        setOtherProperties(object, p2);
        String path = p2.getProperty(ATT_PATH);
        String module = WebModulesHelper.getInstance(object.getModel()).getModuleForPath(path, null);
        if(module == null) return false;

        XModelObject process = object.getParent().getChildByPath("process");
        p2.setProperty(ATT_NAME, XModelObjectUtil.createNewChildName("action", process));
        p2.setProperty(ATT_TYPE, TYPE_ACTION);
        p2.setProperty(ATT_SUBTYPE, SUBTYPE_UNKNOWN);
        p2.setProperty(ATT_PATH, path);
        XModelObject action = process.getModel().createModelObject(ENT_PROCESSITEM, p2);
        setItemShape(action, p);
        DefaultCreateHandler.addCreatedObject(process, action, p);
        StrutsProcessImpl pi = (StrutsProcessImpl)process;
        pi.getHelper().registerAction(action);
        return true;
    }

}


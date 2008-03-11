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
package org.jboss.tools.struts.plugins.model.handlers;

import java.util.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.options.PreferenceModelUtilities;
import org.jboss.tools.common.model.util.*;
import org.jboss.tools.common.meta.action.XActionInvoker;
import org.jboss.tools.common.meta.action.impl.*;

public class AddMessagesHandler extends AbstractHandler {

    public AddMessagesHandler() {}

    public boolean isEnabled(XModelObject object) {
        if(object == null && !object.isObjectEditable()) return false;
        XModelObject rs = getTemplate(PreferenceModelUtilities.getPreferenceModel());
        if(rs == null) return false;
        String dn = "" + getTemplateName(rs);
        ((XActionImpl)action).setDisplayName(dn);
        return true;
    }

    public void executeHandler(XModelObject object, Properties p) throws Exception {
        XModelObject rso = getTemplate(PreferenceModelUtilities.getPreferenceModel());
        if(rso == null) return;
        String text = rso.getAttributeValue("text");
        XModelObject rs = loadResource(object.getModel(), text);
        XModelObject[] ps = rs.getChildren();
        long t = object.getTimeStamp();
        for (int i = 0; i < ps.length; i++)
          object.addChild(ps[i]);
        if(t != object.getTimeStamp()) {
        	object.setModified(true);
			XActionInvoker.invoke("Open", object, new Properties());
        } 
    }

    private XModelObject getTemplate(XModel model) {
        int i = -1;
        try { 
        	i = Integer.valueOf(action.getProperty("index")).intValue(); 
        } catch (Exception e) {
        	//ignore
        }
        return getTemplate(model, i);
    }
    
    static XModelObject getTemplate(XModel model, int i) {
		if(i < 0) return null;
		XModelObject[] ps = model.getByPath("%Options%/Struts Studio/Automation/Resources Insets").getChildren();
		return (i < ps.length) ? ps[i] : null;
    }

    static XModelObject loadResource(XModel model, String body) {
        XModelObject p = model.createModelObject("FilePROPERTIES", null);
        XModelObjectLoaderUtil.setTempBody(p, body);
        XModelObjectLoaderUtil.getObjectLoader(p).load(p);
        return p;
    }
    
    static String getTemplateName(XModelObject template) {
		String dn = template.getAttributeValue("title");
		return (dn != null) ? dn : template.getAttributeValue("name");
    }

}

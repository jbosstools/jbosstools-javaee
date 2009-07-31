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
package org.jboss.tools.jsf.model.handlers;

import java.util.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.meta.action.*;
import org.jboss.tools.common.meta.action.impl.*;
import org.jboss.tools.jsf.model.JSFConstants;

public class HiddenLinksHandler extends AbstractHandler implements JSFConstants {

    public HiddenLinksHandler() {}

    public boolean isEnabled(XModelObject object) {
        if(object == null || !object.isObjectEditable()) return false;
        return true;
    }

    //! 'short' is equivalent to 'no'

    public void executeHandler(XModelObject object, Properties p) throws XModelException {
        if(!isEnabled(object)) return;
		SpecialWizard wizard = SpecialWizardFactory.createSpecialWizard("org.jboss.tools.jst.web.ui.wizards.links.HiddenLinksWizard"); //$NON-NLS-1$
        XModelObject[] links = getLinks(object);
        String[][] vs = new String[links.length][];
        for (int i = 0; i < vs.length; i++) {
          vs[i] = new String[]{links[i].getAttributeValue(ATT_PATH), links[i].getAttributeValue("hidden")}; //$NON-NLS-1$
        }
        if(p == null) p = new Properties();
        p.put("data", vs); //$NON-NLS-1$
        p.put("model", object.getModel()); //$NON-NLS-1$
        p.setProperty("help", "StrutsProcessItem_ShowHideLinks"); //$NON-NLS-1$ //$NON-NLS-2$
        wizard.setObject(p);
        if(wizard.execute() != 0) return;
        for (int i = 0; i < vs.length; i++) {
          if("yes".equals(links[i].getAttributeValue("hidden")) == "yes".equals(vs[i][1])) continue; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
          links[i].getModel().changeObjectAttribute(links[i], "hidden", vs[i][1]); //$NON-NLS-1$
        }
    }
    
    private XModelObject[] getLinks(XModelObject o) {
    	String entity = o.getModelEntity().getName();
    	if(ENT_PROCESS_ITEM.equals(entity)) {
    		return o.getChildren();
    	} else if(ENT_PROCESS_GROUP.equals(entity)) {
    		ArrayList<XModelObject> list = new ArrayList<XModelObject>();
    		XModelObject[] cs = o.getChildren();
    		for (int i = 0; i < cs.length; i++) {
    			XModelObject[] is = cs[i].getChildren();
    			for (int j = 0; j < is.length; j++) list.add(is[j]); 
    		}
    		return list.toArray(new XModelObject[0]);
    	}
    	return new XModelObject[0];
    }

}

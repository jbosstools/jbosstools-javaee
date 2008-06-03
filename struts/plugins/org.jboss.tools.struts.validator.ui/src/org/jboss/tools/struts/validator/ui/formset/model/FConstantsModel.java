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
package org.jboss.tools.struts.validator.ui.formset.model;

import java.util.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.struts.validators.model.ValidatorConstants;
import org.jboss.tools.struts.validators.model.XModelEntityResolver;

public class FConstantsModel extends FModel implements InheritanceStatus {
    protected Properties statuses = new Properties();

    public FConstantsModel() {}

	public void dispose() {
		super.dispose();
		if (statuses!=null) statuses.clear();
		statuses = null;
	}

	public boolean isChildInherited(XModelObject o) {
        String status = statuses.getProperty(o.getPathPart());
        return INHERITES.equals(status);
    }

    public boolean isChildOverriding(XModelObject o) {
        String status = statuses.getProperty(o.getPathPart());
        return OVERWRITES.equals(status);
    }
    
    public void reload() {
        XModelObject[] ps = parent.getModelObjects();
        if(fake == null && ps.length > 0)
          fake = ps[0].getModel().createModelObject("ValidationConstant", null);
        boolean differ = false;
        statuses = new Properties();
        List<XModelObject> l = new ArrayList<XModelObject>();
        Map<String,XModelObject> m = new HashMap<String,XModelObject>();
        for (int i = 0; i < ps.length; i++) {
            XModelObject[] cs = XModelEntityResolver.getResolvedChildren(ps[i], ValidatorConstants.ENT_CONSTANT);
            for (int j = 0; j < cs.length; j++) append(cs[j], l, m);
        }
        if(FSUtil.differ(objects, l)) {
            differ = true;
            objects = l.toArray(new XModelObject[0]);
        }
        isInherited = (!statuses.containsValue(OVERWRITES) && !statuses.containsValue(DEFINES));
        if(differ) fire(this);
    }

    private void append(XModelObject o, List<XModelObject> l, Map<String,XModelObject> m) {
        String p = o.getPathPart(), code = p;
        String status = statuses.getProperty(code);
        boolean inh = isInherited(o);
        if(inh) {
            if(status != null) {
                if(DEFINES.equals(status)) statuses.setProperty(code, OVERWRITES);
                return;
            }
            statuses.setProperty(code, INHERITES);
        } else if(INHERITES.equals(status)) {
            statuses.setProperty(code, OVERWRITES);
        } else {
            statuses.setProperty(code, DEFINES);
        }
        Object x = m.get(code);
        if(x != null) l.remove(x);
        m.put(code, o);
        l.add(o);
    }

    public String getKey() {
        return "Validation_Editor_Constants";
    }

}


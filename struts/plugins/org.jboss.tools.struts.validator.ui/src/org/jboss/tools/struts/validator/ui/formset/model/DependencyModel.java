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

public class DependencyModel extends FModel implements InheritanceStatus {
    protected XModelObject[] msgs = new XModelObject[0];
    protected XModelObject[] args = new XModelObject[0];
    protected XModelObject[] vars = new XModelObject[0];
    protected Properties statuses = new Properties(), codes = new Properties();

    public DependencyModel() {}

	public void dispose() {
		super.dispose();
		if (statuses!=null) statuses.clear();
		statuses = null;
		if (codes!=null) codes.clear();
		codes = null;
	}

	public void reload() {
        loadObjects(parent.getModelObjects(), name);
    }

    public XModelObject getModelObjectForIcon() {
        return fake;
    }

    public void loadObjects(XModelObject[] ps, String name) {
        if(fake == null && ps.length > 0)
          fake = ps[0].getModel().createModelObject("ValidationMsg", null);
        boolean differ = false;
        statuses = new Properties();
        codes.clear();
        List<XModelObject> l = new ArrayList<XModelObject>();
        Map<String,XModelObject> m = new HashMap<String,XModelObject>();
        for (int i = 0; i < ps.length; i++) {
            XModelObject[] cs = ps[i].getChildren();
            for (int j = 0; j < cs.length; j++) append(cs[j], l, m);
        }
        if(FSUtil.differ(objects, l)) {
            differ = true;
            objects = l.toArray(new XModelObject[0]);
        }

        l = new ArrayList<XModelObject>();
        for (int i = 0; i < objects.length; i++)
          if(objects[i].getModelEntity().getName().startsWith(ValidatorConstants.ENT_MSG)) l.add(objects[i]);
        if(FSUtil.differ(msgs, l)) {
            differ = true;
            msgs = l.toArray(new XModelObject[0]);
        }

        l = new ArrayList<XModelObject>();
        if(ps.length > 0 && ps[0].getModelEntity().getName().equals(ValidatorConstants.ENT_FIELD)) {
        	for (int arg = 0; arg < 4; arg ++) for (int i = 0; i < objects.length; i++) {
        		if(objects[i].getModelEntity().getName().equals("ValidationArg" + arg)) l.add(objects[i]);
        	}
        } else {
        	for (int i = 0; i < objects.length; i++) {
        		if(objects[i].getModelEntity().getName().startsWith(ValidatorConstants.ENT_ARG)) l.add(objects[i]);
        	}
        }
    	if(FSUtil.differ(args, l)) {
    		differ = true;
    		args = l.toArray(new XModelObject[0]);
    	}

        l = new ArrayList<XModelObject>();
        for (int i = 0; i < objects.length; i++)
          if(objects[i].getModelEntity().getName().startsWith(ValidatorConstants.ENT_VAR)) l.add(objects[i]);
        if(FSUtil.differ(vars, l)) {
            differ = true;
            vars = (XModelObject[])l.toArray(new XModelObject[0]);
        }

        boolean isInh = isInherited;
        isInherited = true;
        for (int i = 0; i < ps.length; i++) {
            if(parent.isInherited(ps[i])) continue;
            if(name.length() == 0) isInherited = false;
            else if(("," + ps[i].getAttributeValue("depends") + ",").indexOf("," + name + ",") >= 0) isInherited = false;
            if(!isInherited) break;
        }
        differ |= (isInh != isInherited);

        if(differ) fire(name.length() == 0 ? parent : this);
    }

    public XModelObject[] getMsgs() {
        return msgs;
    }

    public XModelObject[] getArgs() {
        return args;
    }

    public XModelObject[] getVars() {
        return vars;
    }

    private void append(XModelObject o, List<XModelObject> l, Map<String,XModelObject> m) {
        String entity = o.getModelEntity().getName();
        String n = o.getAttributeValue("name");
        boolean isvar = (n == null),
                all = name.length() == 0,
                isdef = !isvar && !all && (n.length() == 0);
        boolean relevant = (isvar || isdef || all || n.equals(name));
        if(!relevant) return;
        String p = (isvar) ? o.getAttributeValue("var-name") : (isdef) ? name : n;
        String code = entity + ":" + p;
        if(entity.equals("ValidationArg11")) code += ":" + o.getAttributeValue("position");
        String status = statuses.getProperty(code);
        boolean inh = isInherited(o) || isdef;
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
        codes.setProperty("" + o.hashCode(), code);
        Object x = m.get(code);
        if(x != null) l.remove(x);
        m.put(code, o);
        l.add(o);
    }

    public boolean isChildInherited(XModelObject o) {
        String code = codes.getProperty("" + o.hashCode());
        String status = (code == null) ? null : statuses.getProperty(code);
        return (code == null || INHERITES.equals(status));
    }

    public boolean isChildOverriding(XModelObject o) {
        String code = codes.getProperty("" + o.hashCode());
        String status = (code == null) ? null : statuses.getProperty(code);
        return (code == null || OVERWRITES.equals(status));
    }

    public String getKey() {
        return "Validation_Editor_Rule";
    }

}


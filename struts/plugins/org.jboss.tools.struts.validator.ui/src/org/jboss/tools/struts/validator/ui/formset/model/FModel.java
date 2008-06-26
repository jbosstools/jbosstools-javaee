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

import org.jboss.tools.common.model.*;
import org.jboss.tools.struts.validator.ui.XStudioValidatorPlugin;

public class FModel {
    protected FModel parent = null;
    protected String name = "";
    protected XModelObject[] objects = new XModelObject[0];
    protected FModel[] children = new FModel[0];
    protected boolean isInherited = false;
    protected boolean isInheriting = false;
    protected XModelObject fake = null;

    public FModel() {}
    
    public void dispose() {}

    public static FModel createInstance(Class c, FModel parent, String name) {
        FModel f = null;
        
        try {
			f = (FModel) c.newInstance();
		} catch (Exception e) {
			XStudioValidatorPlugin.getPluginLog().logError(e);			
		}
		
        f.parent = parent;
        f.name = name;
        return f;
    }

    public XModel getModel() {
        return (parent == null) ? null : parent.getModel();
    }

    public boolean isEditable() {
        return parent != null && parent.isEditable();
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return (parent == null) ? name : parent.getPath() + "/" + name.replace('/', '#');
    }
    
    public FModel getChild(String name) {
    	for (int i = 0; i < children.length; i++) 
    		if(children[i].getName().equals(name)) return children[i];
    	return null;
    }

    public boolean isInherited() {
        return isInherited;
    }

    public boolean isOverriding() {
        return !isInherited && isInheriting;
    }

    public FModel getParent() {
        return parent;
    }

    public XModelObject[] getModelObjects() {
        return objects;
    }

    public XModelObject getFakeObject() {
        return fake;
    }

    public XModelObject getModelObjectForIcon() {
        return (objects.length == 0) ? fake : objects[0];
    }

    public int getChildCount() {
        return children.length;
    }

    public FModel getChildAt(int i) {
        return children[i];
    }

    public void reload() {}

    public void fire(FModel source) {
        parent.fire(source);
    }

    protected boolean isInherited(XModelObject object) {
        return object == null || parent.isInherited(object.getParent());
    }

    public String toString() {
        return name;
    }

    public String getKey() {
        return "Validation_Editor_Object";
    }

}
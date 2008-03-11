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
package org.jboss.tools.jsf.model.impl;

import java.util.Properties;

import org.jboss.tools.common.meta.action.impl.handlers.DefaultCreateHandler;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.impl.*;

public class ApplicationObjectImpl extends OrderedObjectImpl {
	private static final long serialVersionUID = 6887421764622727627L;
	
	static String ATTR_CLASS_NAME = "class name";
	static Properties ATTR_TO_ENTITY  = new Properties();
	
	static {
		ATTR_TO_ENTITY.setProperty("variable-resolver", "JSFVariableResolver");
		ATTR_TO_ENTITY.setProperty("property-resolver", "JSFPropertyResolver");
		ATTR_TO_ENTITY.setProperty("el-resolver", "JSFELResolver");
	}

	public String name() {
		return "application";
	}
	
    public String getPresentationString() {
        return "Application";
    }

	public String getAttributeValue(String name) {
		if(name != null && ATTR_TO_ENTITY.containsKey(name)) {
			String entity = ATTR_TO_ENTITY.getProperty(name);
			XModelObject[] cs = getChildren(entity);
			return cs.length == 0 ? "" : cs[0].getAttributeValue(ATTR_CLASS_NAME);
		}
		return super.getAttributeValue(name);
	}
	
	public String setAttributeValue(String name, String value) {
		if(name != null && ATTR_TO_ENTITY.containsKey(name)) {
			//this means that any value is accepted
			return value;
		}
		return super.setAttributeValue(name, value);
	}
	
	public void onAttributeValueEdit(String name, String oldValue, String newValue) {
		if(name != null && ATTR_TO_ENTITY.containsKey(name)) {
			String entity = ATTR_TO_ENTITY.getProperty(name);
			XModelObject[] cs = getChildren(entity);
			if(cs.length > 0) {
				getModel().editObjectAttribute(cs[0], ATTR_CLASS_NAME, newValue);
			} else {
				XModelObject c = getModel().createModelObject(entity, null);
				c.setAttributeValue(ATTR_CLASS_NAME, newValue);
				DefaultCreateHandler.addCreatedObject(this, c, -1);
			}			
		}
	}

}

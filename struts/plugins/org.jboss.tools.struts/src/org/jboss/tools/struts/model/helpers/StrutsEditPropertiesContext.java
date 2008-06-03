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
package org.jboss.tools.struts.model.helpers;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

import java.util.*;
import org.jboss.tools.common.meta.action.XAttributeData;
import org.jboss.tools.common.meta.action.XEntityData;
import org.jboss.tools.common.model.XModelObject;

public class StrutsEditPropertiesContext {
    private XModelObject object = null;
    private XEntityData entityData = null;
    private Map<String,XAttributeData> attributeData = new HashMap<String,XAttributeData>();
    
    public void dispose() {
    	if (attributeData!=null) attributeData.clear();
    	attributeData = null;
    }

    public XAttributeData[] getAttributeData() {
        return entityData.getAttributeData();
    }

    public XAttributeData getAttributeData(String name) {
        return (XAttributeData)attributeData.get(name);
    }

    // For properties
    public void setValue(String name, String value) {
        getAttributeData(name).setValue(value);
    }

    public String getValue(String name) {
        return getAttributeData(name).getValue();
    }

    public void setObject(XModelObject object, XEntityData entityData) {
        this.object = object;
        this.entityData = entityData;
        XAttributeData[] attrs = entityData.getAttributeData();
        for (int i = 0; attrs != null && i < attrs.length; i++) {
            String attrName = attrs[i].getAttribute().getName();
            String attrValue = object.getAttributeValue(attrName);
            attributeData.put(attrName, attrs[i]);
            attrs[i].setValue(attrValue);
        }
    }

    public XModelObject getObject() {
        return this.object;
    }

    public XEntityData getEntityData() {
        return this.entityData;
    }
}
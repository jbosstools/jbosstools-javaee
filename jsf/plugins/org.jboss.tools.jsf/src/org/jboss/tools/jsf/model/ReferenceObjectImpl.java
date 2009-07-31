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
package org.jboss.tools.jsf.model;

import org.eclipse.swt.graphics.Image;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.impl.*;
import org.jboss.tools.jst.web.model.ReferenceObject;

/**
 * @author  valera
 */
public class ReferenceObjectImpl extends OrderedObjectImpl implements ReferenceObject, JSFConstants {
	private static final long serialVersionUID = 2473449103657311162L;
    protected XModelObject reference;
    protected long referenceTimeStamp = -1;

    public ReferenceObjectImpl() {}

    public XModelObject getReference() {
        return reference;
    }

    public void setReference(XModelObject reference) {
    	if(this.reference != reference) {
			referenceTimeStamp = -1;
    	}
        this.reference = reference;
        if(reference != null) {
            String shape = get("SHAPE"); //$NON-NLS-1$
            if(shape != null && shape.length() > 0) reference.set("_shape", shape); //$NON-NLS-1$
        }
    }
    
    public boolean isUpToDate() {
    	return reference == null || reference.getTimeStamp() == referenceTimeStamp;
    }
    
    public void notifyUpdate() {
		referenceTimeStamp = (reference == null) ? -1 : reference.getTimeStamp();
    }

    public String getPresentationString() {
        String title = (reference != null) ? reference.getPresentationString() :
/*                  (TYPE_ACTION.equals(getAttributeValue(ATT_TYPE)) ||
                   TYPE_PAGE.equals(getAttributeValue(ATT_TYPE))
                  ) ? getAttributeValue(ATT_PATH) : */
					getAttributeValue("title"); //$NON-NLS-1$
        if(title == null) title = getAttributeValue(ATT_NAME);
        return "" + title; //$NON-NLS-1$
    }

    public String getMainIconName() {
/*
        String type = getAttributeValue(ATT_TYPE);
        if(type == null || type.length() == 0) return super.getMainIconName();
        if(type.equals(TYPE_ACTION)) {
            String subtype = getAttributeValue(ATT_SUBTYPE);
            return "main.struts.action";
        }
        if(type.equals(TYPE_FORWARD)) return "main.struts.forward";
        if(type.equals(TYPE_EXCEPTION)) return "main.struts.exception";
        if(type.equals(TYPE_PAGE)) {
            String subtype = getAttributeValue(ATT_SUBTYPE);
            if(subtype == null) {}
            else if(subtype.equals(SUBTYPE_JSP)) return "main.file.jsp_file";
            else if(subtype.equals(SUBTYPE_HTML)) return "main.file.html_file";
            else if(subtype.equals(SUBTYPE_TILE)) return "main.struts.tiles.definition";
            else return "main.file.unknow_file";
        } else if(type.equals(TYPE_LINK)) {
            String subtype = getAttributeValue(ATT_SUBTYPE);
            if(subtype == null) {}
            else if(subtype.equals(SUBTYPE_FORWARD)) return "main.struts.forward";
            else if(subtype.equals(TYPE_EXCEPTION)) return "main.struts.exception";
        }
*/
        return super.getMainIconName();
    }

    public Image getImage() {
        return (reference != null) ? reference.getImage() : super.getImage();
    }

    public void set(String name, String value) {
        if("SHAPE".equals(name) && reference != null) { //$NON-NLS-1$
            reference.set("_shape", value); //$NON-NLS-1$
        }
        super.set(name, value);
    }

}


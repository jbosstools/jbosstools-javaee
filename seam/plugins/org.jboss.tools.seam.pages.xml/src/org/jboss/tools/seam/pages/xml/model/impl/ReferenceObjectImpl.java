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
package org.jboss.tools.seam.pages.xml.model.impl;

import org.eclipse.swt.graphics.Image;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.impl.*;
import org.jboss.tools.jst.web.model.ReferenceObject;
import org.jboss.tools.seam.pages.xml.model.SeamPagesConstants;

public class ReferenceObjectImpl extends OrderedObjectImpl implements ReferenceObject, SeamPagesConstants {
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
        	String shape = get("SHAPE");
        	if(shape != null && shape.length() > 0) reference.set("_shape", shape);
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
				TYPE_PAGE.equals(getAttributeValue(ATTR_TYPE))
				|| TYPE_EXCEPTION.equals(getAttributeValue(ATTR_TYPE))
				? getAttributeValue(ATTR_PATH) : 
					getAttributeValue("title");
		if(title == null) title = getAttributeValue(ATTR_NAME);
		return "" + title;
	}

	public String getMainIconName() {
		
		if(reference != null && TYPE_EXCEPTION.equals(getAttributeValue(ATTR_TYPE))) {
			return reference.getMainIconName();
		}
        return super.getMainIconName();
    }

    public Image getImage() {
        return (reference != null) ? reference.getImage() : super.getImage();
    }

    public void set(String name, String value) {
        if("SHAPE".equals(name) && reference != null) {
            reference.set("_shape", value);
        }
        super.set(name, value);
    }

    public void fireReferenceChanged() {
    	changeTimeStamp();
    	fireObjectChanged("reference");
    }
}


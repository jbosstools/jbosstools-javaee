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

import org.jboss.tools.common.model.impl.OrderedByEntityChildren;
import org.jboss.tools.common.model.impl.OrderedObjectImpl;
import org.jboss.tools.common.model.impl.RegularChildren;

public class ConverterObjectImpl extends OrderedObjectImpl {
    private static final long serialVersionUID = 3379037286367131353L;

    protected RegularChildren createChildren() {
        return new OrderedByEntityChildren();
    }
    
	public String name() {
		String id = "" + getAttributeValue("converter-id");
		if(id != null && id.length() > 0) return id;
		String cfc = "" + getAttributeValue("converter-for-class");
		if(cfc != null && cfc.length() > 0) return cfc;
		return "" + System.identityHashCode(this);
	}
	
	public String getPresentationString() {
		String id = "" + getAttributeValue("converter-id");
		if(id != null && id.length() > 0) return id;
		String cfc = "" + getAttributeValue("converter-for-class");
		if(cfc != null && cfc.length() > 0) return cfc;
		return "" + getAttributeValue("converter-class");		
	}

	protected void onAttributeValueEdit(String name, String oldValue, String newValue) {
		if("converter-id".equals(name) && newValue != null && newValue.length() > 0) {
			setAttributeValue("converter-for-class", "");
		} else if("converter-for-class".equals(name) && newValue != null && newValue.length() > 0) {
			setAttributeValue("converter-id", "");
		}		
	}

}

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
package org.jboss.tools.struts.model;

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.impl.*;

public class ActionObjectImpl extends OrderedObjectImpl {
	private static final long serialVersionUID = 6533003923654651013L;

	public ActionObjectImpl() {}

    public String getPathPart() {
        String path = getAttributeValue("path");
        return path == null ? ""+System.identityHashCode(this) : path.replace('/', '#');
    }

    public String getPresentationString() {
        return getAttributeValue("path");
    }

    protected RegularChildren createChildren() {
        return new OrderedByEntityChildren();
    }

	protected void onAttributeValueEdit(String name, String oldValue, String newValue) {
		if("unknown".equals(name) && "true".equals(newValue)) {
			if(!isActive()) return;
			XModelObject[] s = getParent().getChildren(getModelEntity().getName());
			for (int i = 0; i < s.length; i++) {
				if(s[i] == this) continue;
				if("true".equals(s[i].getAttributeValue("unknown"))) {
					getModel().changeObjectAttribute(s[i], "unknown", "false");
				}
			}
		}
	}

}


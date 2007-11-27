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

import org.jboss.tools.common.model.impl.*;

public class DataSourceObjectImpl extends OrderedObjectImpl {
	private static final long serialVersionUID = 3283151461471400332L;

    public DataSourceObjectImpl() {}

    public String getPathPart() {
        return ""+System.identityHashCode(this);
    }

    public String getPresentationString() {
        String key = getAttributeValue("key");
        if (key == null || key.trim().length() == 0) {
            key = "org.apache.struts.action.DATA_SOURCE";
        }
        return key;
    }
}


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

public class NamedObjectImpl extends OrderedObjectImpl {
	private static final long serialVersionUID = 966849608361197338L;

    public NamedObjectImpl() {}

    public String getPathPart() {
    	String n = getAttributeValue("name");
        return n == null ? "" : n.replace('/', '#');
    }

    public String getPresentationString() {
        return getAttributeValue("name");
    }
}


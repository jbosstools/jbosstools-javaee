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

public class MessageResourcesObjectImpl extends OrderedObjectImpl {
	private static final long serialVersionUID = 8629523294404964615L;

    public MessageResourcesObjectImpl() {}

    public String getPathPart() {
        return ""+System.identityHashCode(this);
    }

    public String getPresentationString() {
        return getAttributeValue("parameter");
    }
}


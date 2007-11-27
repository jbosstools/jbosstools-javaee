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

public class SubsetObjectImpl extends OrderedObjectImpl {
	private static final long serialVersionUID = 5546020939978855205L;

    public SubsetObjectImpl() {}

    public String getPathPart() {
        return getAttributeValue("name");
    }

    public String getPresentationString() {
        return getAttributeValue("name");
    }
}


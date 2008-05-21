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
package org.jboss.tools.struts.validators.model;

import org.jboss.tools.common.model.impl.*;

public class ValidatorMsgImpl extends RegularObjectImpl {
	private static final long serialVersionUID = 5344296805192016590L;

    public String name() {
        String n = get("name");
        String p = getModelEntity().getXMLSubPath();
        return (n == null || n.length() == 0) ? p : p + " for " + n;
    }

}


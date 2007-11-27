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

import org.jboss.tools.common.model.impl.OrderedObjectImpl;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;

public class ExtensionObjectImpl extends OrderedObjectImpl {
    private static final long serialVersionUID = 7395454540297918160L;
	
	public String getPresentationString() {
		return "" + getModelEntity().getXMLSubPath();
	}

	public String name() {
		return "" + getModelEntity().getXMLSubPath() + get(XModelObjectLoaderUtil.ATTR_ID_NAME);
	}

}

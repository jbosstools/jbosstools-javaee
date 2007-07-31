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

import org.jboss.tools.common.model.impl.RegularObjectImpl;

public class FacetObjectImpl extends RegularObjectImpl {
    private static final long serialVersionUID = 534845753792987818L;

	public String name() {
		return "facet:" + getAttributeValue("facet-name");
	}

    public String getPresentationString() {
    	return "" + getAttributeValue("facet-name");
    }
}

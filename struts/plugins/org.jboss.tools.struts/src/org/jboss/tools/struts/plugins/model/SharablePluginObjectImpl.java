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
package org.jboss.tools.struts.plugins.model;

import org.jboss.tools.common.model.options.impl.*;

public class SharablePluginObjectImpl extends SharableElementImpl {
	private static final long serialVersionUID = 1576948677460450902L;

	public SharablePluginObjectImpl() {}

    public String get(String name) {
        if(name.equals("title")) name = "NAME";
        return super.get(name);
    }

    public void set(String name, String value) {
        if(name.equals("title")) {
            name = "NAME";
        }
        super.set(name, value);
    }
    
}
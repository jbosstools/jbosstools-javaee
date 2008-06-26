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

public class ValidatorFormsetImpl extends ValidatorGlobalImpl {
	private static final long serialVersionUID = 8346157853523965817L;

    public String getPresentationString() {
        String l = get("language"), c = get("country");
        if(l == null || l.equals("default")) l = "";
        if(c == null || c.equals("default")) c = "";
        String lc = (l.length() == 0 && c.length() == 0) ? "default" : l + "_" + c;
        return "" + get_0("element type") + " (" + lc + ")";
    }

}

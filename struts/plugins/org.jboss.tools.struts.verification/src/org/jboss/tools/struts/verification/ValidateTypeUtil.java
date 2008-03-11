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
package org.jboss.tools.struts.verification;

import org.eclipse.jdt.core.IType;

import org.jboss.tools.common.verification.vrules.*;

public class ValidateTypeUtil {
    public static int OK = 0;
    public static int EMPTY = 1;
    public static int NOT_FOUND = 2;
    public static int WRONG_SUPER = 3;
    public static int NOT_UPTODATE = 4;
    VObject src;

    public ValidateTypeUtil() {}

    public int checkClass(VObject o, String attr, String sup) {
        String type = "" + o.getAttribute(attr);
        if(type.length() == 0) return EMPTY;
       	IType c = o.getModel().getValidType(type);
       	if(c == null) return NOT_FOUND;
           if(sup != null) try {
//			IType sc = o.getModel().getValidType(sup);
				
            ///if(!sc.isAssignableFrom(c)) return WRONG_SUPER;
        } catch (Exception t) {
        	StrutsVerificationPlugin.getPluginLog().logError(t);
        }
/*
        VObject cls = model.getObjectByPath("/"+type.replace('.', '/')+".class");
        src = model.getObjectByPath("/"+type.replace('.', '/')+".java");
        if (cls != null && src != null) {
            if (cls.getTimeStamp() < src.getTimeStamp()) {
                VResult result = factory.getResult("uptodate", object,
                    "type", src, null, new Object[] {object, type});
                return NOT_UPTODATE;
            }
        }
*/
        return OK;
    }

}

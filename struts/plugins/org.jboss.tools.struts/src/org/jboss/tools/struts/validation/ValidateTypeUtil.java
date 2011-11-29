/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.struts.validation;

import org.eclipse.jdt.core.IType;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.jst.web.validation.CheckClass;
import org.jboss.tools.struts.StrutsModelPlugin;

public class ValidateTypeUtil {
    public static int OK = 0;
    public static int EMPTY = 1;
    public static int NOT_FOUND = 2;
    public static int WRONG_SUPER = 3;
    public static int NOT_UPTODATE = 4;

    public ValidateTypeUtil() {}

    public int checkClass(XModelObject o, String attr, String sup) {
        String type = "" + o.getAttributeValue(attr);
        if(type.length() == 0) return EMPTY;
       	IType c = CheckClass.getValidType(type, o);
       	if(c == null) return NOT_FOUND;
           if(sup != null) try {
//			IType sc = o.getModel().getValidType(sup);
				
            ///if(!sc.isAssignableFrom(c)) return WRONG_SUPER;
        } catch (Exception t) {
        	StrutsModelPlugin.getPluginLog().logError(t);
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

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
package org.jboss.tools.struts.validator.ui.formset.model;

import java.util.*;
import org.jboss.tools.common.model.*;

public class FSUtil {

    public static XModelObject[] getChildren(XModelObject[] ps, String name) {
        List<XModelObject> l = new ArrayList<XModelObject>();
        for (int i = 0; i < ps.length; i++) {
            XModelObject o = ps[i].getChildByPath(name.replace('/', '#'));
            if(o != null) l.add(o);
        }
        return l.toArray(new XModelObject[0]);
    }

    public static boolean differ(Object[] a, List b) {
        if(a.length != b.size()) return true;
        for (int i = 0; i < a.length; i++)
          if(a[i] != b.get(i)) return true;
        return false;
    }

    public static boolean differ(Object[] a, Object[] b) {
        if(a.length != b.length) return true;
        for (int i = 0; i < a.length; i++)
          if(a[i] != b[i]) return true;
        return false;
    }

    public static boolean differ(boolean[] b1, boolean[] b2) {
        if(b1.length != b2.length) return true;
        for (int i = 0; i < b1.length; i++) if(b1[i] != b2[i]) return true;
        return false;
    }

}

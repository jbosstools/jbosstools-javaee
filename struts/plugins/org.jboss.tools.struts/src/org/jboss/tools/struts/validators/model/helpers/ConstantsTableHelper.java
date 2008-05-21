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
package org.jboss.tools.struts.validators.model.helpers;

import java.util.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.util.*;
import org.jboss.tools.struts.validators.model.ValidatorConstants;
import org.jboss.tools.struts.validators.model.XModelEntityResolver;

public class ConstantsTableHelper extends AbstractTableHelper {
    static String[] header = new String[]{"constant-name", "constant-value"};
    long timeStamp = -1;
    protected XModelObject[] cs = new XModelObject[0];

    public ConstantsTableHelper() {}

    public String[] getHeader() {
        return header;
    }

    public int size() {
        validate();
        return cs.length;
    }

    public XModelObject getModelObject(int r) {
        validate();
        return (r < 0 || r >= cs.length) ? null : cs[r];
    }

    protected String entity() {
        return ValidatorConstants.ENT_CONSTANT;
    }

    private void validate() {
        long t = (object == null) ? -1 : object.getTimeStamp();
        if(t == timeStamp) return;
        timeStamp = t;
        if(object == null) {
            cs = new XModelObject[0];
        } else {
            List<XModelObject> list = new ArrayList<XModelObject>();
            XModelObject[] gs = XModelEntityResolver.getResolvedChildren(object, ValidatorConstants.ENT_GLOBAL);
            for (int i = 0; i < gs.length; i++) {
                XModelObject[] ws = XModelEntityResolver.getResolvedChildren(gs[i], entity());
                for (int j = 0; j < ws.length; j++)
                  list.add(ws[j]);
            }
            cs = list.toArray(new XModelObject[0]);
        }
    }

}

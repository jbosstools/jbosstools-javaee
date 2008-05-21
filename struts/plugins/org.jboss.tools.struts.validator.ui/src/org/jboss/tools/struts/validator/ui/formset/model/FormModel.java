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
import org.jboss.tools.struts.validators.model.ValidatorConstants;
import org.jboss.tools.struts.validators.model.XModelEntityResolver;

public class FormModel extends FModel {

    public FormModel() {}

    public void reload() {
        objects = FSUtil.getChildren(parent.getModelObjects(), name);

        Map<String,FModel> c = new HashMap<String,FModel>();
        for (int i = 0; i < children.length; i++)
          c.put(children[i].getName(), children[i]);
        Set<String> q = new HashSet<String>();
        ArrayList<FModel> l2 = new ArrayList<FModel>();
        for (int i = 0; i < objects.length; i++) {
            XModelObject[] fs = XModelEntityResolver.getResolvedChildren(objects[i], ValidatorConstants.ENT_FIELD); 
            for (int j = 0; j < fs.length; j++) {
                String n = fs[j].getAttributeValue("property");
                if(q.contains(n)) continue;
                q.add(n);
                FModel f = (FModel)c.remove(n);
                if(f == null) f = FModel.createInstance(FieldModel.class, this, n);
                f.reload();
                l2.add(f);
            }
        }
        if(FSUtil.differ(children, l2)) {
            children = l2.toArray(new FModel[0]);
            parent.fire(this);
        }
        isInherited = objects.length == 0 || parent.isInherited(objects[0].getParent());
        isInheriting = false;
        for (int i = 0; i < objects.length && !isInheriting; i++) isInheriting = isInherited(objects[i]);
    }

    public String getKey() {
        return "Validation_Editor_Form";
    }

}

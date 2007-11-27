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
package org.jboss.tools.struts.model.handlers;

import java.util.*;
import org.jboss.tools.struts.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.meta.action.impl.*;
import org.jboss.tools.common.model.undo.*;

public class SetUnknownActionHandler extends AbstractHandler implements StrutsConstants {

    public SetUnknownActionHandler() {}

    public void executeHandler(XModelObject object, Properties p) throws Exception {

        if (object == null) return;

        XUndoManager undo = object.getModel().getUndoManager();
        XTransactionUndo u = new XTransactionUndo("Change unknown action", XTransactionUndo.EDIT);
        undo.addUndoable(u);

        try {
            if (isUnknown(object.getAttributeValue(ATT_UNKNOWN))) {
                object.getModel().changeObjectAttribute(object, ATT_UNKNOWN, "false");
            }else{
                XModelObject   parent = object.getParent();
                XModelObject[] children = parent.getChildren();
                for (int i=0; i<children.length; i++){

                    if (children[i]==object || !isUnknown(children[i].getAttributeValue(ATT_UNKNOWN))){
                       continue;
                    }
                    children[i].getModel().changeObjectAttribute(children[i], ATT_UNKNOWN, "false");
                }
                object.getModel().changeObjectAttribute(object, ATT_UNKNOWN, "true");
            }
        } catch (Exception e) {
            undo.rollbackTransactionInProgress();
            throw e;
        } finally {
            u.commit();
        }

    }

    public boolean isEnabled(XModelObject obj) {
        if (obj==null || !obj.isObjectEditable()) return false;

        if (isUnknown(obj.getAttributeValue(ATT_UNKNOWN)))
            ((XActionImpl)action).setDisplayName("Reset unknown");
        else
            ((XActionImpl)action).setDisplayName("Set as unknown");

        return true;
    }

    protected boolean isUnknown(String value){
        return (value != null && (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes")) );
    }

    public boolean isEnabled(XModelObject object, XModelObject[] objects) {
        if(object == null || objects == null || objects.length < 2) return isEnabled(object);
        return false;
    }


}//class ====================================================================
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
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.meta.action.impl.handlers.*;
import org.jboss.tools.struts.*;
import org.jboss.tools.struts.model.*;
import org.jboss.tools.struts.model.helpers.StrutsProcessStructureHelper;
import org.jboss.tools.common.model.undo.*;

public class RestoreUnconfirmedActionHandler extends DefaultCreateHandler implements StrutsConstants {

    public RestoreUnconfirmedActionHandler() {}

    public boolean isEnabled(XModelObject object) {
        if(object == null || !object.isActive() || !object.isObjectEditable()) return false;
        if(!(object instanceof ReferenceObjectImpl)) return false;
        ReferenceObjectImpl ro = (ReferenceObjectImpl)object;
        if(ro.getReference() != null) return false;
        String tp = object.getAttributeValue(ATT_TYPE);
        if(!TYPE_ACTION.equals(tp)) return false;
        String st = object.getAttributeValue(ATT_SUBTYPE);
        if(!SUBTYPE_UNKNOWN.equals(st)) return false;
        String path = object.getAttributeValue(ATT_PATH);
        if(path.indexOf('/') < 0 || path.indexOf('/') != path.lastIndexOf('/')) return false;
        boolean b = StrutsProcessStructureHelper.instance.isActionFromOtherConfigOfTheSameModule(object);
        if(b) return false;
        return true;
    }

    public void executeHandler(XModelObject object, Properties p) throws Exception {
        if(!isEnabled(object)) return;
        StrutsProcessImpl process = (StrutsProcessImpl)object.getParent();
        XModelObject parent = process.getParent().getChildByPath(ELM_ACTIONMAP);
        if(parent == null) return;
        String entity = parent.getModelEntity().getChildren()[0].getName();
        XModelObject c = object.getModel().createModelObject(entity, null);
        c.setAttributeValue(ATT_PATH, object.getAttributeValue(ATT_PATH));

        boolean b = parent.addChild(c);
        if(!b) throw new RuntimeException("Cannot add object.");
        XUndoManager undo = parent.getModel().getUndoManager();
        undo.addUndoable(new RU(process, parent, c));
        c.setModified(true);

        process.getHelper().reloadAction(process, c, object);
    }

    class RU extends XCreateUndo {
        private XModel m;
        String pp;
        public RU(XModelObject process, XModelObject parent, XModelObject child) {
            super(parent, child);
            pp = process.getPath();
            m = process.getModel();
        }
        public void doUndo() {
            super.doUndo();
            try {
              XModelObject process = m.getByPath(pp);
              if(!(process instanceof StrutsProcessImpl)) return;
              ((StrutsProcessImpl)process).getHelper().updateProcess();
            } catch (Exception e) {
            	StrutsModelPlugin.getPluginLog().logError(e);
            }
        }
        
        public void doRedo() {
            super.doRedo();
            try {
                XModelObject process = m.getByPath(pp);
                if(!(process instanceof StrutsProcessImpl)) return;
                ((StrutsProcessImpl)process).getHelper().updateProcess();
            } catch (Exception e) {
            	StrutsModelPlugin.getPluginLog().logError(e);
            }
        }
    }

}

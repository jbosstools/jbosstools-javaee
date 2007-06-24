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
import org.jboss.tools.struts.model.*;
import org.jboss.tools.common.model.undo.*;
import org.jboss.tools.common.model.filesystems.XFileObject;
import org.jboss.tools.common.model.impl.*;
import org.jboss.tools.struts.StrutsConstants;

public class StrutsMoveHandler extends MoveHandler implements StrutsConstants {

    public StrutsMoveHandler() {}

    public boolean isEnabled(XModelObject object) {
        if(isMoveOfLink(object)) return true;
        if (object instanceof ReferenceObjectImpl) {
            XModelObject ref = ((ReferenceObjectImpl)object).getReference();
            if (ref == null) {
                String tp = object.getAttributeValue(ATT_TYPE);
                if(TYPE_LINK.equals(tp)) return super.isEnabled(object);
                return false;
            }
            object = ref;
        }
        return super.isEnabled(object);
    }

    private boolean isMoveOfLink(XModelObject object) {
        if(!(object instanceof ReferenceObjectImpl)) return false;
        String tp = object.getAttributeValue(ATT_TYPE);
        return TYPE_LINK.equals(tp) && super.isEnabled(object);
    }

    public void executeHandler(XModelObject object, Properties prop) throws Exception {
        if(!isEnabled(object)) return;
        if(isMoveOfLink(object)) {
            super.executeHandler(object, prop);
            return;
        }
        XModelObject[] ps = getParticipants(object);
        if(ps != null) executeTransaction(ps);
    }

    private XModelObject findObjectByRef(XModelObject ref) {
        XModelObject f = ref;
        while(f != null && f.getFileType() < XFileObject.FILE) f = f.getParent();
        return (f == null) ? null : findObjectByRef(f.getChildByPath("process"), ref);
    }

    private XModelObject findObjectByRef(XModelObject p, XModelObject ref) {
        if(p instanceof ReferenceObjectImpl) {
            ReferenceObjectImpl o = (ReferenceObjectImpl)p;
            if(o.getReference() == ref) return p;
        }
        XModelObject[] cs = p.getChildren();
        for (int i = 0; i < cs.length; i++) {
            XModelObject o = findObjectByRef(cs[i], ref);
            if(o != null) return o;
        }
        return null;
    }

    public void executeTransaction(XModelObject[] ps) throws Exception {
        XUndoManager undo = ps[0].getModel().getUndoManager();
        XTransactionUndo u = new XTransactionUndo("move " + DefaultCreateHandler.title(ps[1], false), XTransactionUndo.EDIT);
        undo.addUndoable(u);
        try {
            transaction(ps);
        } catch (Exception e) {
            undo.rollbackTransactionInProgress();
            throw e;
        } finally {
            u.commit();
        }
    }
    private void transaction(XModelObject[] ps) throws Exception {
        XModelObject p = ps[0].getParent();
        XOrderedObject oo = (XOrderedObject)p;
        int to1 = oo.getIndexOfChild(ps[0]);
        int from1 = oo.getIndexOfChild(ps[1]);
        oo.move(from1, to1, true);
        ps[0].getModel().getUndoManager().addUndoable(new XMoveUndo(p, from1, to1));

        p = ps[2].getParent();
        oo = (XOrderedObject)p;
        int to2 = oo.getIndexOfChild(ps[2]);
        int from2 = oo.getIndexOfChild(ps[3]);
        if((from1 > to1) == (from2 > to2)) {
            oo.move(from2, to2, true);
            ps[0].getModel().getUndoManager().addUndoable(new XMoveUndo(p, from2, to2));
        }
    }

    private XModelObject[] getParticipants(XModelObject object) {
        XModelObject[] ps = new XModelObject[4];
        ps[0] = object;
        ps[2] = (ps[0] instanceof ReferenceObjectImpl) ?
                ((ReferenceObjectImpl)ps[0]).getReference() : findObjectByRef(ps[0]);
        if(ps[2] == null) return null;
        ps[1] = getSource(object.getModel());
        ps[3] = (ps[1] instanceof ReferenceObjectImpl) ?
                ((ReferenceObjectImpl)ps[1]).getReference() : findObjectByRef(ps[1]);
        if(ps[3] == null) return null;
        return ps;
    }

    private XModelObject getSource(XModel model) {
        XModelObject s = model.getModelBuffer().source();
        if(StrutsCopyHandler.referentBuffer == null) return s;
        XModelObject r = model.getByPath(StrutsCopyHandler.referentBuffer);
        if(!(r instanceof ReferenceObjectImpl)) return s;
        ReferenceObjectImpl ro = (ReferenceObjectImpl)r;
        return (ro.getReference() == s) ? ro : s;
    }

}

class XMove2Undo extends XMoveUndo {
    protected String refpath;

    public XMove2Undo(XModelObject ref, XModelObject object, int from, int to) {
        super(object, from, to);
        refpath = ref.getPath();
    }

    protected void execute(int f, int t) {
        XModelObject object = model.getByPath(path);
        if(!(object instanceof OrderedObjectImpl)) return;
        XModelObject ref = model.getByPath(refpath);
        if(!(ref instanceof OrderedObjectImpl)) return;
        OrderedObjectImpl oo = (OrderedObjectImpl)object;
        oo.move(f, t, true);
        oo = (OrderedObjectImpl)ref;
        oo.move(f, t, true);
    }

}


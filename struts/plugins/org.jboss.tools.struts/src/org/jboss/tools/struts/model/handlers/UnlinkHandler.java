/*
 * UnlinkHandler.java
 *
 * Created on February 26, 2003, 10:17 AM
 */

package org.jboss.tools.struts.model.handlers;

import org.jboss.tools.struts.StrutsConstants;
import org.jboss.tools.struts.model.*;
import org.jboss.tools.common.meta.action.impl.handlers.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.undo.*;
import java.util.Properties;
import org.jboss.tools.struts.model.handlers.page.*;

/**
 *
 * @author  valera
 */
public class UnlinkHandler extends DefaultEditHandler implements StrutsConstants {
    DeletePageLinkHandler deleteLink = new DeletePageLinkHandler();

    /** Creates a new instance of UnlinkHandler */
    public UnlinkHandler() {
    }
    
    public void executeHandler(XModelObject object, Properties prop) throws Exception {
        if (!isEnabled(object)) return;
        String type = object.getAttributeValue(ATT_TYPE);
        if(TYPE_LINK.equals(type) &&
           SUBTYPE_CONFIRMED.equals(object.getAttributeValue(ATT_SUBTYPE)) &&
           object.getParent() != null && "true".equals(object.getParent().get("confirmed"))
           ) {
            if(prop == null) prop = new Properties();
            deleteLink.executeHandler(object, prop);
            return;
        }
        if (object instanceof ReferenceObjectImpl) {
            XModelObject ref = ((ReferenceObjectImpl)object).getReference();
            if (ref != null) {
                object = ref;
            }
        }
        XUndoManager undo = object.getModel().getUndoManager();
        XTransactionUndo u = new XTransactionUndo("Unlink " + DefaultCreateHandler.title(object, false), XTransactionUndo.EDIT);
        undo.addUndoable(u);
        long stamp = object.getTimeStamp();
        try {
            if (object.getAttributeValue(ATT_TARGET) != null) {
                object.getModel().changeObjectAttribute(object, ATT_TARGET, "");
            }
            object.getModel().changeObjectAttribute(object, getAttrName(object), "");
            if (stamp == object.getTimeStamp()) undo.rollbackTransactionInProgress();
        } catch (RuntimeException e) {
            undo.rollbackTransactionInProgress();
            throw e;
        } finally {
            u.commit();
        }
    }
    
    public boolean isEnabled(XModelObject object) {
        String type = object.getAttributeValue(ATT_TYPE);
        if(TYPE_LINK.equals(type) &&
           SUBTYPE_CONFIRMED.equals(object.getAttributeValue(ATT_SUBTYPE)) &&
           object.getParent() != null && "true".equals(object.getParent().get("confirmed"))
           ) {
            return super.isEnabled(object);
        }
        if (object instanceof ReferenceObjectImpl) {
            XModelObject ref = ((ReferenceObjectImpl)object).getReference();
            if (ref != null) {
                object = ref;
            }
        }
        if (!super.isEnabled(object)) return false;
        String attr = getAttrName(object);
        if (attr == null) return false;
        String path = object.getAttributeValue(attr);
        return path != null && path.length() > 0;
    }
    
    private String getAttrName(XModelObject object) {
        String entity = object.getModelEntity().getName();
        if (entity.startsWith(ENT_ACTION)) {
            if (object.getAttributeValue(ATT_FORWARD).length() > 0) return ATT_FORWARD;
            if (object.getAttributeValue(ATT_INCLUDE).length() > 0) return ATT_INCLUDE;
            return null;
        } else if (entity.startsWith(ENT_PROCESSITEM)) {
            if (TYPE_ACTION.equals(object.getAttributeValue(ATT_TYPE))) return null;
            if (TYPE_PAGE.equals(object.getAttributeValue(ATT_TYPE))) return null;
        }
        return ATT_PATH;
    }
}


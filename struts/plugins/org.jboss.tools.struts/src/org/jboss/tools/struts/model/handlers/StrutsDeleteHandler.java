/*
 * StrutsDeleteHandler.java
 *
 * Created on February 27, 2003, 9:55 AM
 */

package org.jboss.tools.struts.model.handlers;

import java.util.*;

import org.eclipse.osgi.util.NLS;

import org.jboss.tools.struts.StrutsConstants;
import org.jboss.tools.struts.messages.StrutsUIMessages;
import org.jboss.tools.struts.model.*;
import org.jboss.tools.struts.model.helpers.*;
import org.jboss.tools.common.meta.action.XActionHandler;
import org.jboss.tools.common.meta.action.impl.handlers.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.undo.*;
import org.jboss.tools.struts.model.handlers.page.*;

/**
 *
 * @author  valera
 */
public class StrutsDeleteHandler extends DefaultRemoveHandler implements StrutsConstants {
    static String UNLINK_ACTION = "DeleteActions.Delete"; //"DeleteActions.Unlink"
    String type = "";
    DeletePageLinkHandler deleteLink = new DeletePageLinkHandler();
    DeletePageHandler deletePage = new DeletePageHandler();

    /** Creates a new instance of StrutsDeleteHandler */
    public StrutsDeleteHandler() {
    }

    public void executeHandler(XModelObject object, Properties prop) throws Exception {
    	// Diagram object may be removed by update. 
    	if(object.getParent() == null) return;
		XModelObject process = StrutsProcessStructureHelper.instance.getProcess(object);
        type = object.getAttributeValue(ATT_TYPE);
        String target = object.getAttributeValue(ATT_NAME);
        if (TYPE_LINK.equals(type) &&
           SUBTYPE_CONFIRMED.equals(object.getAttributeValue(ATT_SUBTYPE))) {
            if(prop == null) prop = new Properties();
            deleteLink.executeHandler(object, prop);
            if(prop.getProperty("consumed") != null) return; //$NON-NLS-1$
        } else if (TYPE_PAGE.equals(type)) {
            deletePage.executeHandler(object, prop);
            return;
        }
        List referers = null;
        if (object instanceof ReferenceObjectImpl) {
            XModelObject ref = ((ReferenceObjectImpl)object).getReference();
            if (ENT_PROCESSITEM.equals(object.getModelEntity().getName())) {
                referers = StrutsProcessHelper.getReferers(object.getParent(), target);
            }
            if (ref != null) {
                object = ref;
            }
        } else if (object.getModelEntity().getName().startsWith(ENT_ACTION)) {
            String path = ELM_ACTIONMAP + '/' + object.getPathPart();
            XModelObject action = ((StrutsProcessImpl)process).getHelper().getObject(path);
            if (action != null) {
                referers = StrutsProcessHelper.getReferers(process, action.getPathPart());
            }
        } else if(object.getModelEntity().getName().startsWith(ENT_FORWARD)) {
        	XModelObject forward = SelectOnDiagramHandler.getItemOnProcess(object);
			if (forward != null) {
				referers = StrutsProcessHelper.getReferers(process, forward.getPathPart());
			}
        }
        if (referers != null && referers.size() > 0) {
            ServiceDialog d = object.getModel().getService();
            String msg = NLS.bind(StrutsUIMessages.ACTION_IS_REFERENCED_REMOVE_REFERENCES, object.getPresentationString());
            d.showDialog(StrutsUIMessages.WARNING, msg, new String[]{StrutsUIMessages.OK}, null, ServiceDialog.WARNING);
            return;
        }
        XUndoManager undo = object.getModel().getUndoManager();
        XTransactionUndo u = new XTransactionUndo(StrutsUIMessages.DELETE + DefaultCreateHandler.title(object, false), XTransactionUndo.REMOVE);
        undo.addUndoable(u);
        try {
            if (referers != null && referers.size() > 0) {
                for (int i = 0; i < referers.size(); i++) {
                    XModelObject referer = (XModelObject)referers.get(i);
                    XActionHandler unlink = referer.getModelEntity().getActionList().getAction(UNLINK_ACTION);
                    if (unlink != null && unlink.isEnabled(referer)) {
                        unlink.executeHandler(referer, prop);
                    }
                }
            }
            XModelObject parent = object.getParent();
            if (TYPE_ACTION.equals(type) && SUBTYPE_UNKNOWN.equals(object.getAttributeValue(ATT_SUBTYPE))) {
                removeFromParent(object);
                ((StrutsProcessImpl)parent).getHelper().removeAction(object);
            } else {
                super.executeHandler(object, prop);
            }
        } catch (RuntimeException e) {
            undo.rollbackTransactionInProgress();
            throw e;
        } finally {
            u.commit();
        }
    }
    
//    private boolean isDeletingPage = false;
    
    public boolean isEnabled(XModelObject object) {
    	type = "";
        if(object == null || !object.isActive()) return false;
		type = object.getAttributeValue(ATT_TYPE);
        if (object instanceof ReferenceObjectImpl) {
            XModelObject ref = ((ReferenceObjectImpl)object).getReference();
            if (ENT_PROCESSITEMOUT.equals(object.getModelEntity().getName())) {
                String target = object.getAttributeValue(ATT_NAME);
                if (object.getParent().getAttributeValue(ATT_NAME).equals(target)) return false;
            }
            if (ref == null) {
            } else {
                object = ref;
            }
        }
        return super.isEnabled(object);
    }

    public static void removeFromParent(XModelObject object) {
        XModelObject parent = object.getParent();
    	if(parent == null) return;
        object.removeFromParent();
        XUndoManager undo = object.getModel().getUndoManager();
        if (undo != null) {
            parent.getModel().getUndoManager().addUndoable(new StrutsRemoveUndo(parent, object));
        }
        parent.setModified(true);
    }

	public boolean getSignificantFlag(XModelObject object) {
		// Page has its own confirmation dialog.
		if(TYPE_PAGE.equals(type) && deletePage.getFile(object) != null) return false;
		return true;
	}

}

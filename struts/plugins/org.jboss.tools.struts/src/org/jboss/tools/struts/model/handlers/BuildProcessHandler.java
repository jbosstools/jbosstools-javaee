/*
 * BuildProcessHandler.java
 *
 * Created on February 19, 2003, 6:14 PM
 */

package org.jboss.tools.struts.model.handlers;

import org.jboss.tools.struts.model.helpers.*;
import org.jboss.tools.common.meta.action.impl.handlers.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.undo.*;
import java.util.*;

/**
 *
 * @author  valera
 */
public class BuildProcessHandler extends DefaultCreateHandler {
    
    /** Creates a new instance of BuildProcessHandler */
    public BuildProcessHandler() {
    }
    
    public void executeHandler(XModelObject object, Properties prop) throws Exception {
        XUndoManager undo = object.getModel().getUndoManager();
        XTransactionUndo u = new XTransactionUndo("Build " + DefaultCreateHandler.title(object, false), XTransactionUndo.EDIT);
        undo.addUndoable(u);
        try {
            undo.addUndoable(new XRemoveUndo(object.getParent(), object));
            undo.addUndoable(new XCreateUndo(object.getParent(), object));
            StrutsProcessHelper helper = new StrutsProcessHelper(object);
            helper.build();
            object.setModified(true);
        } catch (Exception e) {
            undo.rollbackTransactionInProgress();
            throw e;
        } finally {
            u.commit();
        }
        //((XModelImpl)object.getModel()).fireStructureChanged(object);//, XModelTreeEvent.STRUCTURE_CHANGED, object);
    }
}

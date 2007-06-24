/*
 * StrutsRemoveUndo.java
 *
 * Created on March 20, 2003, 3:55 PM
 */

package org.jboss.tools.struts.model.handlers;

import org.jboss.tools.struts.model.helpers.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.undo.*;

/**
 *
 * @author  valera
 */
public class StrutsRemoveUndo extends XRemoveUndo {
    
    /** Creates a new instance of StrutsRemoveUndo */
    public StrutsRemoveUndo(XModelObject parent, XModelObject child) {
        super(parent, child);
    }
    
    public void doUndo() {
        //XModelObject parent = model.getByPath(parentpath);
        //if(child != null) parent.addChild(child);
        super.doUndo();
        XModelObject parent = child.getParent();
        if (parent != null) {
            StrutsProcessHelper.getHelper(parent).reloadAction(parent, null, child);
        }
    }

    public void doRedo() {
        /*XModelObject parent = model.getByPath(parentpath);
        if(parent == null) return;
        XModelObject c = parent.getChildByPath(childpath);
        if(c != null) parent.removeChild(c);
        child = c.copy(false);*/
        XModelObject parent = model.getByPath(parentpath);
        super.doRedo();
        if (parent != null) {
            StrutsProcessHelper.getHelper(parent).removeAction(child);
        }
    }
}

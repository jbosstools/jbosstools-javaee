/*
 * StrutsLinkManager.java
 *
 * Created on February 27, 2003, 11:37 AM
 */

package org.jboss.tools.struts.model.handlers;

import org.jboss.tools.struts.model.helpers.*;
import org.jboss.tools.common.meta.XAdoptManager;
import org.jboss.tools.common.model.XModelObject;

/**
 *
 * @author  valera
 */
public class StrutsLinkManager implements XAdoptManager {
    
    private StrutsLinkHelper helper = new StrutsLinkHelper();
    
    /** Creates a new instance of StrutsLinkManager */
    public StrutsLinkManager() {
    }
    
    public void adopt(XModelObject target, XModelObject object, java.util.Properties p) {
        helper.link(target, object, p);
    }
    
    public boolean isAdoptable(XModelObject target, XModelObject object) {
        return helper.canLink(target, object);
    }
    
}

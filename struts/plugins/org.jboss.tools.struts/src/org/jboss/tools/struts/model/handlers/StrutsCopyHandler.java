/*
 * StrutsCopyHandler.java
 *
 * Created on February 27, 2003, 9:55 AM
 */

package org.jboss.tools.struts.model.handlers;

import org.jboss.tools.struts.model.*;
import org.jboss.tools.common.meta.action.impl.handlers.CopyHandler;
import org.jboss.tools.common.model.XModelObject;
import java.util.Properties;

/**
 *
 * @author  valera
 */
public class StrutsCopyHandler extends CopyHandler {
    public static String referentBuffer = null;

    /** Creates a new instance of StrutsCopyHandler */
    public StrutsCopyHandler() {
    }

    public void executeHandler(XModelObject object, Properties prop) throws Exception {
        if (object instanceof ReferenceObjectImpl) {
            referentBuffer = object.getPath();
            XModelObject ref = ((ReferenceObjectImpl)object).getReference();
            if (ref != null) object = ref;
        } else {
            referentBuffer = null;
        }
        super.executeHandler(object, prop);
    }

}

/*
 * ImportWebPrjHandler.java
 *
 * Created on February 12, 2003, 10:40 AM
 */

package org.jboss.tools.struts.webprj.model.handlers;

import org.jboss.tools.common.meta.action.impl.handlers.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.jst.web.project.helpers.NewWebProjectHelper;

import java.util.*;

/**
 *
 * @author  valera
 */
public class ImportWebPrjHandler extends DefaultSpecialHandler {

    private static NewWebProjectHelper helper = new NewWebProjectHelper();

    /** Creates a new instance of ImportWebPrjHandler */
    public ImportWebPrjHandler() {
    }

    public void executeHandler(XModelObject object, Properties p) throws Exception {
        if (p == null) p = new Properties();
        super.executeHandler(object, p);
        if (p.getProperty("canceled") != null) return;
        if (p.getProperty("finished") == null) return;
//        String location = p.getProperty("location");
        if(true) throw new Exception("Method changed");
        helper.createProject(object, /*location,*/ p);
    }

}

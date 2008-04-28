/*
 * StrutsUpdateManager.java
 *
 * Created on February 25, 2003, 10:34 AM
 */

package org.jboss.tools.struts.model.helpers;

import org.jboss.tools.common.model.*;
import org.jboss.tools.jst.web.model.helpers.WebProcessUpdateManager;

public class StrutsUpdateManager extends WebProcessUpdateManager {

    public static synchronized StrutsUpdateManager getInstance(XModel model) {
		StrutsUpdateManager instance = (StrutsUpdateManager)model.getManager("StrutsUpdateManager");
        if (instance == null) {
        	instance = new StrutsUpdateManager();
        	model.addManager("StrutsUpdateManager", instance);
        	model.addModelTreeListener(instance);
        }
        return instance;
    }

}

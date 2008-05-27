package org.jboss.tools.seam.pages.xml.model.helpers;

import org.jboss.tools.common.model.XModel;
import org.jboss.tools.jst.web.model.helpers.WebProcessUpdateManager;

public class SeamPagesUpdateManager extends WebProcessUpdateManager {

    public static synchronized SeamPagesUpdateManager getInstance(XModel model) {
    	SeamPagesUpdateManager instance = (SeamPagesUpdateManager)model.getManager("SeamPagesUpdateManager");
        if (instance == null) {
        	instance = new SeamPagesUpdateManager();
        	model.addManager("SeamPagesUpdateManager", instance);
        	model.addModelTreeListener(instance);
        }
        return instance;
    }

}

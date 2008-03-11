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
package org.jboss.tools.jsf.model.handlers.run;

import java.util.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.meta.action.impl.*;
import org.jboss.tools.jsf.model.JSFConstants;
import org.jboss.tools.jst.web.browser.wtp.RunOnServerContext;

public class RunOnServerHandler extends AbstractHandler implements JSFConstants {
	static RunOnServerContext context = RunOnServerContext.getInstance();
	
	static {
		IPathSourceImpl pathSource = new IPathSourceImpl();
		pathSource.setBrowserContext(context);
		context.addPathSource(pathSource);
	}
	
    public RunOnServerHandler() {}

    public boolean isEnabled(XModelObject object) {
        if(object == null || !object.isActive()) return false;
        if(object == object.getModel().getRoot()) return true;
        return IPathSourceImpl.getPath(object) != null;
    }
    
    public void executeHandler(XModelObject object, Properties p) throws Exception {
        if(!isEnabled(object)) return;
        context.execute(object);
		/*TRIAL_JSF*/
    }

/*TRIAL_JSF_CLASS*/
}

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
package org.jboss.tools.jsf.model.helpers;

import org.jboss.tools.common.model.*;
import org.jboss.tools.jst.web.model.helpers.WebProcessUpdateManager;

public class JSFUpdateManager extends WebProcessUpdateManager {

    public static synchronized JSFUpdateManager getInstance(XModel model) {
		JSFUpdateManager instance = (JSFUpdateManager)model.getManager("JSFUpdateManager"); //$NON-NLS-1$
        if (instance == null) {
        	instance = new JSFUpdateManager();
        	model.addManager("JSFUpdateManager", instance); //$NON-NLS-1$
        	model.addModelTreeListener(instance);
        }
        return instance;
    }

}

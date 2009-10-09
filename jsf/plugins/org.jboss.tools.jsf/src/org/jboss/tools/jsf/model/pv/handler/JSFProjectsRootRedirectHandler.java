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
package org.jboss.tools.jsf.model.pv.handler;

import java.util.Properties;

import org.jboss.tools.common.meta.action.SpecialWizard;
import org.jboss.tools.common.meta.action.impl.handlers.*;
import org.jboss.tools.common.model.XModelException;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.handlers.RemoveModelNatureHandler;
import org.jboss.tools.common.model.util.ModelFeatureFactory;

public class JSFProjectsRootRedirectHandler extends DefaultRedirectHandler {

	protected XModelObject getTrueSource(XModelObject source) {
		return source.getModel().getByPath("FileSystems");
	}

    public void executeHandler(XModelObject object, Properties p) throws XModelException {
    	String c = action.getProperty(RemoveModelNatureHandler.PARAM_CONTRIBUTION);
    	if(c != null) {
    		SpecialWizard w = (SpecialWizard)ModelFeatureFactory.getInstance().createFeatureInstance(c);
    		if(p == null) p = new Properties();
    		if(w != null) p.put(RemoveModelNatureHandler.PARAM_CONTRIBUTION, w);
    	}
    	super.executeHandler(object, p);
    }
}

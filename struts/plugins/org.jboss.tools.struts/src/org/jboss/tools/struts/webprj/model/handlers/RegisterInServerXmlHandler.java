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
package org.jboss.tools.struts.webprj.model.handlers;

import java.util.*;
import org.jboss.tools.common.meta.action.*;
import org.jboss.tools.common.meta.action.impl.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.struts.StrutsProjectUtil;

public class RegisterInServerXmlHandler extends AbstractHandler {

	public boolean isEnabled(XModelObject object) {
		return true;
	}

    public boolean isEnabled(XModelObject object, XModelObject[] objects) {
        if(object != null && (objects == null || objects.length == 1)) return isEnabled(object);
        return false;
    }

    public void executeHandler(XModelObject object, Properties p) throws Exception {
		SpecialWizard wizard = SpecialWizardFactory.createSpecialWizard("org.jboss.tools.jst.web.ui.wizards.appregister.AppRegisterWizard");
		if(p == null) p = new Properties();
		p.setProperty("title", action.getDisplayName());
		p.setProperty("wtp", "true");
		p.put("object", object);
		p.setProperty("natureIndex", StrutsProjectUtil.NATURE_NICK);
		wizard.setObject(p);
		wizard.execute();
	}

}

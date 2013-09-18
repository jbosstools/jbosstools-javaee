/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.ui.internal.handlers;

import java.util.Properties;

import org.jboss.tools.common.meta.action.SpecialWizard;
import org.jboss.tools.common.model.ui.internal.handlers.RemoveModelNatureHandler;
import org.jboss.tools.jsf.model.handlers.RemoveJSFNatureContribution;
import org.jboss.tools.jsf.project.JSFNature;

public class RemoveJSFNatureHandler extends RemoveModelNatureHandler {
	protected String getModelNatureName() {
		return JSFNature.NATURE_ID;
	}

	protected void initProperties(Properties p) {
		super.initProperties(p);
		SpecialWizard w = new RemoveJSFNatureContribution();
		p.put(RemoveModelNatureHandler.PARAM_CONTRIBUTION, w);
	}
	
}

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
package org.jboss.tools.jsf.ui.action;

import java.util.Properties;

import org.jboss.tools.jsf.model.handlers.RemoveJSFNatureContribution;
import org.jboss.tools.jsf.project.JSFNature;
import org.jboss.tools.common.meta.action.SpecialWizard;
import org.jboss.tools.common.model.ui.action.file.RemoveModelNatureActionDelegate;

public class RemoveJSFNatureActionDelegate extends RemoveModelNatureActionDelegate {
	protected String getModelNatureName() {
		return JSFNature.NATURE_ID;
	}

	protected void initProperties(Properties p) {
		super.initProperties(p);
		SpecialWizard w = new RemoveJSFNatureContribution();
		p.put("contribution", w); //$NON-NLS-1$
	}
	
}

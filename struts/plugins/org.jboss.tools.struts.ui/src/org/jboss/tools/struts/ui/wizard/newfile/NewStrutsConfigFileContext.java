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
package org.jboss.tools.struts.ui.wizard.newfile;

import org.jboss.tools.common.meta.action.impl.SpecialWizardSupport;
import org.jboss.tools.common.model.ui.wizard.newfile.*;
import org.jboss.tools.struts.model.handlers.*;

public class NewStrutsConfigFileContext extends NewFileContextEx {
	static String ACTION_PATH_B = "CreateActions.CreateFiles.Struts.CreateStrutsConfig";
	static String ACTION_PATH_0 = ACTION_PATH_B + "10";
	static String ACTION_PATH_1 = ACTION_PATH_B + "11";
	static String ACTION_PATH_2 = ACTION_PATH_B + "12";

	int version = 1; 
	
	NewStrutsConfigFileContext(int version) {
		this.version = version;
		support = createSupport();
	}

	protected String getActionPath() {
		return (version == 1) ? ACTION_PATH_1 : (version == 2) ? ACTION_PATH_2 : ACTION_PATH_0;
	}

	protected SpecialWizardSupport createSupport() {
		if(version == 0) return new CreateStrutsConfig_1_0Support();
		return new CreateStrutsConfigSupport();
	}

}

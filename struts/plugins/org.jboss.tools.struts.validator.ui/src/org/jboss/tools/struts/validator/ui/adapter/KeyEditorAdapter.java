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
package org.jboss.tools.struts.validator.ui.adapter;

import org.jboss.tools.common.model.ui.wizards.query.*;
import org.jboss.tools.common.meta.action.SpecialWizardFactory;

public class KeyEditorAdapter extends DependencyEditorAdapter {
	
	protected AbstractQueryWizard createWizard() {
		return (AbstractQueryWizard)SpecialWizardFactory.createSpecialWizard("org.jboss.tools.struts.validator.ui.wizard.key.SelectKeyWizard");
	}

	protected String _getAttributeName() {
		return "key";
	}

	protected String getHelpKey() {
		return "Wizard_SelectKey";
	}
	
}

/*******************************************************************************
 * Copyright (c) 2010-2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.cdi.bot.test.uiutils.actions;

import org.jboss.tools.cdi.bot.test.CDIConstants;
import org.jboss.tools.cdi.bot.test.annotations.CDIWizardType;
import org.jboss.tools.cdi.bot.test.uiutils.wizards.CDIWizardBase;
import org.jboss.tools.cdi.bot.test.uiutils.wizards.Wizard;

public class NewCDIFileWizard extends NewFileWizardAction {

	private final CDIWizardType type;

	public NewCDIFileWizard(CDIWizardType type) {
		super();
		this.type = type;
	}

	@Override
	public CDIWizardBase run() {
		Wizard w = super.run();
		w.selectTemplate(CDIConstants.CDI_GROUP, type.getAnnotationType());
		w.next();
		return new CDIWizardBase(type);
	}

}

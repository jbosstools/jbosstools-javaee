/*******************************************************************************
 * Copyright (c) 2007-2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.ui.wizard.newfile;

import org.eclipse.jface.wizard.IWizard;
import org.jboss.tools.common.meta.action.impl.SpecialWizardSupport;
import org.jboss.tools.common.model.ui.wizards.standard.DefaultStandardStep;

/**
 * @author mareshkau
 *
 */
public class NewXHTMLWizardSelectTagLibrariesPage extends DefaultStandardStep {

	public NewXHTMLWizardSelectTagLibrariesPage(SpecialWizardSupport support,
			int id) {
		super(support, id);
	}
	@Override
	public void setWizard(IWizard wizard) {
		super.setWizard(wizard);
//		this.wizard = (DefaultStandardWizard)wizard;
	}
	@Override
	public void validate() {
		setPageComplete(true);
	}
}

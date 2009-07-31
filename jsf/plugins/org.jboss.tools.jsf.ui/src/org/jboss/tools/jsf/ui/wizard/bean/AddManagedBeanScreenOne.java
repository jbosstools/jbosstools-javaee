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
package org.jboss.tools.jsf.ui.wizard.bean;

import java.beans.PropertyChangeEvent;
import org.jboss.tools.common.model.ui.IValueProvider;
import org.jboss.tools.common.model.ui.wizards.special.SpecialWizardStep;
import org.jboss.tools.jsf.model.handlers.bean.AddManagedBeanSupport;

public class AddManagedBeanScreenOne extends SpecialWizardStep {
	boolean lock = false;

	public void propertyChange(PropertyChangeEvent event) {
		if(lock) return;
		if(event.getSource() == attributes.getPropertyEditorAdapterByName("managed-bean-class")) { //$NON-NLS-1$
			processClassNameChange((String)event.getNewValue());
		}
		super.propertyChange(event);
	}

	void processClassNameChange(String newValue) {
		lock = true;
		try {
			AddManagedBeanSupport bs = (AddManagedBeanSupport)support;
			String beanName = bs.getDefaultBeanName(newValue);
			IValueProvider vp = attributes.getPropertyEditorAdapterByName("managed-bean-name"); //$NON-NLS-1$
			if(beanName != null) vp.setValue(beanName);
		} finally {
			lock = false;
		}
	}

}

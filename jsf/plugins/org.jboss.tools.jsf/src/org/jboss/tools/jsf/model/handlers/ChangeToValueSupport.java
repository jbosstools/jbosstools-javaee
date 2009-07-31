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
package org.jboss.tools.jsf.model.handlers;

import java.util.*;
import org.jboss.tools.common.meta.action.impl.SpecialWizardSupport;
import org.jboss.tools.common.model.*;

public class ChangeToValueSupport extends SpecialWizardSupport {

	public boolean isEnabled(XModelObject target) {
		if(!super.isEnabled(target)) return false;
		String toKind = action.getProperty("value-kind"); //$NON-NLS-1$
		return ChangeValueKindHandler.isNewValueKind(target, toKind);
	}

	public void reset() {
		if(!ChangeValueKindHandler.checkChangeSignificance(getTarget())) {
			setFinished(true);
		} else {
			String kind = getTarget().getAttributeValue("value-kind"); //$NON-NLS-1$
			String value = (!"value".equals(kind)) ? "" : getTarget().getAttributeValue("value"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			setAttributeValue(0, "value", value); //$NON-NLS-1$
		}
	}

	public void action(String name) throws XModelException {
		if(FINISH.equals(name)) {
			execute();
			setFinished(true);
		} else if(CANCEL.equals(name)) {
			setFinished(true);
		} else if(HELP.equals(name)) {
			help();
		}
	}

	public String[] getActionNames(int stepId) {
		return new String[]{FINISH, CANCEL, HELP};
	}
	
	void execute() throws XModelException {
		Properties p = extractStepData(0);
		getTarget().getModel().changeObjectAttribute(getTarget(), "value-kind", "value"); //$NON-NLS-1$ //$NON-NLS-2$
		getTarget().getModel().changeObjectAttribute(getTarget(), "value", p.getProperty("value")); //$NON-NLS-1$ //$NON-NLS-2$
	}

}

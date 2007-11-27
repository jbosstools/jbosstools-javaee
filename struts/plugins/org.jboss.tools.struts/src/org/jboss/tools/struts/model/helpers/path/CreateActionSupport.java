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
package org.jboss.tools.struts.model.helpers.path;

import java.util.*;
import org.jboss.tools.common.meta.action.impl.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.struts.*;
import org.jboss.tools.struts.model.handlers.*;

public class CreateActionSupport extends SpecialWizardSupport implements StrutsConstants {
	CreateConfigElementHandler handler = new CreateConfigElementHandler();
	
	public void reset() {
		handler.setData(getEntityData());
		handler.setAction(action);
	}

	public boolean isEnabled(XModelObject object) {
		return handler.isEnabled(object);
	}

	public void action(String name) throws Exception {
		if(FINISH.equals(name)) {
			execute();
			setFinished(true);
		} else if(CANCEL.equals(name)) {
			setFinished(true);
		}
	}
	
	public String[] getActionNames(int stepId) {
		return new String[]{FINISH, CANCEL, HELP};
	}

	void execute() throws Exception {
		handler.executeHandler(getTarget(), getProperties());
	}

	private Validator validator = new Validator();
    
	public WizardDataValidator getValidator(int step) {
		validator.setSupport(this, step);
		return validator;    	
	}
	
	class Validator extends DefaultWizardDataValidator {
		public void validate(Properties data) {
			handler.validatePathAttr(data);
			super.validate(data);
		}
	}

}

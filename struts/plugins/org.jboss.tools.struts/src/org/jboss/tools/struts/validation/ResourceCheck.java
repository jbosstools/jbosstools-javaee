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
package org.jboss.tools.struts.validation;

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.validation.ValidationErrorManager;
import org.jboss.tools.struts.model.handlers.OpenMessageResourcesHandler;

public class ResourceCheck extends StrutsConfigControllerCheck {

	public ResourceCheck(ValidationErrorManager manager, String preference) {
		super(manager, preference);
		attr = "parameter"; //$NON-NLS-1$
	}

	public void check(XModelObject object) {
		this.object = object;
		if(!OpenMessageResourcesHandler.isReferencingResourceObject(object)) return;
		if(OpenMessageResourcesHandler.getResourceObject(object) != null) return;
		fireMessage(object, StrutsValidatorMessages.RESOURCE_EXISTS, object.getAttributeValue("parameter")); //$NON-NLS-1$
	}
}

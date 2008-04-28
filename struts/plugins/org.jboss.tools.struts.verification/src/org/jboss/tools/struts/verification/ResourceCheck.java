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
package org.jboss.tools.struts.verification;

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.struts.model.handlers.OpenMessageResourcesHandler;
import org.jboss.tools.common.verification.vrules.*;
import org.jboss.tools.common.verification.vrules.layer.VObjectImpl;

public class ResourceCheck extends StrutsConfigControllerCheck {

	public VResult[] check(VObject object) {
		this.object = object;
		VObjectImpl impl = (VObjectImpl)object;
		XModelObject o = impl.getModelObject();
		if(!OpenMessageResourcesHandler.isReferencingResourceObject(o)) return null;
		if(OpenMessageResourcesHandler.getResourceObject(o) != null) return null;
		return fireParameter();
	}

	protected VResult[] fireParameter() {
		String id = "parameter";
		String attr = "parameter";
		Object[] os = new Object[] {object.getAttribute("parameter")};
		VResult result = rule.getResultFactory().getResult(id, object, attr, object, attr, os);
		return new VResult[] {result};
	}
}

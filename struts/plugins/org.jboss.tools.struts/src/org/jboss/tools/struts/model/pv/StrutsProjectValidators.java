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
package org.jboss.tools.struts.model.pv;

import java.util.*;
import org.jboss.tools.common.model.XModelObject;

public class StrutsProjectValidators extends StrutsProjectPlugin {
	private static final long serialVersionUID = 124643662924235421L;

	protected void collect(XModelObject c, Set<String> uris) {
		super.collect(c, uris);
		if(uris.size() == 0) {
			uris.add("/WEB-INF/validation.xml");
			uris.add("/WEB-INF/validator-rules.xml");
		}
	}
	
	protected String getPathnames(XModelObject c) {
		return getPathnames(c, "org.apache.struts.validator.ValidatorPlugIn", "pathnames");
	}
	
	public XModelObject getTreeParent(XModelObject object) {
		String entity = object.getModelEntity().getName();
		if(!entity.startsWith("FileValidationRules")) return null;
		return (isChild(object)) ? this : null;
	}

}

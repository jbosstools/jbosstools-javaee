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
package org.jboss.tools.struts.model;

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.impl.*;

public class FormBeanObjectImpl extends NamedObjectImpl {
	private static final long serialVersionUID = 1476599707337942701L;

	protected RegularChildren createChildren() {
		return new FormBeanChildren();
	}

}

class FormBeanChildren extends GroupOrderedChildren {

	protected int getGroup(XModelObject o) {
		String entity = o.getModelEntity().getName();
		return (entity.indexOf("Form") >= 0) ? 1 : 0;
	}

}

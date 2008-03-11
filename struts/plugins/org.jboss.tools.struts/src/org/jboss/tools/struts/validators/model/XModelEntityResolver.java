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
package org.jboss.tools.struts.validators.model;

import org.jboss.tools.common.meta.XChild;
import org.jboss.tools.common.model.XModelObject;

public class XModelEntityResolver {
	
	public static String resolveEntity(XModelObject parent, String entityRoot) {
		XChild[] cs = parent.getModelEntity().getChildren();
		for (int i = 0; i < cs.length; i++) {
			String n = cs[i].getName();
			if(n.startsWith(entityRoot)) return n;
		}
		return null;
	}
	
	public static XModelObject[] getResolvedChildren(XModelObject parent, String entityRoot) {
		String entity = resolveEntity(parent, entityRoot);
		if(entity == null) return new XModelObject[0];
		return parent.getChildren(entity);
	}

}

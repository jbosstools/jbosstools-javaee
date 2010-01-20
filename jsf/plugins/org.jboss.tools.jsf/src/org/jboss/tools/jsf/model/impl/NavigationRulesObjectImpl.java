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
package org.jboss.tools.jsf.model.impl;

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.impl.OrderedObjectImpl;
import org.jboss.tools.jsf.model.JSFConstants;
import org.jboss.tools.jsf.model.JSFNavigationModel;

public class NavigationRulesObjectImpl extends OrderedObjectImpl implements JSFConstants {
    private static final long serialVersionUID = 7751479257758800098L;

	public boolean move(int from, int to, boolean firechange) {
		XModelObject[] os = children.getObjects();
		boolean updateRules = false;
		if(from >= 0 && from < os.length && to >= 0 && to < os.length && from != to) {
			updateRules = os[from].getModelEntity().getName().startsWith(ENT_NAVIGATION_RULE);
		}
		boolean b = super.move(from, to, firechange);
		if(b && updateRules) ((JSFNavigationModel)getParent()).updateRuleIndices();
		return b;
	}


}

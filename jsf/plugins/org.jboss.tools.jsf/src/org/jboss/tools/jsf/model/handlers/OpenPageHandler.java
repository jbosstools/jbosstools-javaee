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

import org.jboss.tools.common.meta.action.impl.handlers.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.jsf.model.*;

public class OpenPageHandler extends DefaultRedirectHandler implements JSFConstants {

	protected XModelObject getTrueSource(XModelObject source) {
		String entity = source.getModelEntity().getName();
		String attr = null;
		/*TRIAL_JSF*/
		if(ENT_NAVIGATION_RULE.equals(entity) || ENT_NAVIGATION_RULE_20.equals(entity)) {
			attr = ATT_FROM_VIEW_ID;
		} else if(ENT_PROCESS_GROUP.equals(entity) || 
		          ENT_PROCESS_ITEM.equals(entity) ||
		          ENT_PROCESS_ITEM_OUTPUT.equals(entity)) {
			attr = ATT_PATH;
		} else if(ENT_NAVIGATION_CASE.equals(entity) || ENT_NAVIGATION_CASE_20.equals(entity)) {
			attr = ATT_TO_VIEW_ID;
		}
		if(attr == null) return null;
		String path = source.getAttributeValue(attr);
		if(path == null || path.length() == 0 || path.indexOf('*') >= 0) return null;
		path = path.replace('\\', '/');
		if(path.indexOf('?') >= 0) {
			path = path.substring(0, path.indexOf('?'));
		}
		return (path.startsWith("/")) ? source.getModel().getByPath(path) : null; //$NON-NLS-1$
	}
	/*TRIAL_JSF_CLASS*/
}

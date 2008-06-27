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

import org.jboss.tools.common.model.impl.RegularObjectImpl;
import org.jboss.tools.jsf.model.JSFConstants;

public class NavigationCaseObjectImpl extends RegularObjectImpl implements JSFConstants {
    private static final long serialVersionUID = 7686172293059421195L;

	public String getPresentationString() {
		String action = getAttributeValue(ATT_FROM_ACTION);
		String prefix = (action == null || action.length() == 0) ? "" : action + ":";
		String outcome = getAttributeValue(ATT_FROM_OUTCOME);
		if(outcome.length() == 0) outcome = JSFConstants.EMPTY_NAVIGATION_RULE_NAME;
		return prefix + outcome;		
	}

	public String getPathPart() {
		String path = "" + getAttributeValue(ATT_FROM_OUTCOME) + ":" + getAttributeValue(ATT_FROM_ACTION) + ":" + getAttributeValue(ATT_TO_VIEW_ID);
		return path == null ? ""+System.identityHashCode(this) : path.replace('/', '#');
	}
}

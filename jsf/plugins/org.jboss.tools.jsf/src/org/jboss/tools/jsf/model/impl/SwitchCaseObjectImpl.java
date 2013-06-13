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

public class SwitchCaseObjectImpl extends RegularObjectImpl {
    private static final long serialVersionUID = 1612150606692915698L;

	public String name() {
		String fromOutcome = getAttributeValue("from-outcome");
		String _if = getAttributeValue("if");
		return fromOutcome + ":" + _if;
	}

	public String getPresentationString() {
		String fromOutcome = getAttributeValue("from-outcome");
		String _if = getAttributeValue("if");
		if(fromOutcome.length() > 0) {
			return fromOutcome;
		}
		if(_if.length() > 0) {
			return _if;
		}
		return getAttributeValue("id");
	}

}

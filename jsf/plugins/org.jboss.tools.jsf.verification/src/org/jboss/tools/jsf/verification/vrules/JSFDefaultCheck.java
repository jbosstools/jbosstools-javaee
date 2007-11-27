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
package org.jboss.tools.jsf.verification.vrules;

import org.jboss.tools.common.model.*;
import org.jboss.tools.common.verification.vrules.*;
import org.jboss.tools.common.verification.vrules.layer.VObjectImpl;

public class JSFDefaultCheck implements VAction {
	protected VRule rule;
	protected XModel model;

	public VRule getRule() {
		return rule;
	}

	public void setRule(VRule rule) {
		this.rule = rule;
	}
	
	protected boolean isRelevant(VObject object) {
		return object.getParent() != null;
	}

	public VResult[] check(VObject object) {
		return null;
	}

	protected VResult[] fire(VObject object, String id, String attr, String info) {
		Object[] os = (info == null) ? new Object[] {attr} : new Object[] {attr, info};
		VResult result = rule.getResultFactory().getResult(id, object, attr, object, attr, os);
		return new VResult[] {result};
	}
	
	protected XModel getXModel(VObject object) {
		return ((VObjectImpl)object).getModelObject().getModel();
	}

}

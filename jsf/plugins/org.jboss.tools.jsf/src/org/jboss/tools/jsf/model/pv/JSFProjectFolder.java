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
package org.jboss.tools.jsf.model.pv;

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.impl.RegularObjectImpl;
import org.jboss.tools.jst.web.model.pv.WebProjectNode;

public abstract class JSFProjectFolder extends RegularObjectImpl implements WebProjectNode {
	protected XModelObject[] treeChildren = EMPTY_CHILDREN;
	protected boolean isLoading = false;
	protected boolean valid = false;

	public boolean isChild(XModelObject object) {
		if(treeChildren.length == 0) getTreeChildren();
		for (int i = 0; i < treeChildren.length; i++) {
			  if(treeChildren[i] == object) return true;
			  if(treeChildren[i] instanceof WebProjectNode) {
				  WebProjectNode n = (WebProjectNode)treeChildren[i];
				  if(n.isChild(object)) return true;
			  }
		}
		return false;
	}

	public int getErrorState() {
		XModelObject[] ts = getTreeChildren();
		int es = 0;
		for (int i = 0; i < ts.length && es < 2; i++) {
			int es1 = ts[i].getErrorChildCount() > 0 ? 2 : ts[i].getWarningChildCount() > 0 ? 1 : 0;
			if(es1 > es) es = es1;
			if(ts[i].getErrorState() > es) {
				es = ts[i].getErrorState();
			}
		}
		setErrorState(es);
		return super.getErrorState();
	}

}

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
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.event.*;
import org.jboss.tools.struts.webprj.model.helpers.WebModulesHelper;

public class StrutsProjectModule extends StrutsProjectFolder {
	private static final long serialVersionUID = 7051623793045846498L;
	protected long moduleTimeStamp = -1;
	protected XModelObject module = null;
	
	public XModelObject getModule() {
		return module;
	}
	
	public void setModule(XModelObject module) {
		this.module = module;		
	}

	public void invalidate() {
		if(!valid || isLoading) return;
		long ts = (module == null) ? -1 : module.getTimeStamp();
		if(ts == moduleTimeStamp && areChildrenValid()) return;
		valid = false;
		fireStructureChanged(XModelTreeEvent.STRUCTURE_CHANGED, this);
	}
	
	private boolean areChildrenValid() {
		for (int i = 0; i < treeChildren.length; i++) {
			if(!treeChildren[i].isActive()) return false;
		}
		return true;
	}

	public XModelObject[] getTreeChildren() {
		if(isLoading || valid) return treeChildren;
		isLoading = true;
		valid = true;
		try {
			moduleTimeStamp = (module == null) ? -1 : module.getTimeStamp();
			if(module == null) return treeChildren = EMPTY_CHILDREN;
			WebModulesHelper h = WebModulesHelper.getInstance(getModel());
			XModelObject[] os = h.getConfigsForModule(getModel(), module.getAttributeValue("name"));
			treeChildren = os;
		} finally {
			isLoading = false;
		}
		Arrays.sort(treeChildren, DEFAULT_COMPARATOR);
		return treeChildren;
	}

	public XModelObject getTreeParent(XModelObject object) {
		String entity = object.getModelEntity().getName();
		if(!entity.startsWith("StrutsConfig")) return null;
		return (isChild(object)) ? this : null;
	}
	
}

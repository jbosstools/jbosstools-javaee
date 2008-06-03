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
package org.jboss.tools.struts.model.helpers.open;

import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.util.FindObjectHelper;
import org.jboss.tools.struts.model.pv.*;
import org.jboss.tools.jst.web.model.pv.*;

public class OpenValidator {
	
	public String run(XModel model, String name) {
		if(model == null || name == null) return null;
		XModelObject v = findValidator(model, name);
		if(v == null) return "Cannot find validator " + name + ".";
		FindObjectHelper.findModelObject(v, FindObjectHelper.IN_EDITOR_ONLY);
		return null;
	}
	
	public XModelObject findValidator(XModel model, String name) {
		StrutsProjectsRoot root = StrutsProjectsTree.getProjectsRoot(model);
		if(root == null) return null;
		WebProjectNode n = (WebProjectNode)root.getChildByPath("Validation");
		if(n == null) return null;
		XModelObject[] os = n.getTreeChildren();
		for (int i = 0; i < os.length; i++) {
			XModelObject v = findValidatorInFile(os[i], name);
			if(v != null) return v;
		}
		return null;
	}
	
	private XModelObject findValidatorInFile(XModelObject f, String name) {
		XModelObject[] gs = f.getChildren();
		for (int i = 0; i < gs.length; i++) {
			if(!gs[i].getModelEntity().getName().startsWith("ValidationGlobal")) continue;
			XModelObject v = gs[i].getChildByPath(name);
			if(v == null || !v.getModelEntity().getName().startsWith("ValidationValidator")) continue;
			return v;
		}
		return null;
	}

}

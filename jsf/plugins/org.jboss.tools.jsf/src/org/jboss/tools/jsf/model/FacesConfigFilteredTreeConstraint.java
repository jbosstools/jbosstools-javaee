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
package org.jboss.tools.jsf.model;

import org.jboss.tools.common.model.*;

public class FacesConfigFilteredTreeConstraint implements XFilteredTreeConstraint, JSFConstants {
	boolean onlyHideProcess = true;
	
	public void setEditorEnvironment(boolean b) {
		onlyHideProcess = !b;
	}

	public void update(XModel model) {		
	}
	
	static String HIDING_ALL_CHILDREN_ENTITIES = ".JSFListEntries.";

	public boolean isHidingAllChildren(XModelObject object) {
		String entity = object.getModelEntity().getName();
		return (HIDING_ALL_CHILDREN_ENTITIES.indexOf("." + entity + ".") >= 0);
	}
	
	static String HIDING_SOME_CHILDREN_ENTITIES = "." + ENT_FACESCONFIG_10 + "." + ENT_FACESCONFIG_11 + "." + ENT_FACESCONFIG_12 + "." + "JSFApplication.JSFApplication12."; 

	public boolean isHidingSomeChildren(XModelObject object) {
		String entity = object.getModelEntity().getName();
		return (HIDING_SOME_CHILDREN_ENTITIES.indexOf("." + entity + ".") >= 0);
	}
	
	static String HIDDEN_CHILDREN_ENTITIES = ".JSFProcess.JSFLifecycle.JSFFactory." + 
		"JSFLocaleConfig." +
		"JSFMessageBundle.";

	public boolean accepts(XModelObject object) {
		String entity = object.getModelEntity().getName();
		if(onlyHideProcess) {
			if("JSFProcess".equals(entity)) return false;
		} else {
			if(HIDDEN_CHILDREN_ENTITIES.indexOf("." + entity + ".") >= 0) return false;
		}
		return true;
	}

}
